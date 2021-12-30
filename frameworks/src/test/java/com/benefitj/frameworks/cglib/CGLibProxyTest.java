package com.benefitj.frameworks.cglib;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.AttributeMap;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.StackLogger;
import com.benefitj.core.TimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CGLibProxyTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateProxy() {
    Coder coderProxy = CGLibProxy.newProxy(Coder.class, (obj, method, args, proxy) -> {
      String key = method.getDeclaringClass().getSimpleName() + "." + method.getName();
      System.err.println(
          "\n.............. [" + key + "] .............."
              + "\n" + obj.getClass().getSimpleName() + "." + obj.hashCode()
              + "\n" + proxy.getClass().getSimpleName() + "." + proxy.hashCode()
              + "\n.............. [" + key + "] ..............\n"
      );
      return proxy.invokeSuper(obj, args);
    });
    coderProxy.say();
  }

  @Test
  public void testInterface() {
    // 创建代理
    Coder coder = CGLibProxy.newProxy(Coder.class
        , new Class[]{Map.class}
        , new Object[]{new HashMap<>()}
    );
    coder.put("hehe", "呵呵");
    coder.say();
    log.info("coder: {}", coder);
    log.info("coder json: {}", JSON.toJSONString(coder));

    coder.setSource("Hello ???");
    log.info("source: {}", coder.getSource());

    coder.setBirthday(TimeUtils.toDate(1982, 10, 2));
    log.info("age: {}", coder.getAge());


  }

  @Test
  public void testInterfaceWrapper() {

    MapWrapper wrapper = CGLibProxy.newProxy(null
        , new Class<?>[]{MapWrapper.class}
        , new Object[]{new ConcurrentHashMap<>()}
    );

    wrapper.put("key-1", "1");
    wrapper.put("key-2", "2");
    wrapper.put("key-3", "3");

    System.err.println(wrapper.keySet());
    System.err.println(wrapper.values());
    System.err.println("wrapper: " + wrapper);

  }

  @Test
  public void testEventLoop() throws InterruptedException {
    Object[] objects = new Object[]{Executors.newScheduledThreadPool(1)};
    Class[] interfaces = {EventLoop.class, ScheduledExecutorService.class};
    EventLoop eventLoop = CGLibProxy.newProxy(null
        , interfaces
        , (obj, method, args, proxy) -> {
          Class<?> declaringClass = method.getDeclaringClass();
          if (declaringClass.isAssignableFrom(EventLoop.class)
              || declaringClass.isAssignableFrom(ScheduledExecutorService.class)) {
            // 替换参数
            for (int i = 0; i < args.length; i++) {
              Object arg = args[i];
              if (arg instanceof Runnable) {
                args[i] = wrapped((Runnable) arg);
              } else if (arg instanceof Callable) {
                args[i] = wrapped((Callable) arg);
              } else if (arg instanceof Collection) {
                args[i] = wrapped((Collection) arg);
              }
            }
          }

          CGLibMethodInvoker invoker = new CGLibMethodInvoker(obj, method, proxy, interfaces, objects);
          Object returnValue = invoker.invoke(args);
          if (returnValue instanceof ScheduledFuture) {
            return new CancelableScheduledFuture<>((ScheduledFuture<?>) returnValue);
          }
          return returnValue;
        });


    eventLoop.schedule(() -> log.info("爽歪歪...."), 1, TimeUnit.SECONDS);
    eventLoop.schedule(() -> {
      log.info("乖乖，出错了....");
      throw new IllegalStateException("不可知的错误");
    }, 1, TimeUnit.SECONDS);


    ScheduledFuture<Object> future = eventLoop.schedule(() -> {
      log.info("乖乖，出错了....");
      throw new IllegalStateException("不可知的错误");
    }, 2, TimeUnit.SECONDS);

    eventLoop.schedule(() -> {
      // 结束
      if (!future.isDone() && !future.isCancelled()) {
        log.info("取消...., {}", future.getClass().getSimpleName());
        future.cancel(true);
      }
    }, 1, TimeUnit.SECONDS);

    TimeUnit.SECONDS.sleep(3);


  }


  @After
  public void tearDown() throws Exception {
  }


  public static abstract class Coder extends SourceRoot<String> implements IPerson, Map<String, Object> {

    private Date birthday;

    public String say() {
      System.err.println("hello world: " + getClass().getSimpleName() + "\n");
      return "hello world !";
    }

    @Override
    public Date getBirthday() {
      return birthday;
    }

    public void setBirthday(Date birthday) {
      this.birthday = birthday;
    }
  }

  public interface IPerson {

    Date getBirthday();

    default int getAge() {
      return TimeUtils.getAge(getBirthday());
    }

  }


  public interface MapWrapper extends Map<String, Object> {
  }

  public interface EventLoop extends ScheduledExecutorService {

    @Override
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    @Override
    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
  }


  private static final Logger log = StackLogger.getLogger();

  /**
   * 包裹 Runnable
   *
   * @param task 任务
   * @return 返回结果
   */
  static Runnable wrapped(Runnable task) {
    return () -> {
      try {
        task.run();
      } catch (Exception e) {
        log.error("event_loop throws: " + e.getMessage(), e);
        throw e;
      }
    };
  }

  /**
   * 包裹 Callable
   *
   * @param task 任务
   * @param <T>  返回类型
   * @return 返回结果
   */
  static <T> Callable<T> wrapped(Callable<T> task) {
    return () -> {
      try {
        return task.call();
      } catch (Exception e) {
        log.error("event_loop throws: " + e.getMessage(), e);
        throw e;
      }
    };
  }

  /**
   * 包裹 Callable
   *
   * @param tasks 任务
   * @param <T>   返回类型
   * @return 返回结果
   */
  static <T> Collection<? extends Callable<T>> wrapped(Collection<? extends Callable<T>> tasks) {
    return tasks.stream()
        .map(CGLibProxyTest::wrapped)
        .collect(Collectors.toList());
  }


  static class CancelableScheduledFuture<V> implements ScheduledFuture<V>, AttributeMap {

    private final ScheduledFuture<V> original;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public CancelableScheduledFuture(ScheduledFuture<V> original) {
      this.original = original;
    }

    public ScheduledFuture<V> original() {
      return original;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      return original().getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
      return original().compareTo(o);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return original().cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return original().isCancelled();
    }

    @Override
    public boolean isDone() {
      return original().isDone();
    }

    @Override
    public V get() {
      try {
        return original().get();
      } catch (InterruptedException | ExecutionException e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public V get(long timeout, TimeUnit unit) {
      try {
        return original().get(timeout, unit);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public Map<String, Object> attributes() {
      return attributes;
    }
  }

}