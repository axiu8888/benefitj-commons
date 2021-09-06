package com.benefitj.device;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

/**
 * 启动器
 */
public class ExpiredCheckerBootstrap<T extends Device> extends DefaultExpiredChecker<T> {

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
