package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;

/**
 * Inspector Domain
 */
@ChromiumApi("Inspector")
public interface Inspector {

  /**
   * Disables inspector domain notifications.
   */
  void disable();

  /**
   * Enables inspector domain notifications.
   */
  void enable();


  @Event("Inspector")
  public interface Events {
    /**
     * Fired when remote debugging connection is about to be terminated. Contains detach reason.
     *
     * @param reason string
     *               The reason why connection has been terminated.
     */
    @Event("detached")
    void detached(String reason);

    /**
     * Fired when debugging target has crashed
     */
    @Event("targetCrashed")
    void targetCrashed();

    /**
     * Fired when debugging target has reloaded after crash
     */
    @Event("targetReloadedAfterCrash")
    void targetReloadedAfterCrash();

  }
}
