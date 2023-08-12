package com.benefitj.jpuppeteer.chromium;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * This domain provides experimental commands only supported in headless mode. EXPERIMENTAL
 */
@ChromiumApi("HeadlessExperimental")
public interface HeadlessExperimental {

  /**
   * Sends a BeginFrame to the target and returns when the frame was completed. Optionally captures a screenshot from the
   * resulting frame. Requires that the target was created with enabled BeginFrameControl. Designed for use with
   * --run-all-compositor-stages-before-draw, see also https://goo.gle/chrome-headless-rendering for more background.
   *
   * @param frameTimeTicks   number
   *                         Timestamp of this BeginFrame in Renderer TimeTicks (milliseconds of uptime). If not set, the current time will be used.
   * @param interval         number
   *                         The interval between BeginFrames that is reported to the compositor, in milliseconds. Defaults to a 60 frames/second interval, i.e. about 16.666 milliseconds.
   * @param noDisplayUpdates boolean
   *                         Whether updates should not be committed and drawn onto the display. False by default. If true, only side effects of the BeginFrame will be run, such as layout and animations, but any visual updates may not be visible on the display or in screenshots.
   * @param screenshot       ScreenshotParams
   *                         If set, a screenshot of the frame will be captured and returned in the response. Otherwise, no screenshot will be captured. Note that capturing a screenshot can fail, for example, during renderer initialization. In such a case, no screenshot data will be returned.
   * @return {
   * hasDamage: boolean  Whether the BeginFrame resulted in damage and, thus, a new frame was committed to the display. Reported for diagnostic uses, may be removed in the future.
   * screenshotData: string  Base64-encoded image data of the screenshot, if one was requested and successfully taken. (Encoded as a base64 string when passed over JSON)
   * }
   */
  JSONObject beginFrame(Long frameTimeTicks, Long interval, Boolean noDisplayUpdates, ScreenshotParams screenshot);

  /**
   * Disables headless events for the target.
   */
  void disable();

  /**
   * Enables headless events for the target.
   */
  void enable();

  /**
   * Encoding options for a screenshot.
   */
  @Data
  public class ScreenshotParams {
    /**
     * Image compression format (defaults to png).
     * Allowed Values: jpeg, png, webp
     */
    String format;
    /**
     * Compression quality from range [0..100] (
     * jpeg and webp only).
     */
    Integer quality;
    /**
     * Optimize image encoding for speed, not for resulting size (defaults to false)
     */
    boolean optimizeForSpeed;
  }

}
