package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * This domain emulates different environments for the page.
 */
public interface Emulation {

  /**
   * Tells whether emulation is supported.
   *
   * @param result True if emulation is supported.
   */
  void canEmulate(boolean result);

  /**
   * Clears the overridden device metrics.
   */
  void clearDeviceMetricsOverride();

  /**
   * Clears the overridden Geolocation Position and Error.
   */
  void clearGeolocationOverride();

  /**
   * Sets or clears an override of the default background color of the frame. This override is used if the content does not specify one.
   *
   * @param color RGBA of the default background color. If not specified, any existing override will be cleared.
   */
  void setDefaultBackgroundColorOverride(DOM.RGBA color);


  /**
   * Overrides the values of device screen dimensions (window.screen.width, window.screen.height, window.innerWidth, window.innerHeight, and "device-width"/"device-height"-related CSS media query results).
   *
   * @param width              Overriding width value in pixels (minimum 0, maximum 10000000). 0 disables the override.
   * @param height             Overriding height value in pixels (minimum 0, maximum 10000000). 0 disables the override.
   * @param deviceScaleFactor  Overriding device scale factor value. 0 disables the override.
   * @param mobile             Whether to emulate mobile device. This includes viewport meta tag, overlay scrollbars, text autosizing and more.
   * @param scale              Scale to apply to resulting view image. EXPERIMENTAL
   * @param screenWidth        Overriding screen width value in pixels (minimum 0, maximum 10000000). EXPERIMENTAL
   * @param screenHeight       Overriding screen height value in pixels (minimum 0, maximum 10000000). EXPERIMENTAL
   * @param positionX          Overriding view X position on screen in pixels (minimum 0, maximum 10000000). EXPERIMENTAL
   * @param positionY          Overriding view Y position on screen in pixels (minimum 0, maximum 10000000). EXPERIMENTAL
   * @param dontSetVisibleSize Do not set visible view size, rely upon explicit setVisibleSize call. EXPERIMENTAL
   * @param screenOrientation  Screen orientation override.
   * @param viewport           If set, the visible area of the page will be overridden to this viewport. This viewport change is not observed by the page, e.g. viewport-relative elements do not change positions. EXPERIMENTAL
   * @param displayFeature     If set, the display feature of a multi-segment screen. If not set, multi-segment support is turned-off. EXPERIMENTAL
   */
  void setDeviceMetricsOverride(int width, int height, double deviceScaleFactor, boolean mobile, double scale
      , int screenWidth, int screenHeight, int positionX, int positionY, boolean dontSetVisibleSize, ScreenOrientation screenOrientation, Page.Viewport viewport, DisplayFeature displayFeature);

  /**
   * Overrides the Geolocation Position or Error. Omitting any of the parameters emulates position unavailable.
   *
   * @param latitude  Mock latitude
   * @param longitude Mock longitude
   * @param accuracy  Mock accuracy
   */
  void setGeolocationOverride(double latitude, double longitude, double accuracy);

  /**
   * Switches script execution in the page.
   *
   * @param value Whether script execution should be disabled in the page.
   */
  void setScriptExecutionDisabled(boolean value);

  /**
   * Enables touch on platforms which do not support them.
   *
   * @param venabled       Whether the touch event emulation should be enabled.
   * @param maxTouchPoints Maximum touch points supported. Defaults to one.
   */
  void setTouchEmulationEnabled(boolean venabled, int maxTouchPoints);

  /**
   * Allows overriding user agent with the given string.
   *
   * @param userAgent         User agent to use.
   * @param acceptLanguage    Browser langugage to emulate.
   * @param platform          The platform navigator.platform should return.
   * @param userAgentMetadata To be sent in Sec-CH-UA-* headers and returned in navigator.userAgentData EXPERIMENTAL
   */
  void setUserAgentOverride(String userAgent, String acceptLanguage, String platform, UserAgentMetadata userAgentMetadata);

  /**
   * Emulates the given media type or media feature for CSS media queries.
   *
   * @param media    Media type to emulate. Empty string disables the override.
   * @param features Media features to emulate.
   */
  void setEmulatedMedia(String media, List<MediaFeature> features);

  /**
   * Clears Idle state overrides.
   */
  void clearIdleOverride();

  /**
   * Requests that page scale factor is reset to initial values.
   */
  void resetPageScaleFactor();

  /**
   * Automatically render all web contents using a dark theme.
   *
   * @param enabled Whether to enable or disable automatic dark mode. If not specified, any existing override will be cleared.
   */
  void setAutoDarkModeOverride(boolean enabled);

  /**
   * Allows overriding the automation flag.
   *
   * @param enabled Whether the override should be enabled.
   */
  void setAutomationOverride(boolean enabled);

  /**
   * Enables CPU throttling to emulate slow CPUs.
   *
   * @param rate hrottling rate as a slowdown factor (1 is no throttle, 2 is 2x slowdown, etc).
   */
  void setCPUThrottlingRate(double rate);

  /**
   * @param imageTypes Image types to disable.
   */
  void setDisabledImageTypes(List<DisabledImageType> imageTypes);

  /**
   * @param idisabled Whether document.coookie API should be disabled.
   */
  void setDocumentCookieDisabled(boolean idisabled);

  /**
   * @param enabled       Whether touch emulation based on mouse input should be enabled.
   * @param configuration Touch/gesture events configuration. Default: current platform.
   *                      Allowed Values: mobile, desktop
   */
  void setEmitTouchEventsForMouse(boolean enabled, String configuration);

