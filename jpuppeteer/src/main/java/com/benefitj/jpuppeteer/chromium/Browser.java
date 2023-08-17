package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.jpuppeteer.Event;
import com.benefitj.jpuppeteer.NoAwait;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 浏览器
 * <p>
 * The Browser domain defines methods and events for browser managing.
 */
@ChromiumApi("Browser")
public interface Browser {

  /**
   * Allows a site to use privacy sandbox features that require enrollment without the site actually being enrolled. Only supported on page targets.
   * 允许网站使用需要注册的隐私沙箱功能，而无需实际注册网站。仅在页面目标上支持。
   */
  void addPrivacySandboxEnrollmentOverride(String url);

  /**
   * Close browser gracefully.
   * 优雅地关闭浏览器。
   */
  @NoAwait
  void close();

  /**
   * 返回版本信息。
   * Returns version information.
   *
   * @return {
   * protocolVersion: Protocol version.
   * product:         Product name.
   * revision:        Product revision.
   * userAgent:       User-Agent.
   * jsVersion:       8 version.
   * }
   */
  Version getVersion();

  /**
   * 如果下载正在进行，请取消下载
   * Cancel a download if in progress
   *
   * @param guid             Global unique identifier of the download.
   * @param browserContextId BrowserContext to perform the action in. When omitted, default browser context is used.
   */
  void cancelDownload(String guid, String browserContextId);

  /**
   * 在主线程上导致浏览器崩溃。
   * Crashes browser on the main thread.
   */
  void crash();

  /**
   * GPU进程崩溃。
   * Crashes GPU process.
   */
  void crashGpuProcess();

  /**
   * 调用遥测使用的自定义浏览器命令。
   * Invoke custom browser commands used by telemetry.
   *
   * @param browserCommandId
   */
  void executeBrowserCommand(BrowserCommandId browserCommandId);

  /**
   * 当且仅当命令行上有——enable-automation时，返回浏览器进程的命令行开关。
   * Returns the command line switches for the browser process if, and only if --enable-automation is on the commandline.
   *
   * @param arguments Commandline parameters
   */
  List<String> getBrowserCommandLine(String... arguments);

  /**
   * 按名称获取Chrome直方图。
   * Get a Chrome histogram by name.
   *
   * @param name  Requested histogram name.
   * @param delta If true, retrieve delta since last delta call.
   * @return Histograms.
   */
  Histogram getHistogram(String name, boolean delta);

  /**
   * 获取Chrome直方图。
   * Get Chrome histograms.
   *
   * @param query Requested substring in name. Only histograms which have query as a substring in their name are extracted. An empty or absent query returns all histograms.
   * @param delta If true, retrieve delta since last delta call.
   * @return Histograms.
   */
  List<Histogram> getHistograms(String query, boolean delta);

  /**
   * 获取浏览器窗口的位置和大小。
   * Get position and size of the browser window.
   *
   * @param windowId Browser window id.
   * @return bounds   Bounds information of the window. When window state is 'minimized', the restored window position and size are returned.
   */
  Bounds getWindowBounds(String windowId);

  /**
   * 获取包含devtools目标的浏览器窗口。
   * Get the browser window that contains the devtools target.
   *
   * @param targetId Devtools agent host id. If called as a part of the session, associated targetId is used.
   * @return {
   * windowId: Browser window id.
   * bounds: Bounds information of the window. When window state is 'minimized', the restored window position and size are returned.
   * }
   */
  JSONObject getWindowForTarget(String targetId);

  /**
   * 向给定的源授予特定权限，并拒绝所有其他权限。
   * Grant specific permissions to the given origin and reject all others.
   *
   * @param permissions      Origin the permission applies to, all origins if not specified.
   * @param browserContextId BrowserContext to reset permissions. When omitted, default browser context is used.
   */
  void grantPermissions(List<PermissionType> permissions, String browserContextId);

