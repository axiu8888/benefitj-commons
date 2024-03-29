package com.benefitj.jpuppeteer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.*;
import com.benefitj.core.cmd.Callback;
import com.benefitj.core.cmd.CmdCall;
import com.benefitj.core.cmd.CmdExecutor;
import com.benefitj.http.HttpClient;
import com.benefitj.http.WebSocket;
import com.benefitj.http.WebSocketImpl;
import com.benefitj.http.WebSocketListener;
import com.benefitj.jpuppeteer.chromium.Runtime;
import com.benefitj.jpuppeteer.chromium.*;
import com.google.common.reflect.ClassPath;
import lombok.extern.slf4j.Slf4j;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 浏览器对象
 */
@Slf4j
public class Chromium implements Launcher {

  public static final Pattern WS_ENDPOINT_PATTERN = Pattern.compile("^DevTools listening on (ws://.*)$");
  // 地址缓存的文件
  public static final String WS_ENDPOINT_TXT = "ws-endpoint.txt";

  /**
   * 启动参数
   */
  private LauncherOptions options = new LauncherOptions();
  /**
   * 进程调用
   */
  private CmdCall call;
  /**
   * 连接的WebSocket
   */
  private final DevtoolSocket socket = new DevtoolSocket(this);
  /**
   * 全局处理监听
   */
  private final IntervalInterceptor interceptor = new IntervalInterceptor(this);
  /**
   * 当前正在请求的消息
   */
  final Map<Long, Message> messages = new ConcurrentHashMap<>();
  /**
   * 消息监听
   */
  private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();
  /**
   * 事件监听
   */
  private final Map<String, List<MessageListener>> eventListeners = new ConcurrentHashMap<>();
  /**
   * 当前结果缓存
   */
  private final ThreadLocal<Message> msgLocal = new ThreadLocal<>();
  /**
   * 初始化的对象
   */
  private final Map<Class<? extends ChromiumApi>, Object> apis = new ConcurrentHashMap<>();
  /**
   * 是否已初始化
   */
  private volatile boolean initialized = false;
  /**
   * 会话ID
   */
  private final List<String> sessionIds = new ArrayList<>();

  private final ThreadLocal<String> sessionIdLocal = ThreadLocal.withInitial(() -> !sessionIds.isEmpty() ? sessionIds.get(0) : null);

  public Chromium() {
  }

  public Chromium(LauncherOptions options) {
    this.options = options;
  }

  public LauncherOptions getOptions() {
    return options;
  }

  public void setOptions(LauncherOptions options) {
    this.options = options;
  }

  public CmdCall getCall() {
    return call;
  }

  void addSessionId(String sessionId) {
    if (StringUtils.isNotBlank(sessionId) && !sessionIds.contains(sessionId)) {
      sessionIds.add(sessionId);
    }
  }

  public void setLocalSessionId(String sessionId) {
    this.sessionIdLocal.set(sessionId);
  }

  public String getLocalSessionId() {
    return sessionIdLocal.get();
  }

