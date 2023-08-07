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
import com.benefitj.jpuppeteer.chromium.Browser;
import com.benefitj.jpuppeteer.chromium.Page;
import com.benefitj.jpuppeteer.chromium.Target;
import lombok.extern.slf4j.Slf4j;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private DevtoolSocket socket = new DevtoolSocket(this);
  /**
   * 当前正在请求的消息
   */
  final Map<Long, Message> messages = new ConcurrentHashMap<>();
  /**
   * 当前结果缓存
   */
  private final ThreadLocal<Message> msgLocal = new ThreadLocal<>();
  /**
   * 浏览器对象
   */
  private Browser browser;
  /**
   * 当前的页面
   */
  private final Map<String, Page> pages = new ConcurrentHashMap<>();

  @Override
  public Browser launch() {
    LauncherOptions opts = getOptions();
    File executableFile = opts.getExecutablePath();
    if (executableFile == null) {
      throw new IllegalStateException("不存在可执行文件!");
    }
    String executablePath = executableFile.getAbsolutePath().replace("\\", "/");
    String userDataDir = StringUtils.getIfBlank(opts.getUserDataDir(), () -> executableFile.getParentFile().getParentFile().getAbsolutePath().replace("\\", "/"));
    opts.setUserDataDir(new File(userDataDir));
    String cmd = executablePath + " " + opts.getArgumentsCommandLine();
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
          wsEndpointRef.set(IOUtils.readFileAsString(txt));
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
    this.browser = newProxy(this, Browser.class);
    return browser;
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

  public Map<String, Page> getPages() {
    return pages;
  }

  public Target newTarget() {
    return newProxy(this, Target.class);
  }

  public Page newPage() {
    return newProxy(this, Page.class);
  }

  /**
   * 获取当前调用的Message
   */
  @Nullable
  public Message getLocalMsg() {
    return msgLocal.get();
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
      if (method.isAnnotationPresent(Event.class)) {
        throw new IllegalStateException("不支持直接调用Event函数: " + method.getName());
      }

      DevtoolSocket socket = chromium.socket;
      if (!socket.isOpen()) {
        socket.reconnect();
        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 2_000) {
          EventLoop.sleep(1, TimeUnit.MILLISECONDS);
        }
        if (!socket.isOpen()) {
          throw new IllegalStateException("socket客户端已断开连接!");
        }
      }
      Message msg = new Message();
      chromium.msgLocal.set(msg);
      msg.setMethod(type.getSimpleName() + "." + method.getName());
      msg.getParams().putAll(ReflectUtils.getParameterValues(method.getParameters(), args));
      msg.setLatch(new CountDownLatch(1));
      log.info("[BEFORE] {}.{}({}), id: {}", type.getSimpleName(), method.getName()
          , (args == null || args.length == 0) ? "" : JSON.toJSONString(msg.getParams())
          , msg.getId()
      );
      chromium.messages.put(msg.getId(), msg);
      socket.send(JSON.toJSONString(msg));
      msg.getLatch().await();
      JSONObject result = msg.getResult();
      if (result != null) {
        Class<?> returnType = method.getReturnType();
        if (returnType != void.class) {
          log.info("[AFTER] {}.{}({}), id: {}, result: {}", type.getSimpleName(), method.getName()
              , (args == null || args.length == 0) ? "" : JSON.toJSONString(msg.getParams())
              , msg.getId()
              , msg.getResult()
          );
          return result.toJavaObject(returnType);
        }
        if (!result.isEmpty()) {
          log.info("出错了? {}", msg);
        }
      }

      Message.Error error = msg.getError();
      if (error != null) {
        throw new IllegalStateException(error.getMessage() + ", " + error.getData());
      }
      return null;
    });
  }

  static class DevtoolSocket extends WebSocketImpl implements WebSocketListener {

    final Chromium chromium;

    public DevtoolSocket(Chromium chromium) {
      this.chromium = chromium;
      setListener(this);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
      log.info("onOpen, code: {}, {}, {}", response.code(), response.message(), CatchUtils.ignore(() -> response.body().string()));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
      log.info("onMessage, text: {}", text);
      JSONObject json = JSON.parseObject(text);
      Long id = json.getLong("id");
      Message msg = chromium.messages.remove(id);
      if (msg != null) {
        msg.setRawResponse(json);
        msg.setResult(json.getJSONObject("result"));
        JSONObject error = json.getJSONObject("error");
        if (error != null) {
          msg.setError(error.toJavaObject(Message.Error.class));
        }
        msg.getLatch().countDown();
      }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
      log.info("onMessage, bytes: {}", HexUtils.bytesToHex(bytes.toByteArray()));
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable e, @Nullable okhttp3.Response response) {
      log.info("onFailure, error: {}, {}, {}", e.getMessage(), response != null ? response.message() : null, CatchUtils.ignore(() -> response.body().string()));
      e.printStackTrace();
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
      log.info("onClosing, code: {}, reason: {}", code, reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
      log.info("onClosed, code: {}, reason: {}", code, reason);
    }
  }

}