  /**
   * 重置所有起源的所有权限管理。
   * Reset all permission management for all origins.
   *
   * @param browserContextId BrowserContext to reset permissions. When omitted, default browser context is used.
   */
  void resetPermissions(String browserContextId);

  /**
   * 设置码头平铺细节，特定于平台。
   * Set dock tile details, platform-specific.
   *
   * @param badgeLabel
   * @param image      Png encoded image. (Encoded as a base64 string when passed over JSON)
   */
  void setDockTile(String badgeLabel, String image);

  /**
   * 设置下载文件时的行为。
   * Set the behavior when downloading a file.
   *
   * @param behavior         Whether to allow all or deny all download requests, or use default Chrome behavior if available (otherwise deny). |allowAndName| allows download and names files according to their dowmload guids.
   *                         Allowed Values: deny, allow, allowAndName, default
   * @param browserContextId BrowserContext to set download behavior. When omitted, default browser context is used.
   * @param downloadPath     The default path to save downloaded files to. This is required if behavior is set to 'allow' or 'allowAndName'.
   * @param eventsEnabled    Whether to emit download events (defaults to false).
   */
  void setDownloadBehavior(Behavior behavior, String browserContextId, String downloadPath, boolean eventsEnabled);

  /**
   * 设置给定源的权限设置。
   * Set permission settings for given origin.
   *
   * @param permission       Descriptor of permission to override.
   * @param setting          Setting of the permission.
   * @param origin           Origin the permission applies to, all origins if not specified.
   * @param browserContextId Context to override. When omitted, default browser context is used.
   */
  void setPermission(PermissionDescriptor permission, PermissionSetting setting, String origin, String browserContextId);

  /**
   * 设置浏览器窗口的位置和/或大小。
   * Set position and/or size of the browser window.
   *
   * @param windowId Browser window id.
   * @param bounds   New window bounds. The 'minimized', 'maximized' and 'fullscreen' states cannot be combined with 'left', 'top', 'width' or 'height'. Leaves unspecified fields unchanged.
   */
  void setWindowBounds(String windowId, Bounds bounds);

  /**
   * 事件
   */
  @Event("Browser")
  public interface Events {

    /**
     * Fired when download makes progress. Last call has |done| == true.
     *
     * @param guid          string
     *                      Global unique identifier of the download.
     * @param totalBytes    number
     *                      Total expected bytes to download.
     * @param receivedBytes number
     *                      Total bytes received.
     * @param state         string
     *                      Download status.
     *                      Allowed Values: inProgress, completed, canceled
     */
    @Event("downloadProgress")
    void downloadProgress(String guid, Long totalBytes, Long receivedBytes, String state);

    /**
     * Fired when page is about to start a download.
     *
     * @param frameId           Page.FrameId
     *                          Id of the frame that caused the download to begin.
     * @param guid              string
     *                          Global unique identifier of the download.
     * @param url               string
     *                          URL of the resource being downloaded.
     * @param suggestedFilename string
     *                          Suggested file name of the resource (the actual name of the file saved on disk may differ).
     */
    @Event("downloadWillBegin")
    void downloadWillBegin(String frameId, String guid, String url, String suggestedFilename);

  }
  @Data
  public class Version {
    /**
     * Protocol version.
     */
    String protocolVersion;
    /**
     * Product name.
     */
    String product;
    /**
     * Product revision.
     */
    String revision;
    /**
     * User-Agent.
     */
    String userAgent;
    /**
     * V8 version.
     */
    String jsVersion;
  }

  public enum BrowserCommandId {
    openTabSearch, closeTabSearch
  }

  public enum Behavior {
    @JSONField(name = "deny")
    @JsonProperty(namespace = "deny")
    deny,
    @JSONField(name = "allow")
    @JsonProperty(namespace = "allow")
    allow,
    @JSONField(name = "allowAndName")
    @JsonProperty(namespace = "allowAndName")
    allowAndName,
    @JSONField(name = "default")
    @JsonProperty(namespace = "default")
    __default
  }