  protected void loadApis() {
    if (!initialized) {
      synchronized (this) {
        try {
          String packageName = Chromium.class.getPackage().getName() + "." + "chromium";
          ClassPath.from(ClassLoader.getSystemClassLoader())
              .getAllClasses()
              .stream()
              .filter(c -> packageName.equalsIgnoreCase(c.getPackageName()))
              .map(ClassPath.ClassInfo::load)
              .filter(cls -> cls.isAnnotationPresent(ChromiumApi.class) || cls.isAnnotationPresent(Event.class))
              .forEach(cls -> {
                if (cls.isAnnotationPresent(ChromiumApi.class)) {
                  log.trace("load chromium api: {}", cls);
                  Object old = this.apis.put((Class) cls, newProxy(this, cls));
                  if (old != null) {
                    throw new IllegalStateException("存在重复的Chromium接口: " + cls.getAnnotation(ChromiumApi.class).value());
                  }
                } else if (cls.isAnnotationPresent(Event.class)) {
                  Event clsEvent = cls.getAnnotation(Event.class);
                  List<Method> methods = ReflectUtils.getMethods(cls, m -> true);
                  for (Method method : methods) {
                    if (!method.isAnnotationPresent(Event.class)) {
                      throw new IllegalStateException("缺少Event注解: " + method.getDeclaringClass().getName() + "." + method.getName());
                    }
                    Event methodEvent = method.getAnnotation(Event.class);
                    if (StringUtils.isBlank(methodEvent.value())) {
                      throw new IllegalStateException("Event注解[" + method.getDeclaringClass().getName() + "." + method.getName() + "]名称不能为空!");
                    }
                    List<MessageListener> old = eventListeners.put(clsEvent.value() + "." + methodEvent.value(), new CopyOnWriteArrayList<>());
                    if (old != null) {
                      throw new IllegalStateException("存在重复的事件: " + clsEvent.value() + "." + methodEvent.value() + ", " + cls);
                    }
                  }
                }
              });
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  @Override
  public Browser launch() {
    this.loadApis();
    LauncherOptions opts = getOptions();
    File executableFile = opts.getExecutablePath();
    if (executableFile == null || !executableFile.exists()) {
      throw new IllegalStateException("不存在可执行文件!");
    }
    String userDataDir = StringUtils.getIfBlank(opts.getUserDataDir(), () -> executableFile.getParentFile().getParentFile().getAbsolutePath().replace("\\", "/"));
    opts.setUserDataDir(new File(userDataDir));
    String cmd = executableFile.getAbsolutePath().replace("\\", "/") + " " + opts.getArgumentsCommandLine();
    log.trace("startup cmd: {}", cmd);
    final CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<String> wsEndpointRef = new AtomicReference<>();
    EventLoop.asyncIO(() -> CmdExecutor.get().call(cmd, 10_000, new Callback() {
      @Override
      public void onCallBefore(CmdCall call, String command, String[] envp, File dir) {
        Chromium.this.call = call;
      }

      @Override
      public void onMessage(CmdCall call, List<String> lines, String line, boolean error) {
        log.trace("onMessage: {}", line);
        if (latch.getCount() > 0) {
          Matcher matcher = WS_ENDPOINT_PATTERN.matcher(line);
          if (matcher.find()) {
            wsEndpointRef.set(matcher.group(1));
            File txt = new File(opts.getUserDataDir(), WS_ENDPOINT_TXT);
            IOUtils.write(IOUtils.createFile(txt), wsEndpointRef.get().getBytes(StandardCharsets.UTF_8), false);
            latch.countDown();
          }
          if (latch.getCount() > 0 && line.contains("正在现有的浏览器会话中打开")) {
            onWaitForAfter(call.getProcess(), call);
            latch.countDown();
          }
        }
      }

      @Override
      public void onWaitForAfter(Process process, CmdCall call) {
        String url = wsEndpointRef.get();
        File txt = new File(opts.getUserDataDir(), WS_ENDPOINT_TXT);
        if (StringUtils.isBlank(url) && txt.exists() && txt.length() > 0) {
          wsEndpointRef.set(IOUtils.readAsString(txt));
        }
      }
    }));
    CatchUtils.ignore(() -> latch.await(30, TimeUnit.SECONDS));
    String url = wsEndpointRef.get();
    if (StringUtils.isBlank(url)) {
      String error = StringUtils.getIfBlank(call.getMessage(), () -> call.getError());
      throw new IllegalStateException("无法启动Chromium: " + error);
    }
    log.trace("ws endpoint: {}", url);
    HttpClient.newWebSocket(socket, url);
    return getBrowser();
  }

  public void exec(Runnable r) {
    r.run();
  }

  public <T> T getApi(Class<T> type) {
    return (T) apis.get(type);
  }

  /**
   * {@link Browser}
   */
  public Browser getBrowser() {
    return getApi(Browser.class);
  }

  /**
   * {@link Target}
   */
  public Target getTarget() {
    return getApi(Target.class);
  }

  /**
   * {@link Page}
   */
  public Page getPage() {
    return getApi(Page.class);
  }

  /**
   * {@link Runtime}
   */
  public Runtime getRuntime() {
    return getApi(Runtime.class);
  }

  /**
   * {@link Network}
   */
  public Network getNetwork() {
    return getApi(Network.class);
  }

  /**
   * {@link Emulation}
   */
  public Emulation getEmulation() {
    return getApi(Emulation.class);
  }

  /**
   * {@link Performance}
   */
  public Performance getPerformance() {
    return getApi(Performance.class);
  }

  /**
   * {@link Debugger}
   */
  public Debugger getDebugger() {
    return getApi(Debugger.class);
  }

  /**
   * {@link DeviceAccess}
   */
  public DeviceAccess getDeviceAccess() {
    return getApi(DeviceAccess.class);
  }

  /**
   * 获取当前调用的Message
   */
  @Nullable
  public Message getLocalMsg() {
    return msgLocal.get();
  }

  public List<MessageListener> getListeners() {
    return listeners;
  }

  /**
   * 监听消息
   *
   * @param listener 监听
   * @return 返回是否监听
   */
  public boolean register(MessageListener listener) {
    List<MessageListener> list = getListeners();
    if (!list.contains(listener)) {
      list.add(listener);
    }
    return true;
  }

  /**
   * 取消监听
   *
   * @param listener 监听
   * @return 返回是否取消
   */
  public boolean unregister(MessageListener listener) {
    return getListeners().remove(listener);
  }

  /**
   * 一次性监听
   */
  public void once(MessageListener listener) {
    register(new MessageListener.OnceMessageListener(this, listener));
  }

  /**
   * 创建代理对象
   *
   * @param chromium Chromium对象
   * @param type     对象类型：Browser、Page、IO...
   * @param <T>      Chromium的接口
   * @return 返回代理对象
   */
  public static <T> T newProxy(Chromium chromium, Class<T> type) {
    Set<String> methods = new HashSet<>(Arrays.asList("toString", "equals", "hashCode", "notify", "notifyAll", "wait", ""));
    return ProxyUtils.newProxy(type, (proxy, method, args) -> {
      if (methods.contains(method.getName())) {
        return null;
      }
      try {
        if (method.isAnnotationPresent(Event.class)) {
          throw new IllegalStateException("不支持直接调用Event函数: " + method.getName());
        }

        DevtoolSocket socket = chromium.socket;
        if (!socket.isOpen()) {
          socket.reconnect();
          long start = System.currentTimeMillis();
          while ((System.currentTimeMillis() - start) < 2_000) {
            // 让出5毫秒，等待连接成功
            EventLoop.await(5);
            if (socket.isOpen()) {
              break;
            }
          }
          if (!socket.isOpen()) {
            throw new IllegalStateException("socket客户端已断开连接!");
          }
        }
        Message msg = new Message();
        chromium.msgLocal.set(msg);
        msg.setSessionId(chromium.getLocalSessionId());
        msg.setMethod(type.getSimpleName() + "." + method.getName());
        msg.getParams().putAll(ReflectUtils.getParameterValues(method.getParameters(), args));
        msg.setLatch(new CountDownLatch(1));
        log.info("[BEFORE] {}.{}({}), id: {}", type.getSimpleName(), method.getName()
            , (args == null || args.length == 0) ? "" : JSON.toJSONString(msg.getParams())
            , msg.getId()
        );
        chromium.messages.put(msg.getId(), msg);
        socket.send(JSON.toJSONString(msg));
        if (!method.isAnnotationPresent(NoAwait.class)) {
          msg.getLatch().await();
        }
        JSONObject result = msg.getResult();
        if (result != null) {
          Class<?> returnType = method.getReturnType();
          if (returnType != void.class) {
            log.info("[AFTER] {}.{}({}), id: {}, result: {}", type.getSimpleName(), method.getName()
                , (args == null || args.length == 0) ? "" : JSON.toJSONString(msg.getParams())
                , msg.getId()
                , msg.getResult()
            );
            if (JSONObject.class.isAssignableFrom(returnType)) {
              return result;
            }
            return result.toJavaObject(returnType);
          }
          if (!result.isEmpty()) {
            log.info("出错了? {}", msg);
          }
        }

        Message.Error error = msg.getError();
        if (error != null) {
          throw new IllegalStateException(error.getMessage()
              + (StringUtils.isNotBlank(error.getData()) ? ", " + error.getData() : ""));
        }
        return null;
      } finally {
        chromium.sessionIdLocal.remove();
      }
    });
  }

  static class IntervalInterceptor {

    final Chromium chromium;

    public IntervalInterceptor(Chromium chromium) {
      this.chromium = chromium;
    }

    public boolean intercept(String method, JSONObject msg) {
      return false;
    }

  }

  static class DevtoolSocket extends WebSocketImpl implements WebSocketListener {

    final Chromium chromium;

    public DevtoolSocket(Chromium chromium) {
      this.chromium = chromium;
      setListener(this);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
      log.info("[Chromium] onOpen, code: {}, {}, {}", response.code(), response.message(), CatchUtils.ignore(() -> response.body().string()));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
      log.info("[Chromium] onMessage, text: {}", text);
      JSONObject json = JSON.parseObject(text);
      String method = json.getString("method");
      // 优先全局处理
      boolean intercepted = chromium.interceptor.intercept(method, json);
      try {
        Long id = json.getLong("id");
        chromium.addSessionId(json.getString("sessionId"));
        Message msg = chromium.messages.remove(id != null ? id : -1);
        if (msg != null) {
          msg.setRawResponse(json);
          msg.setResult(json.getJSONObject("result"));
          JSONObject error = json.getJSONObject("error");
          if (error != null) {
            msg.setError(error.toJavaObject(Message.Error.class));
          }
          if (msg.getResult() != null) {
            chromium.addSessionId(msg.getResult().getString("sessionId"));
          }
          msg.getLatch().countDown();
        }
      } finally {
        if (!intercepted && StringUtils.isNotBlank(method)) {
          chromium.listeners.forEach(listener -> {
            try {
              listener.onMessage(method, json);
            } catch (Exception e) {
              log.warn("call {}, error: {}", listener.getClass(), e.getMessage());
            }
          });
        }
      }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
      log.info("[Chromium] onMessage, bytes: {}", HexUtils.bytesToHex(bytes.toByteArray()));
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable e, @Nullable okhttp3.Response response) {
      log.info("[Chromium] onFailure, error: {}, {}, {}", e.getMessage(), response != null ? response.message() : null, CatchUtils.ignore(() -> response.body().string()));
      e.printStackTrace();
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
      log.info("[Chromium] onClosing, code: {}, reason: {}", code, reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
      log.info("[Chromium] onClosed, code: {}, reason: {}", code, reason);
    }
  }


}
