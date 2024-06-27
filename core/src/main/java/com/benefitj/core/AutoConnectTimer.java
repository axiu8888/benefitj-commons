package com.benefitj.core;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自动重连的定时器
 */
public class AutoConnectTimer {

  /**
   * 连接器
   */
  public interface Connector {

    /**
     * 是否已连接
     */
    boolean isConnected();

    /**
     * 连接
     */
    void doConnect();
  }



  private final AtomicReference<ScheduledFuture<?>> timerRef = new AtomicReference<>();
  /**
   * 是否自动重连
   */
  private volatile boolean autoConnect = true;
  /**
   * 尝试间隔
   */
  private Duration interval;
  /**
   * 连接锁
   */
  private final AtomicBoolean lock = new AtomicBoolean(false);

  public AutoConnectTimer() {
    this(true);
  }

  public AutoConnectTimer(boolean autoConnect) {
    this(autoConnect, Duration.ofSeconds(10));
  }

  public AutoConnectTimer(boolean autoConnect, Duration interval) {
    this.autoConnect = autoConnect;
    this.interval = interval;
  }

  /**
   * 重连
   *
   * @param socket 客户端
   */
  public void start(Connector socket) {
    if (isAutoConnect()) {
      if (timerRef.get() != null) return;
      synchronized (this) {
        if (timerRef.get() == null) {
          EventLoop.cancel(this.timerRef.getAndSet(EventLoop.asyncIOFixedRate(() -> {
            if (lock.compareAndSet(false, true)) {
              try {
                if (socket.isConnected()) {
                  EventLoop.cancel(timerRef.getAndSet(null));
                  return;
                }
                socket.doConnect();
              } finally {
                lock.set(false);
              }
            }
          }, 0, getInterval().toMillis(), TimeUnit.MILLISECONDS)));
        }
      }
    }
  }

  public void stop() {
    synchronized (this) {
      EventLoop.cancel(timerRef.getAndSet(null));
    }
  }

  public boolean isAutoConnect() {
    return autoConnect;
  }

  public AutoConnectTimer setAutoConnect(boolean autoConnect) {
    this.autoConnect = autoConnect;
    return this;
  }

  public Duration getInterval() {
    return interval;
  }

  public AutoConnectTimer setInterval(Duration interval) {
    this.interval = interval;
    return this;
  }

  public AutoConnectTimer setAutoConnect(boolean autoConnect, Duration interval) {
    return setAutoConnect(autoConnect).setInterval(interval);
  }

  public static final AutoConnectTimer NONE = new AutoConnectTimer(false) {
    @Override
    public boolean isAutoConnect() {
      return false;
    }

    @Override
    public AutoConnectTimer setAutoConnect(boolean autoConnect) {
      return this;
    }

    @Override
    public AutoConnectTimer setInterval(Duration interval) {
      return this;
    }
  };

}
