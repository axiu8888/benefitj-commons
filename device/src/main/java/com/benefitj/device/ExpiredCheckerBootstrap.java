package com.benefitj.device;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.TimeUnit;

/**
 * 启动器
 */
public class ExpiredCheckerBootstrap<T extends Device> extends DefaultExpiredChecker<T> {

  /**
   * 创建超时检测
   *
   * @param manager 设备管理器
   * @param <T>     设备类型
   * @return 返回超时检测对象
   */
  public static <T extends Device> ExpiredCheckerBootstrap<T> newInstance(DeviceManager<T> manager) {
    return newInstance(manager, 1, 60, TimeUnit.SECONDS);
  }

  /**
   * 创建超时检测
   *
   * @param manager 设备管理器
   * @param delay   延迟时间
   * @param timeout 超时时长
   * @param unit    时间单位
   * @param <T>     设备类型
   * @return 返回超时检测对象
   */
  public static <T extends Device> ExpiredCheckerBootstrap<T> newInstance(DeviceManager<T> manager, int delay, int timeout, TimeUnit unit) {
    ExpiredCheckerBootstrap<T> bootstrap = new ExpiredCheckerBootstrap<>(manager);
    bootstrap.setInitialDelay(delay);
    bootstrap.setDelay(delay);
    bootstrap.setTimeout(timeout);
    bootstrap.setUnit(unit);
    return bootstrap;
  }

  public ExpiredCheckerBootstrap(DeviceManager<T> deviceManager) {
    super(deviceManager);
  }

  /**
   * APP启动
   */
  @EventListener
  public void onAppStart(ApplicationReadyEvent event) {
    start();
  }

  /**
   * APP停止
   */
  @EventListener
  public void onAppStop(ContextClosedEvent event) {
    stop();
  }

}
