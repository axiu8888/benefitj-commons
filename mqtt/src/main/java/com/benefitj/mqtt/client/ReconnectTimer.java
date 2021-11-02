package com.benefitj.mqtt.client;

import com.benefitj.core.EventLoop;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自动重连的定时器
 */
public class ReconnectTimer {

  private final AtomicReference<ScheduledFuture<?>> timerRef = new AtomicReference<>();
  /**
   * 尝试间隔
   */
  private int period = 10;
  /**
   * 时间单位
   */
  private TimeUnit unit = TimeUnit.SECONDS;

  /**
   * 是否自动重连
   */
  private volatile boolean autoConnect = true;

  /**
   * 是否已经stop
   */
  private volatile boolean stopped = false;

  public ReconnectTimer() {
    this(true);
  }

  public ReconnectTimer(boolean autoConnect) {
    this.setAutoConnect(autoConnect);
  }

  /**
   * 重连
   *
   * @param client 客户端
   */
  public void start(VertxMqttClient client) {
    if (isAutoConnect()) {
      synchronized (this) {
        if (timerRef.get() == null) {
          this.timerRef.set(EventLoop.io().scheduleAtFixedRate(() -> {
            if (client.isConnected()) {
              ScheduledFuture<?> sf = timerRef.getAndSet(null);
              if (sf != null) {
                sf.cancel(true);
              }
              return;
            }
            client.reconnect();
          }, 1, getPeriod(), getUnit()));
        }
      }
    }
  }

  public void stop() {
    synchronized (this) {
      ScheduledFuture<?> sf = timerRef.getAndSet(null);
      if (sf != null) {
        sf.cancel(true);
      }
    }
  }

  public int getPeriod() {
    return period;
  }

  public ReconnectTimer setPeriod(int period) {
    this.period = period;
    return this;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public ReconnectTimer setUnit(TimeUnit unit) {
    this.unit = unit;
    return this;
  }

  public boolean isAutoConnect() {
    return autoConnect;
  }

  public ReconnectTimer setAutoConnect(boolean autoConnect) {
    this.autoConnect = autoConnect;
    return this;
  }

  protected boolean isStopped() {
    return stopped;
  }

  protected ReconnectTimer setStopped(boolean stopped) {
    this.stopped = stopped;
    return this;
  }
}