  /**
   * Emulates the given vision deficiency.
   *
   * @param type Vision deficiency to emulate. Order: best-effort emulations come first, followed by any physiologically accurate emulations for medically recognized color vision deficiencies.
   *             Allowed Values: none, blurredVision, reducedContrast, achromatopsia, deuteranopia, protanopia, tritanopia
   */
  void setEmulatedVisionDeficiency(String type);

  /**
   * Enables or disables simulating a focused and active page.
   *
   * @param enabled Whether to enable to disable focus emulation.
   */
  void setFocusEmulationEnabled(boolean enabled);

  /**
   * @param hardwareConcurrency Hardware concurrency to report
   */
  void setHardwareConcurrencyOverride(int hardwareConcurrency);

  /**
   * Overrides the Idle state.
   *
   * @param isUserActive     Mock isUserActive
   * @param isScreenUnlocked Mock isScreenUnlocked
   */
  void setIdleOverride(boolean isUserActive, boolean isScreenUnlocked);

  /**
   * Overrides default host system locale with the specified one.
   *
   * @param locale ICU style C locale (e.g. "en_US"). If not specified or empty, disables the override and restores default host system locale.
   */
  void setLocaleOverride(String locale);

  /**
   * Sets a specified page scale factor.
   *
   * @param pageScaleFactor Page scale factor.
   */
  void setPageScaleFactor(double pageScaleFactor);

  /**
   * @param hidden Whether scrollbars should be always hidden.
   */
  void setScrollbarsHidden(boolean hidden);

  /**
   * Overrides default host system timezone with the specified one.
   *
   * @param timezoneId The timezone identifier. If empty, disables the override and restores default host system timezone.
   */
  void setTimezoneOverride(String timezoneId);

  /**
   * Turns on virtual time for all frames (replacing real-time with a synthetic time source) and sets the current virtual time policy. Note this supersedes any previous time budget.
   *
   * @param policy
   * @param budget                            If set, after this many virtual milliseconds have elapsed virtual time will be paused and a virtualTimeBudgetExpired event is sent.
   * @param maxVirtualTimeTaskStarvationCount If set this specifies the maximum number of tasks that can be run before virtual is forced forwards to prevent deadlock.
   * @param initialVirtualTime                If set, base::Time::Now will be overridden to initially return this value.
   */
  void setVirtualTimePolicy(VirtualTimePolicy policy, Number budget, int maxVirtualTimeTaskStarvationCount, long initialVirtualTime);

  /**
   * Overrides value returned by the javascript navigator object.
   *
   * @param platform The platform navigator.platform should return.
   */
  void setNavigatorOverrides(String platform);

  /**
   * Resizes the frame/viewport of the page. Note that this does not affect the frame's container (e.g. browser window). Can be used to produce screenshots of the specified size. Not supported on Android.
   *
   * @param width  Frame width (DIP).
   * @param height Frame height (DIP).
   */
  void setVisibleSize(int width, int height);

  /**
   * Notification sent after the virtual time budget for the current VirtualTimePolicy has run out.
   */
  @Event("virtualTimeBudgetExpired")
  void virtualTimeBudgetExpired();

  /**
   * Screen orientation.
   */
  @Data
  public class ScreenOrientation {
    /**
     * Orientation type.
     * Allowed Values: portraitPrimary, portraitSecondary, landscapePrimary, landscapeSecondary
     */
    Orientation type;
    /**
     * Orientation angle.
     */
    Integer angle;
  }

  public enum Orientation {
    portraitPrimary, portraitSecondary, landscapePrimary, landscapeSecondary
  }

  /**
   * Enum of image types that can be disabled.
   * Allowed Values: avif, webp
   */
  public enum DisabledImageType {
    avif, webp
  }

  /**
   * Used to specify User Agent Cient Hints to emulate. See https://wicg.github.io/ua-client-hints
   */
  @Data
  public class UserAgentBrandVersion {
    String brand;
    String version;
  }

  /**
   * Used to specify User Agent Cient Hints to emulate. See https://wicg.github.io/ua-client-hints Missing optional values will be filled in by the target with what it would normally use.
   */
  @Data
  public class UserAgentMetadata {
    /**
     * Brands appearing in Sec-CH-UA.
     */
    List<UserAgentBrandVersion> brands;
    /**
     * Brands appearing in Sec-CH-UA-Full-Version-List.
     */
    List<UserAgentBrandVersion> fullVersionList;
    String fullVersion;
    String platform;
    String platformVersion;
    String architecture;
    String model;
    boolean mobile;
    String bitness;
    boolean wow64;
  }

  @Data
  public class DisplayFeature {
    /**
     * Orientation of a display feature in relation to screen
     * Allowed Values: vertical, horizontal
     */
    String orientation;
    /**
     * The offset from the screen origin in either the x (for vertical orientation) or y (for horizontal orientation) direction.
     */
    Integer offset;
    /**
     * A display feature may mask content such that it is not physically displayed - this length along with the offset describes this area. A display feature that only splits content will have a 0 mask_length.
     */
    Integer maskLength;
  }

  @Data
  public class MediaFeature {
    String name;
    String value;
  }

  /**
   * advance: If the scheduler runs out of immediate work, the virtual time base may fast forward to allow the next delayed task (if any) to run; pause: The virtual time base may not advance; pauseIfNetworkFetchesPending: The virtual time base may not advance if there are any pending resource fetches.
   * Allowed Values: advance, pause, pauseIfNetworkFetchesPending
   */
  public enum VirtualTimePolicy {
    advance, pause, pauseIfNetworkFetchesPending
  }

}
