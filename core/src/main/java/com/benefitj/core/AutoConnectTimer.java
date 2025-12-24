package com.benefitj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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


  static final Logger log = LoggerFactory.getLogger(AutoConnectTimer.class);


  private volatile long systemTime = System.currentTimeMillis();

  private final AtomicReference<ScheduledFuture<?>> timer = new AtomicReference<>();
  /**
   * 是否自动重连
   */
  private volatile boolean autoConnect = true;
  /**
   * 尝试间隔
   */
  private Duration interval;
  /**
   * 尝试因子
   */
  private Duration factor;
  /**
   * 最大间隔
   */
  private Duration maxInterval;
  /**
   * 自动计算下一次的间隔
   */
  private volatile Duration nextInterval;
  /**
   * 执行时间
   */
  private final AtomicLong nextTime = new AtomicLong();
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
    this(autoConnect, interval, Duration.ofSeconds(3));
  }

  public AutoConnectTimer(boolean autoConnect, Duration interval, Duration factor) {
    this(autoConnect, interval, factor, Duration.ofMinutes(5));
  }

  public AutoConnectTimer(boolean autoConnect, Duration interval, Duration factor, Duration maxInterval) {
    this.autoConnect = autoConnect;
    this.interval = interval;
    this.factor = factor;
    this.maxInterval = maxInterval;
  }

  /**
   * 重连
   *
   * @param socket 客户端
   */
  public void start(Connector socket) {
    if (isAutoConnect()) {
      if (timer.get() != null) return;
      synchronized (this) {
        if (timer.get() == null) {
          this.nextInterval = null;
          EventLoop.cancel(this.timer.getAndSet(EventLoop.asyncIOFixedRate(() -> {
            if (lock.compareAndSet(false, true)) {
              try {
                if (!isAutoConnect() || socket.isConnected()) {//不需要重连或已经连接了
                  EventLoop.cancel(timer.getAndSet(null));
                  this.nextInterval = null;
                  return;
                }

                long now = System.currentTimeMillis();
                if (nextTime.get() <= now || (systemTime - now) >= 5 * 60_000L) {//5分钟以上的差异就忽略
                  try {
                    socket.doConnect();
                  } catch (Throwable e) {
                    log.warn("[ WARNING ] doConnect --->: \n{}", CatchUtils.getLogStackTrace(e));
                  } finally {
                    systemTime = System.currentTimeMillis();
                    //计算下一次的间隔
                    Duration current = (nextInterval != null ? nextInterval : getInterval());
                    nextInterval = Duration.ofMillis(Math.min(current.toMillis() + getFactor().toMillis(), getMaxInterval().toMillis()));
                    nextTime.set(System.currentTimeMillis() + nextInterval.toMillis());
                  }
                }
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
      EventLoop.cancel(timer.getAndSet(null));
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

  public Duration getFactor() {
    return factor;
  }

  public AutoConnectTimer setFactor(Duration factor) {
    this.factor = factor;
    return this;
  }

  public Duration getMaxInterval() {
    return maxInterval;
  }

  public AutoConnectTimer setMaxInterval(Duration maxInterval) {
    this.maxInterval = maxInterval;
    return this;
  }

  public AutoConnectTimer setAutoConnect(boolean autoConnect, Duration interval) {
    return setAutoConnect(autoConnect, interval, null);
  }

  public AutoConnectTimer setAutoConnect(boolean autoConnect, Duration interval, Duration factor) {
    return setAutoConnect(autoConnect, interval, factor, null);
  }

  public AutoConnectTimer setAutoConnect(boolean autoConnect, Duration interval, Duration factor, Duration maxInterval) {
    return setAutoConnect(autoConnect)
        .setInterval(interval != null ? interval : Duration.ofSeconds(10))
        .setFactor(factor != null ? factor : Duration.ofSeconds(3))
        .setMaxInterval(maxInterval != null ? maxInterval : Duration.ofMinutes(5));
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