  /**
   * 当下载取得进展时触发。最后一次调用已经|done| == true。
   * Fired when download makes progress. Last call has |done| == true.
   */
  @Data
  public class DownloadProgress {
    /**
     * Id of the frame that caused the download to begin.
     */
    String frameId;
    /**
     * Global unique identifier of the download.
     */
    String guid;
    /**
     * URL of the resource being downloaded.
     */
    String url;
    /**
     * Suggested file name of the resource (the actual name of the file saved on disk may differ).
     */
    String suggestedFilename;

  }

  public enum DownloadStatus {
    inProgress, completed, canceled
  }

  /**
   * 当页面即将开始下载时触发。
   * Fired when page is about to start a download.
   */
  @Data
  public class DownloadWillBegin {
    /**
     * Id of the frame that caused the download to begin.
     */
    String frameId;
    /**
     * Global unique identifier of the download.
     */
    String guid;
    /**
     * URL of the resource being downloaded.
     */
    String url;
    /**
     * Suggested file name of the resource (the actual name of the file saved on disk may differ).
     */
    String suggestedFilename;
  }

  /**
   * 浏览器窗口边界信息
   * Browser window bounds information
   */
  @Data
  public class Bounds {
    /**
     * The offset from the left edge of the screen to the window in pixels.
     */
    int left;
    /**
     * The offset from the top edge of the screen to the window in pixels.
     */
    int top;
    /**
     * The window width in pixels.
     */
    int width;
    /**
     * The window height in pixels.
     */
    int height;
    /**
     * The window state. Default to normal.
     */
    WindowState windowState;
  }


  /**
   * 直方图
   */
  @Data
  public class Histogram {
    /**
     * Name.
     */
    String name;
    /**
     * Sum of sample values.
     */
    Integer sum;
    /**
     * Total number of samples.
     */
    Integer count;
    /**
     * Buckets.
     */
    List<Bucket> buckets;
  }

  /**
   * Chrome histogram bucket.
   */
  @Data
  public class Bucket {
    /**
     * Minimum value (inclusive).
     */
    int low;
    /**
     * Maximum value (exclusive).
     */
    int high;
    /**
     * Number of samples.
     */
    int count;
  }


  /**
   * 屏幕状态
   */
  public enum WindowState {
    normal,
    minimized,
    maximized,
    fullscreen

  }

  /**
   * 权限设置
   */
  public enum PermissionSetting {
    granted, denied, prompt
  }

  /**
   * 权限类型
   */
  public enum PermissionType {
    accessibilityEvents,
    audioCapture,
    backgroundSync,
    backgroundFetch,
    clipboardReadWrite,
    clipboardSanitizedWrite,
    displayCapture,
    durableStorage,
    flash,
    geolocation,
    idleDetection,
    localFonts,
    midi,
    midiSysex,
    nfc,
    notifications,
    paymentHandler,
    periodicBackgroundSync,
    protectedMediaIdentifier,
    sensors,
    storageAccess,
    topLevelStorageAccess,
    videoCapture,
    videoCapturePanTiltZoom,
    wakeLockScreen,
    wakeLockSystem,
    windowManagement
  }

  @Data
  public class PermissionDescriptor {
    /**
     * Name of permission. See https://cs.chromium.org/chromium/src/third_party/blink/renderer/modules/permissions/permission_descriptor.idl for valid permission names.
     */
    String name;
    /**
     * For "midi" permission, may also specify sysex control.
     */
    boolean sysex;
    /**
     * For "push" permission, may specify userVisibleOnly. Note that userVisibleOnly = true is the only currently supported type.
     */
    boolean userVisibleOnly;
    /**
     * For "clipboard" permission, may specify allowWithoutSanitization.
     */
    boolean allowWithoutSanitization;
    /**
     * For "camera" permission, may specify panTiltZoom.
     */
    boolean panTiltZoom;
  }

}
