package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 *
 */
@ChromiumApi("DeviceAccess")
public interface DeviceAccess {

  /**
   * Cancel a prompt in response to a DeviceAccess.deviceRequestPrompted event.
   *
   * @param id
   */
  void cancelPrompt(String id);

  /**
   * Disable events in this domain.
   */
  void disable();

  /**
   * Enable events in this domain.
   */
  void enable();

  /**
   * Select a device in response to a DeviceAccess.deviceRequestPrompted event.
   *
   * @param id       RequestId
   * @param deviceId DeviceId
   */
  void selectPrompt(String id, String deviceId);

  /**
   * 事件
   */
  @Event("DeviceAccess")
  public interface Events {

    /**
     * A device request opened a user prompt to select a device. Respond with the selectPrompt or cancelPrompt command.
     *
     * @param id      RequestId
     * @param devices array[ PromptDevice ]
     */
    @Event("deviceRequestPrompted")
    void deviceRequestPrompted(String id, List<PromptDevice> devices);

  }

  /**
   * Device information displayed in a user prompt to select a device.
   */
  @Data
  public class PromptDevice {
    /**
     * DeviceId
     */
    String id;
    /**
     * Display name as it appears in a device request user prompt.
     */
    String name;
  }

}
