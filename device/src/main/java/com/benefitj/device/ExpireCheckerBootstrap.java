package com.benefitj.device;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.TimeUnit;

/**
 * 启动器
 */
public class ExpireCheckerBootstrap<Id, T extends Device<Id>> extends DefaultExpireChecker<Id, T> {

  /**
   * 创建超时检测
   *
   * @param manager 设备管理器
   * @param <T>     设备类型
   * @return 返回超时检测对象
   */
  public static <Id, T extends Device<Id>> ExpireCheckerBootstrap<Id, T> newInstance(DeviceManager<Id, T> manager) {
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
  public static <Id, T extends Device<Id>> ExpireCheckerBootstrap<Id, T> newInstance(DeviceManager<Id, T> manager, int delay, int timeout, TimeUnit unit) {
    ExpireCheckerBootstrap<Id, T> bootstrap = new ExpireCheckerBootstrap<>(manager);
    bootstrap.setInitialDelay(delay);
    bootstrap.setDelay(delay);
    bootstrap.setTimeout(timeout);
    bootstrap.setUnit(unit);
    return bootstrap;
  }

  public ExpireCheckerBootstrap(DeviceManager<Id, T> deviceManager) {
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
