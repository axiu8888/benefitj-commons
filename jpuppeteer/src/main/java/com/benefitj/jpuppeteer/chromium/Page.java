package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.jpuppeteer.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 页面
 */
@ChromiumApi("Page")
public interface Page {

  /**
   * 在创建时(在加载框架的脚本之前)计算每个帧中的给定脚本。
   * Evaluates given script in every frame upon creation (before loading frame's scripts).
   *
   * @param source
   * @param worldName             If specified, creates an isolated world with the given name and evaluates given script in it. This world name will be used as the ExecutionContextDescription::name when the corresponding event is emitted. EXPERIMENTAL
   * @param includeCommandLineAPI Specifies whether command line API should be available to the script, defaults to false. EXPERIMENTAL
   * @param runImmediately        If true, runs the script immediately on existing execution contexts or worlds. Default: false. EXPERIMENTAL
   * @return Identifier of the added script.
   */

  /**
   * 将页面放到最前面(激活选项卡)。
   * Brings page to front (activates tab).
   */
  void bringToFront();

  /**
   * 抓取页面截图。
   * Capture page screenshot.
   *
   * @param format                Image compression format (defaults to png). Allowed Values: jpeg, png, webp
   * @param quality               Compression quality from range [0..100] (jpeg only).
   * @param clip                  Capture the screenshot of a given region only.
   * @param fromSurface           Capture the screenshot from the surface, rather than the view. Defaults to true. EXPERIMENTAL
   * @param captureBeyondViewport Capture the screenshot beyond the viewport. Defaults to false. EXPERIMENTAL
   * @param optimizeForSpeed      Optimize image encoding for speed, not for resulting size (defaults to false) EXPERIMENTAL
   * @return Base64-encoded image data. (Encoded as a base64 string when passed over JSON)
   */
  String captureScreenshot(String format, int quality, Viewport clip, boolean fromSurface, boolean captureBeyondViewport, boolean optimizeForSpeed);

  /**
   * 为给定的帧创建一个孤立的世界。
   * Creates an isolated world for the given frame.
   *
   * @param frameId             Id of the frame in which the isolated world should be created.
   * @param worldName           An optional name which is reported in the Execution Context.
   * @param grantUniveralAccess Whether or not universal access should be granted to the isolated world. This is a powerful option, use with caution.
   * @return Execution context of the isolated world.
   */
  Integer createIsolatedWorld(String frameId, String worldName, boolean grantUniveralAccess);

  /**
   * Disables page domain notifications.
   */
  void disable();

  /**
   * Enables page domain notifications.
   */
  void enable();

  /**
   * {
   * url: string Manifest location.
   * errors: array[ AppManifestError ]
   * data: String Manifest content.
   * parsed: AppManifestParsedProperties  Parsed manifest properties EXPERIMENTAL
   * }
   */
  AppManifest getAppManifest();

  /**
   * Returns present frame tree structure.
   */
  FrameTree getFrameTree();

  /**
   * Returns metrics relating to the layouting of the page, such as viewport bounds/scale.
   */
  LayoutMetrics getLayoutMetrics();

  /**
   * Returns navigation history for the current page.
   * {
   * currentIndex：integer Index of the current navigation history entry.
   * entries: array[ NavigationEntry ] Array of navigation history entries.
   * }
   */
  JSONObject getNavigationHistory();

  /**
   * Accepts or dismisses a JavaScript initiated dialog (alert, confirm, prompt, or onbeforeunload).
   *
   * @param accept     Whether to accept or dismiss the dialog.
   * @param promptText The text to enter into the dialog prompt before accepting. Used only if this is a prompt dialog.
   */
  void handleJavaScriptDialog(boolean accept, String promptText);

  /**
   * Navigates current page to the given URL.
   *
   * @param url            URL to navigate the page to.
   * @param referrer       Referrer URL.
   * @param transitionType Intended transition type.
   * @param frameId        Frame id to navigate, if not specified navigates the top frame.
   * @param referrerPolicy Referrer-policy used for the navigation. EXPERIMENTAL
   * @return {
   * frameId: Frame id that has navigated (or failed to navigate)
   * loaderId: Network.LoaderId  Loader identifier. This is omitted in case of same-document navigation, as the previously committed loaderId would not change.
   * errorText: User friendly error message, present if and only if navigation has failed.
   * }
   */
  JSONObject navigate(String url, String referrer, TransitionType transitionType, String frameId, ReferrerPolicy referrerPolicy);

  /**
   * Navigates current page to the given history entry.
   *
   * @param entryId Unique id of the entry to navigate to.
   */
  void navigateToHistoryEntry(int entryId);

  /**
   * Print page as PDF.
   *
   * @param landscape:           Paper orientation. Defaults to false.
   * @param displayHeaderFooter: Display header and footer. Defaults to false.
   * @param printBackground:     Print background graphics. Defaults to false.
   * @param scale:               Scale of the webpage rendering. Defaults to 1.
   * @param paperWidth:          Paper width in inches. Defaults to 8.5 inches.
   * @param paperHeight:         Paper height in inches. Defaults to 11 inches.
   * @param marginTop:           Top margin in inches. Defaults to 1cm (~0.4 inches).
   * @param marginBottom:        Bottom margin in inches. Defaults to 1cm (~0.4 inches).
   * @param marginLeft:          Left margin in inches. Defaults to 1cm (~0.4 inches).
   * @param marginRight:         Right margin in inches. Defaults to 1cm (~0.4 inches).
   * @param pageRanges:          Paper ranges to print, one based, e.g., '1-5, 8, 11-13'. Pages are printed in the document order, not in the order specified, and no more than once. Defaults to empty string, which implies the entire document is printed. The page numbers are quietly capped to actual page count of the document, and ranges beyond the end of the document are ignored. If this results in no pages to print, an error is reported. It is an error to specify a range with start greater than end.
   * @param headerTemplate:      HTML template for the print header. Should be valid HTML markup with following classes used to inject printing values into them:
   *                             date:                formatted print date
   *                             title:               document title
   *                             url:                 document location
   *                             pageNumber:          current page number
   *                             totalPages:          total pages in the document
   *                             For example, <span class=title></span> would generate span containing the title.
   * @param footerTemplate:      HTML template for the print footer. Should use the same format as the headerTemplate.
   * @param preferCSSPageSize:   Whether or not to prefer page size as defined by css. Defaults to false, in which case the content will be scaled to fit the paper size.
   * @param transferMode:        return as stream Allowed Values: ReturnAsBase64, ReturnAsStream EXPERIMENTAL
   * @param generateTaggedPDF:   Whether or not to generate tagged (accessible) PDF. Defaults to embedder choice. EXPERIMENTAL
   * @return {
   * data: string,  Base64-encoded pdf data. Empty if |returnAsStream| is specified. (Encoded as a base64 string when passed over JSON)
   * stream: IO.StreamHandle, A handle of the stream that holds resulting PDF data. EXPERIMENTAL
   * }
   */
  JSONObject printToPDF(boolean landscape, boolean displayHeaderFooter, boolean printBackground, double scale, double paperWidth, double paperHeight,
                    int marginTop, int marginBottom, int marginLeft, int marginRight, String pageRanges, String headerTemplate,
                    String footerTemplate, boolean preferCSSPageSize, TransferMode transferMode, boolean generateTaggedPDF);

  /**
   * Reloads given page optionally ignoring the cache.
   *
   * @param ignoreCache            If true, browser cache is ignored (as if the user pressed Shift+refresh).
   * @param scriptToEvaluateOnLoad If set, the script will be injected into all frames of the inspected page after reload. Argument will be ignored if reloading dataURL origin.
   */
  void reload(boolean ignoreCache, boolean scriptToEvaluateOnLoad);

  /**
   * Removes given script from the list.
   *
   * @param identifier
   */
  void removeScriptToEvaluateOnNewDocument(String identifier);

  /**
   * 重置当前页面的导航历史记录。
   * Resets navigation history for the current page.
   *
   * @param frameId Frame id to set HTML for.
   * @param html    HTML content to set.
   */
  void resetNavigationHistory(String frameId, String html);

  /**
   * 将给定标记设置为文档的HTML。
   * Sets given markup as the document's HTML.
   *
   * @param frameId Frame id to set HTML for.
   * @param html    HTML content to set.
   */
  void setDocumentContent(String frameId, String html);

  /**
   * 强制页面停止所有导航和挂起的资源获取。
   * Force the page stop all navigations and pending resource fetches.
   */
  void stopLoading();

  /**
   * 清除覆盖的地理位置和错误。
   * Clears the overridden Geolocation Position and Error.
   */
  @Deprecated
  void clearGeolocationOverride();

  /**
   * 覆盖地理位置位置或错误。省略任何参数将模拟位置不可用。
   * Overrides the Geolocation Position or Error. Omitting any of the parameters emulates position unavailable.
   *
   * @param latitude  Mock latitude
   * @param longitude Mock longitude
   * @param accuracy  Mock accuracy
   */
  @Deprecated
  void setGeolocationOverride(double latitude, double longitude, double accuracy);

  /**
   * 给定url的种子编译缓存。编译缓存不能在跨进程导航中存活。
   * Seeds compilation cache for given url. Compilation cache does not survive cross-process navigation.
   *
   * @param url
   * @param data Base64-encoded data (Encoded as a base64 string when passed over JSON)
   */
  void addCompilationCache(String url, String data);

  /**
   * Returns a snapshot of the page as a string. For MHTML format, the serialization includes iframes, shadow DOM, external resources, and element-inline styles.
   *
   * @param format Format (defaults to mhtml). Allowed Values: mhtml
   */
  void captureSnapshot(String format);

  /**
   * Clears seeded compilation cache.
   */
  void clearCompilationCache();

  /**
   * Tries to close page, running its beforeunload hooks, if any.
   */
  void close();

  /**
   * Crashes renderer on the IO thread, generates minidumps.
   */
  void crash();

  /**
   * Generates a report for testing.
   *
   * @param message Message to be displayed in the report.
   * @param group   Specifies the endpoint group to deliver the report to.
   */
  void generateTestReport(String message, String group);

  /**
   * Generates a report for testing.
   *
   * @param frameId .
   * @return {
   * adScriptId: Identifies the bottom-most script which caused the frame to be labelled as an ad. Only sent if frame is labelled as an ad and id is available.
   * }
   */
  JSONObject getAdScriptId(String frameId);

  /**
   * Returns the unique (PWA) app id. Only returns values if the feature flag 'WebAppEnableManifestId' is enabled
   *
   * @return {
   * appId: App id, either from manifest's id attribute or computed from start_url
   * recommendedId: Recommendation for manifest's id attribute to match current id computed from start_url
   * }
   */
  JSONObject getAppId();

  List<InstallabilityError> getInstallabilityErrors();

  /**
   * Get Origin Trials on given frame.
   * {
   * frameId: String
   * originTrials: List<OriginTrial>
   * }
   */
  JSONObject getOriginTrials();

  /**
   * Get Permissions Policy state on given frame.
   */
  List<PermissionsPolicyFeatureState> getPermissionsPolicyState(String frameId);

  /**
   * Returns content of the given resource.
   * {
   * content: Resource content.
   * base64Encoded: True, if content was served as base64.
   * }
   */
  JSONObject getResourceContent(String frameId, String url);

  /**
   * Returns present frame / resource tree structure.
   */
  FrameResourceTree getResourceTree();

  /**
   * Requests backend to produce compilation cache for the specified scripts. scripts are appeneded to the list of scripts for which the cache would be produced.
   * The list may be reset during page navigation. When script with a matching URL is encountered, the cache is optionally produced upon backend discretion,
   * based on internal heuristics. See also: Page.compilationCacheProduced.
   */
  void produceCompilationCache(List<CompilationCacheParams> scripts);

  /**
   * Acknowledges that a screencast frame has been received by the frontend.
   */
  void screencastFrameAck(int sessionId);

  /**
   * Searches for given string in resource content.
   *
   * @param frameId       Frame id for resource to search in.
   * @param url           URL of the resource to search in.
   * @param query         String to search for.
   * @param caseSensitive If true, search is case sensitive.
   * @param isRegex       If true, treats string parameter as regex.
   */
  void searchInResource(String frameId, String url, String query, boolean caseSensitive, boolean isRegex);

  /**
   * Searches for given string in resource content.
   */
  List<Debugger.SearchMatch> searchInResource(int sessionId);

  /**
   * Enable Chrome's experimental ad filter on all sites.
   *
   * @param enabled Whether to block ads.
   */
  void setAdBlockingEnabled(boolean enabled);

  /**
   * Enable page Content Security Policy by-passing.
   *
   * @param enabled Whether to bypass page CSP.
   */
  void setBypassCSP(boolean enabled);

  /**
   * Set generic font families.
   *
   * @param fontFamilies Specifies font families to set. If a font family is not specified, it won't be changed.
   * @param forScripts   array[ ScriptFontFamilies ] Specifies font families to set for individual scripts.
   */
  void setFontFamilies(FontFamilies fontFamilies, List<ScriptFontFamilies> forScripts);

  /**
   * Set default font sizes.
   *
   * @param fontFamilies Specifies font sizes to set. If a font size is not specified, it won't be changed.
   */
  void setFontSizes(FontFamilies fontFamilies);

  /**
   * Intercept file chooser requests and transfer control to protocol clients. When file chooser interception is enabled, native file chooser dialog is not shown. Instead, a protocol event Page.fileChooserOpened is emitted.
   *
   * @param enabled
   */
  void setInterceptFileChooserDialog(boolean enabled);

  /**
   * Controls whether page will emit lifecycle events.
   *
   * @param enabled If true, starts emitting lifecycle events.
   */
  void setLifecycleEventsEnabled(boolean enabled);

  /**
   * Enable/disable prerendering manually.
   * This command is a short-term solution for https://crbug.com/1440085.
   * See https://docs.google.com/document/d/12HVmFxYj5Jc-eJr5OmWsa2bqTJsbgGLKI6ZIyx0_wpA for more details.
   * TODO(https://crbug.com/1440085): Remove this once Puppeteer supports tab targets.
   *
   * @param isAllowed
   */
  void setPrerenderingAllowed(boolean isAllowed);

  /**
   * Extensions for Custom Handlers API: https://html.spec.whatwg.org/multipage/system-state.html#rph-automation
   *
   * @param mode
   */
  void setRPHRegistrationMode(AutoResponseMode mode);

  /**
   * Sets the Secure Payment Confirmation transaction mode. https://w3c.github.io/secure-payment-confirmation/#sctn-automation-set-spc-transaction-mode
   *
   * @param mode
   */
  void setSPCTransactionMode(AutoResponseMode mode);

  /**
   * Tries to update the web lifecycle state of the page. It will transition the page to the given state according to: https://github.com/WICG/web-lifecycle/
   *
   * @param state Target lifecycle state  Allowed Values: frozen, active
   */
  void setWebLifecycleState(String state);

  /**
   * Starts sending each frame using the screencastFrame event.
   *
   * @param format        Image compression format. Allowed Values: jpeg, png
   * @param quality       Compression quality from range [0..100].
   * @param maxWidth      Maximum screenshot width.
   * @param maxHeight     Maximum screenshot height.
   * @param everyNthFrame Send every n-th frame.
   */
  void startScreencast(String format, int quality, int maxWidth, int maxHeight, int everyNthFrame);

  /**
   * Stops sending each frame in the screencastFrame.
   */
  void stopScreencast();

  /**
   * Pauses page execution. Can be resumed using generic Runtime.runIfWaitingForDebugger.
   */
  void waitForDebugger();

  /**
   * Deprecated, please use addScriptToEvaluateOnNewDocument instead.
   */
  @Deprecated
  String addScriptToEvaluateOnLoad(String scriptSource);

  /**
   * Clears the overridden device metrics.
   */
  @Deprecated
  String clearDeviceMetricsOverride();

  /**
   * Clears the overridden Device Orientation.
   */
  @Deprecated
  String clearDeviceOrientationOverride();

  /**
   * Deletes browser cookie with given name, domain and path.
   *
   * @param cookieName Name of the cookie to remove.
   * @param url        URL to match cooke domain and path.
   */
  @Deprecated
  String deleteCookie(String cookieName, String url);

  /**
   * Deletes browser cookie with given name, domain and path.
   *
   * @param cookieName Name of the cookie to remove.
   * @param url        URL to match cooke domain and path.
   */
  @Deprecated
  String getCookies(String cookieName, String url);

  /**
   * Deprecated because it's not guaranteed that the returned icon is in fact the one used for PWA installation.
   */
  @Deprecated
  String getManifestIcons();

  /**
   * Deprecated, please use removeScriptToEvaluateOnNewDocument instead.
   */
  @Deprecated
  void removeScriptToEvaluateOnLoad(String id);

  /**
   * Overrides the values of device screen dimensions (window.screen.width, window.screen.height, window.innerWidth, window.innerHeight, and "device-width"/"device-height"-related CSS media query results).
   *
   * @param width                 Overriding width value in pixels (minimum 0, maximum 10000000). 0 disables the override.
   * @param height                Overriding height value in pixels (minimum 0, maximum 10000000). 0 disables the override.
   * @param deviceScaleFactor     Overriding device scale factor value. 0 disables the override.
   * @param mobile                Whether to emulate mobile device. This includes viewport meta tag, overlay scrollbars, text autosizing and more.
   * @param scale                 Scale to apply to resulting view image.
   * @param screenWidthOverriding screen width value in pixels (minimum 0, maximum 10000000).
   * @param screenHeight          Overriding screen height value in pixels (minimum 0, maximum 10000000).
   * @param positionX             Overriding view X position on screen in pixels (minimum 0, maximum 10000000).
   * @param positionY             Overriding view Y position on screen in pixels (minimum 0, maximum 10000000).
   * @param dontSetVisibleSize    Do not set visible view size, rely upon explicit setVisibleSize call.
   * @param screenOrientation     Screen orientation override.
   * @param viewport              The viewport dimensions and scale. If not set, the override is cleared.
   */
  @Deprecated
  void setDeviceMetricsOverride(int width, int height, double deviceScaleFactor, boolean mobile, double scale, int screenWidthOverriding, int screenHeight, int positionX, int positionY,
                                boolean dontSetVisibleSize, Emulation.ScreenOrientation screenOrientation, Viewport viewport);

  /**
   * Overrides the Device Orientation.
   *
   * @param alpha Mock alpha
   * @param beta  Mock beta
   * @param gamma Mock gamma
   */
  void setDeviceOrientationOverride(double alpha, double beta, double gamma);

  /**
   * Set the behavior when downloading a file.
   *
   * @param behavior     Whether to allow all or deny all download requests, or use default Chrome behavior if available (otherwise deny).
   *                     Allowed Values: deny, allow, default
   * @param downloadPath The default path to save downloaded files to. This is required if behavior is set to 'allow'
   */
  void setDownloadBehavior(String behavior, String downloadPath);

  /**
   * Toggles mouse event-based touch event emulation.
   *
   * @param enabled       Whether the touch event emulation should be enabled.
   * @param configuration Touch/gesture events configuration. Default: current platform.
   *                      Allowed Values: mobile, desktop
   */
  void setTouchEmulationEnabled(boolean enabled, String configuration);

  /**
   * 事件
   */
  @Event("Page")
  public interface Events {

    @Event("domContentEventFired")
    void domContentEventFired(long timestamp);

    /**
     * Emitted only when page.interceptFileChooser is enabled.
     *
     * @param frameId       Id of the frame containing input node. EXPERIMENTAL
     * @param mode          Input mode.
     *                      Allowed Values: selectSingle, selectMultiple
     * @param backendNodeId Input node id. Only present for file choosers opened via an <input type="file"> element. EXPERIMENTAL
     */
    @Event("fileChooserOpened")
    void fileChooserOpened(String frameId, String mode, String backendNodeId);

    /**
     * Fired when frame has been attached to its parent.
     *
     * @param frameId       Id of the frame that has been attached.
     * @param parentFrameId FrameId
     *                      Parent frame identifier.
     * @param stack         Runtime.StackTrace
     *                      JavaScript stack trace of when frame was attached, only set if frame initiated from script.
     */
    @Event("frameAttached")
    void frameAttached(String frameId, String parentFrameId, Runtime.StackTrace stack);

    /**
     * Fired when frame has been detached from its parent.
     *
     * @param frameId FrameId
     *                Id of the frame that has been detached.
     * @param reason  Allowed Values: remove, swap
     *                EXPERIMENTAL
     */
    @Event("frameDetached")
    void frameDetached(String frameId, String reason);

    /**
     * Fired once navigation of the frame has completed. Frame is now associated with the new loader.
     *
     * @param frame Frame object.
     * @param type  EXPERIMENTAL
     */
    @Event("frameNavigated")
    void frameNavigated(Frame frame, NavigationType type);

    /**
     * Fired when interstitial page was hidden
     */
    @Event("interstitialHidden")
    void interstitialHidden();

    /**
     * Fired when interstitial page was shown
     */
    @Event("interstitialShown")
    void interstitialShown();

    /**
     * Fired when a JavaScript initiated dialog (alert, confirm, prompt, or onbeforeunload) has been closed.
     *
     * @param result    Whether dialog was confirmed.
     * @param userInput User input in case of prompt.
     */
    @Event("javascriptDialogClosed")
    void javascriptDialogClosed(boolean result, String userInput);

    /**
     * Fired when a JavaScript initiated dialog (alert, confirm, prompt, or onbeforeunload) is about to open.
     *
     * @param url               Frame url.
     * @param message           Message that will be displayed by the dialog.
     * @param type              Dialog type.
     * @param hasBrowserHandler True iff browser is capable showing or acting on the given dialog. When browser has no dialog handler for given target, calling alert while Page domain is engaged will stall the page execution. Execution can be resumed via calling Page.handleJavaScriptDialog.
     * @param defaultPrompt     Default dialog prompt.
     */
    @Event("javascriptDialogOpening")
    void javascriptDialogOpening(String url, String message, DialogType type, boolean hasBrowserHandler, String defaultPrompt);

    /**
     * Fired for top level page lifecycle events such as navigation, load, paint, etc.
     *
     * @param frameId   Id of the frame.
     * @param loaderId  Loader identifier. Empty string if the request is fetched from worker.
     * @param name
     * @param timestamp Network.MonotonicTime
     */
    @Event("lifecycleEvent")
    void lifecycleEvent(String frameId, String loaderId, String name, Long timestamp);

    /**
     * @param timestamp Network.MonotonicTime
     */
    @Event("loadEventFired")
    void loadEventFired(Long timestamp);

    /**
     * Fired when a new window is going to be opened, via window.open(), link click, form submission, etc.
     *
     * @param url            The URL for the new window.
     * @param windowName     Window name.
     * @param windowFeatures array[ string ]
     *                       An array of enabled window features.
     * @param userGesture    Whether or not it was triggered by user gesture.
     */
    @Event("windowOpen")
    void windowOpen(String url, String windowName, List<String> windowFeatures, boolean userGesture);

    /**
     * Fired when frame no longer has a scheduled navigation.
     *
     * @param frameId Id of the frame that has cleared its scheduled navigation.
     */
    @Event("frameClearedScheduledNavigation")
    void frameClearedScheduledNavigation(String frameId);

    /**
     * Fired when frame schedules a potential navigation.
     *
     * @param frameId Id of the frame that has scheduled a navigation.
     * @param delay   Delay (in seconds) until the navigation is scheduled to begin. The navigation is not guaranteed to start.
     * @param reason  The reason for the navigation.
     * @param url     The destination URL for the scheduled navigation.
     */
    @Event("frameScheduledNavigation")
    void frameScheduledNavigation(String frameId, int delay, ClientNavigationReason reason, String url);

    /**
     * Fired for failed bfcache history navigations if BackForwardCache feature is enabled. Do not assume any ordering with the Page.frameNavigated event. This event is fired only for main-frame history navigation where the document changes (non-same-document navigations), when bfcache navigation fails.
     *
     * @param loaderId                    The loader id for the associated navgation.
     * @param frameId                     The frame id of the associated frame.
     * @param notRestoredExplanations     array[ BackForwardCacheNotRestoredExplanation ]
     *                                    Array of reasons why the page could not be cached. This must not be empty.
     * @param notRestoredExplanationsTree BackForwardCacheNotRestoredExplanationTree
     *                                    Tree structure of reasons why the page could not be cached for each frame.
     */
    @Event("backForwardCacheNotUsed")
    void backForwardCacheNotUsed(String loaderId, String frameId, List<BackForwardCacheNotRestoredExplanation> notRestoredExplanations, List<BackForwardCacheNotRestoredExplanationTree> notRestoredExplanationsTree);

    /**
     * Issued for every compilation cache generated. Is only available if Page.setGenerateCompilationCache is enabled.
     *
     * @param url
     * @param data Base64-encoded data (Encoded as a base64 string when passed over JSON)
     */
    @Event("compilationCacheProduced")
    void compilationCacheProduced(String url, String data);

    /**
     * Fired when opening document to write to.
     *
     * @param frame Frame object.
     */
    @Event("documentOpened")
    void documentOpened(Frame frame);

    /**
     * Fired when a renderer-initiated navigation is requested. Navigation may still be cancelled after the event is issued.
     *
     * @param frameId     FrameId
     *                    Id of the frame that is being navigated.
     * @param reason      ClientNavigationReason
     *                    The reason for the navigation.
     * @param url         The destination URL for the requested navigation.
     * @param disposition ClientNavigationDisposition
     *                    The disposition for the navigation.
     */
    @Event("frameRequestedNavigation")
    void frameRequestedNavigation(String frameId, ClientNavigationReason reason, String url, ClientNavigationDisposition disposition);

    @Event("frameResized")
    void frameResized();

    /**
     * Fired when frame has started loading.
     *
     * @param frameId Id of the frame that has started loading.
     */
    @Event("frameStartedLoading")
    void frameStartedLoading(String frameId);

    /**
     * Fired when frame has stopped loading.
     *
     * @param frameId Id of the frame that has stopped loading.
     */
    @Event("frameStoppedLoading")
    void frameStoppedLoading(String frameId);

    /**
     * Fired when same-document navigation happens, e.g. due to history API usage or anchor navigation.
     *
     * @param frameId Id of the frame.
     * @param url     Frame's new url.
     */
    @Event("navigatedWithinDocument")
    void navigatedWithinDocument(String frameId, String url);

    /**
     * Compressed image data requested by the startScreencast.
     *
     * @param data      Base64-encoded compressed image. (Encoded as a base64 string when passed over JSON)
     * @param metadata  ScreencastFrameMetadata
     *                  Screencast frame metadata.
     * @param sessionId Frame number.
     */
    @Event("screencastFrame")
    void screencastFrame(String data, ScreencastFrameMetadata metadata, String sessionId);

    /**
     * Fired when the page with currently enabled screencast was shown or hidden `.
     *
     * @param visible True if the page is visible.
     */
    @Event("screencastVisibilityChanged")
    void screencastVisibilityChanged(boolean visible);

    /**
     * Fired when download makes progress. Last call has |done| == true. Deprecated. Use Browser.downloadProgress instead.
     *
     * @param guid          Global unique identifier of the download.
     * @param totalBytes    Total expected bytes to download.
     * @param receivedBytes Total bytes received.
     * @param state         Download status.
     *                      Allowed Values: inProgress, completed, canceled
     */
    @Event("downloadProgress")
    void downloadProgress(String guid, long totalBytes, long receivedBytes, String state);

    /**
     * Fired when page is about to start a download. Deprecated. Use Browser.downloadWillBegin instead.
     *
     * @param frameId           Id of the frame that caused download to begin.
     * @param guid              Global unique identifier of the download.
     * @param url               URL of the resource being downloaded.
     * @param suggestedFilename Suggested file name of the resource (the actual name of the file saved on disk may differ).
     *                          Allowed Values: inProgress, completed, canceled
     */
    @Event("downloadWillBegin")
    void downloadWillBegin(String frameId, String guid, String url, String suggestedFilename);

  }


  public enum TransferMode {
    ReturnAsBase64, ReturnAsStream
  }

  @Data
  public class LayoutMetrics {
    /**
     * Deprecated metrics relating to the layout viewport. Is in device pixels. Use cssLayoutViewport instead. DEPRECATED
     */
    @Deprecated
    LayoutViewport layoutViewport;
    /**
     * Deprecated metrics relating to the visual viewport. Is in device pixels. Use cssVisualViewport instead. DEPRECATED
     */
    @Deprecated
    VisualViewport visualViewport;
    /**
     * Deprecated size of scrollable area. Is in DP. Use cssContentSize instead. DEPRECATED
     */
    @Deprecated
    DOM.Rect contentSize;
    /**
     * Metrics relating to the layout viewport in CSS pixels.
     */
    LayoutViewport cssLayoutViewport;
    /**
     * Metrics relating to the visual viewport in CSS pixels.
     */
    VisualViewport cssVisualViewport;
    /**
     * Size of scrollable area in CSS pixels.
     */
    DOM.Rect cssContentSize;
  }

  @Data
  public class AppManifest {
    String url;
    List<AppManifestError> errors;
    String data;
    AppManifestParsedProperties parsed;
  }

  /**
   * Error while paring app manifest.
   * 删除应用程序清单时出错。
   */
  @Data
  public class AppManifestError {
    /**
     * Error message.
     */
    String messag;
    /**
     * If criticial, this is a non-recoverable parse error.
     */
    int critical;
    /**
     * Error line.
     */
    int line;
    /**
     * Error column.
     */
    int column;
  }

  /**
   * Javascript对话框类型。
   * Javascript dialog type.
   * Allowed Values: alert, confirm, prompt, beforeunload
   */
  public enum DialogType {
    alert, confirm, prompt, beforeunload
  }

  /**
   * 页面上有关框架的信息。
   * Information about the Frame on the page.
   */
  @Data
  public class Frame {
    /**
     * Frame unique identifier.
     */
    String id;
    /**
     * Parent frame identifier.
     */
    String parentId;
    /**
     * Network.LoaderId
     * Identifier of the loader associated with this frame.
     */
    String loaderId;
    /**
     * Frame's name as specified in the tag.
     */
    String name;
    /**
     * Frame document's URL without fragment.
     */
    String url;
    /**
     * Frame document's URL fragment including the '#'. EXPERIMENTAL
     */
    String urlFragment;
    /**
     * Frame document's registered domain, taking the public suffixes list into account. Extracted from the Frame's url. Example URLs: http://www.google.com/file.html -> "google.com" http://a.b.co.uk/file.html -> "b.co.uk" EXPERIMENTAL
     */
    String domainAndRegistry;
    /**
     * Frame document's security origin.
     */
    String securityOrigin;
    /**
     * Frame document's mimeType as determined by the browser.
     */
    String mimeType;
    /**
     * If the frame failed to load, this contains the URL that could not be loaded. Note that unlike url above, this URL may contain a fragment. EXPERIMENTAL
     */
    String unreachableUrl;
    /**
     * Indicates whether this frame was tagged as an ad and why. EXPERIMENTAL
     */
    AdFrameStatus adFrameStatus;
    /**
     * Indicates whether the main document is a secure context and explains why that is the case. EXPERIMENTAL
     */
    SecureContextType secureContextType;
    /**
     * Indicates whether this is a cross origin isolated context. EXPERIMENTAL
     */
    CrossOriginIsolatedContextType crossOriginIsolatedContextType;
    /**
     * Indicated which gated APIs / features are available. EXPERIMENTAL
     */
    List<GatedAPIFeatures> gatedAPIFeatures;
  }

  /**
   * 关于帧层次结构的信息。
   * Information about the Frame hierarchy.
   */
  @Data
  public class FrameTree {
    /**
     * Frame information for this tree item.
     */
    Frame frame;
    /**
     * Child frames.
     */
    List<Frame> childFrames;
  }

  /**
   * 布局视窗位置和尺寸。
   * Layout viewport position and dimensions.
   */
  @Data
  public class LayoutViewport {
    /**
     * Horizontal offset relative to the document (CSS pixels).
     */
    int pageX;
    /**
     * Vertical offset relative to the document (CSS pixels).
     */
    int pageY;
    /**
     * Width (CSS pixels), excludes scrollbar if present.
     */
    int clientWidth;
    /**
     * Height (CSS pixels), excludes scrollbar if present.
     */
    int clientHeight;
  }

  /**
   * 导航历史记录条目。
   * Navigation history entry.
   */
  @Data
  public class NavigationEntry {
    /**
     * Unique id of the navigation history entry.
     */
    int id;
    /**
     * URL of the navigation history entry.
     */
    String url;
    /**
     * URL that the user typed in the url bar.
     */
    String userTypedURL;
    /**
     * Title of the navigation history entry.
     */
    String title;
    /**
     * Transition type.
     */
    TransitionType transitionType;
  }

  /**
   * Unique script identifier.
   */
  @Data
  public class ScriptIdentifier implements Serializable {

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private String value;

    public ScriptIdentifier(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  /**
   * Viewport for capturing screenshot.
   */
  @Data
  public class Viewport {
    /**
     * X offset in device independent pixels (dip).
     */
    int x;
    /**
     * Y offset in device independent pixels (dip).
     */
    int y;
    /**
     * Rectangle width in device independent pixels (dip).
     */
    int width;
    /**
     * Rectangle height in device independent pixels (dip).
     */
    int height;
    /**
     * Page scale factor.
     */
    double scale;
  }

  /**
   * 视觉视口位置、尺寸和比例。
   * Visual viewport position, dimensions, and scale.
   */
  @Data
  public class VisualViewport {
    /**
     * Horizontal offset relative to the layout viewport (CSS pixels).
     */
    Integer offsetX;
    /**
     * Vertical offset relative to the layout viewport (CSS pixels).
     */
    Integer offsetY;
    /**
     * Horizontal offset relative to the document (CSS pixels).
     */
    Integer pageX;
    /**
     * Vertical offset relative to the document (CSS pixels).
     */
    Integer pageY;
    /**
     * Width (CSS pixels), excludes scrollbar if present.
     */
    Integer clientWidth;

    /**
     * Height (CSS pixels), excludes scrollbar if present.
     */
    Integer clientHeight;
    /**
     * Scale relative to the ideal viewport (size at width=device-width).
     */
    Double scale;
    /**
     * Page zoom factor (CSS to device independent pixels ratio).
     */
    Double zoom;
  }

  /**
   * 标识导致框架被标记为广告的最底部脚本
   * Identifies the bottom-most script which caused the frame to be labelled as an ad.
   */
  @Data
  public class AdScriptId {
    /**
     * Runtime.ScriptId
     * Script Id of the bottom-most script which caused the frame to be labelled as an ad.
     */
    String scriptId;
    /**
     * Runtime.UniqueDebuggerId
     * Id of adScriptId's debugger.
     */
    String debuggerId;
  }

  /**
   * 解析过的应用清单属性。
   * Parsed app manifest properties.
   */
  @Data
  public class AppManifestParsedProperties {
    /**
     * Computed scope value
     */
    String scope;
  }

  @Data
  public class BackForwardCacheNotRestoredExplanation {
    /**
     * Type of the reason
     */
    BackForwardCacheNotRestoredReasonType type;
    /**
     * Not restored reason
     */
    BackForwardCacheNotRestoredReason reason;
    /**
     * Context associated with the reason. The meaning of this context is dependent on the reason:
     * EmbedderExtensionSentMessageToCachedFrame: the extension ID.
     */
    String context;
  }

  @Data
  public class BackForwardCacheNotRestoredExplanationTree {
    /**
     * URL of each frame
     */
    String url;
    /**
     * Not restored reasons of each frame
     */
    List<BackForwardCacheNotRestoredExplanation> explanations;
    /**
     * Array of children frame
     */
    List<BackForwardCacheNotRestoredExplanationTree> children;
  }

  /**
   * Per-script compilation cache parameters for Page.produceCompilationCache
   */
  @Data
  public class CompilationCacheParams {
    /**
     * The URL of the script to produce a compilation cache entry for.
     */
    String url;
    /**
     * A hint to the backend whether eager compilation is recommended. (the actual compilation mode used is upon backend discretion).
     */
    boolean eager;
  }

  /**
   * Generic font families collection.
   */
  @Data
  public class FontFamilies {
    /**
     * The standard font-family.
     */
    String standard;
    /**
     * The fixed font-family.
     */
    String fixed;
    /**
     * The serif font-family.
     */
    String serif;
    /**
     * The sansSerif font-family.
     */
    String sansSerif;
    /**
     * The cursive font-family.
     */
    String cursive;

    /**
     * The fantasy font-family.
     */
    String fantasy;

    /**
     * The math font-family.
     */
    String math;
  }

  /**
   * 默认字体大小。
   * Default font sizes.
   */
  @Data
  public class FontSizes {
    /**
     * Default standard font size.
     */
    Integer standard;
    /**
     * Default fixed font size.
     */
    Integer fixed;
  }

  /**
   * 页面中显示资源的相关信息。
   * Information about the Resource on the page.
   */
  @Data
  public class FrameResource {
    /**
     * Resource URL.
     */
    String url;
    /**
     * Network.ResourceType
     * Type of this resource.
     */
    String type;
    /**
     * Resource mimeType as determined by the browser.
     */
    String mimeType;
    /**
     * Network.TimeSinceEpoch
     * last-modified timestamp as reported by server.
     */
    long lastModified;
    /**
     * Resource content size.
     */
    Integer contentSize;
    /**
     * True if the resource failed to load.
     */
    boolean failed;
    /**
     * True if the resource was canceled during loading.
     */
    boolean canceled;
  }

  /**
   * 关于Frame层次结构及其缓存资源的信息。
   * Information about the Frame hierarchy along with their cached resources.
   */
  @Data
  public class FrameResourceTree {
    /**
     * Frame information for this tree item.
     */
    Frame frame;
    /**
     * Child frames.
     */
    List<FrameResourceTree> childFrames;
    /**
     * Information about frame resources.
     */
    List<FrameResource> resources;
  }

  /**
   * 安装错误
   * The installability error
   */
  @Data
  public class InstallabilityError {
    /**
     * The error id (e.g. 'manifest-missing-suitable-icon').
     */
    String errorId;
    /**
     * The list of error arguments (e.g. {name:'minimum-icon-size-in-pixels', value:'64'}).
     */
    List<InstallabilityErrorArgument> errorArguments;
  }

  @Data
  public class InstallabilityErrorArgument {
    /**
     * Argument name (e.g. name:'minimum-icon-size-in-pixels').
     */
    String name;
    /**
     * Argument value (e.g. value:'64').
     */
    String value;
  }

  @Data
  public class OriginTrial {
    /**
     * Argument name (e.g. name:'minimum-icon-size-in-pixels').
     */
    String trialName;
    OriginTrialStatus status;
    List<OriginTrialTokenWithStatus> tokensWithStatus;
  }

  @Data
  public class OriginTrialToken {
    /**
     * Argument name (e.g. name:'minimum-icon-size-in-pixels').
     */
    String origin;
    boolean matchSubDomains;
    String trialName;
    /**
     * Network.TimeSinceEpoch
     */
    long expiryTime;
    boolean isThirdParty;
    OriginTrialUsageRestriction usageRestriction;
  }

  @Data
  public class PermissionsPolicyFeatureState {
    PermissionsPolicyFeature feature;
    boolean allowed;
    PermissionsPolicyBlockLocator locator;
  }

  /**
   *
   */
  @Data
  public class PermissionsPolicyBlockLocator {
    /**
     * FrameId
     */
    String frameId;
    PermissionsPolicyBlockReason blockReason;
  }

  /**
   * 截屏帧元数据。
   * Screencast frame metadata.
   */
  @Data
  public class ScreencastFrameMetadata {
    /**
     * Top offset in DIP.
     */
    Number offsetTop;
    /**
     * Page scale factor.
     */
    Number pageScaleFactor;
    /**
     * Device screen width in DIP.
     */
    Number deviceWidth;
    /**
     * Device screen height in DIP.
     */
    Number deviceHeight;
    /**
     * Position of horizontal scroll in CSS pixels.
     */
    Number scrollOffsetX;
    /**
     * Position of vertical scroll in CSS pixels.
     */
    Number scrollOffsetY;
    /**
     * Network.TimeSinceEpoch
     * Frame swap timestamp.
     */
    Long timestamp;
  }

  /**
   * Font families collection for a script.
   */
  @Data
  public class ScriptFontFamilies {
    /**
     * Name of the script which these font families are defined for.
     */
    String script;
    /**
     * Generic font families collection for the script.
     */
    FontFamilies fontFamilies;
  }

  /**
   * Allowed Values: ParentIsAd, CreatedByAdScript, MatchedBlockingRule
   */
  public enum AdFrameStatus {
    ParentIsAd, CreatedByAdScript, MatchedBlockingRule
  }

  /**
   * 指示帧是否被标识为ad。
   * Indicates whether a frame has been identified as an ad.
   * Allowed Values: none, child, root
   */
  public enum AdFrameType {
    none, child, root
  }

  /**
   * Enum of possible auto-reponse for permisison / prompt dialogs.
   * Allowed Values: none, autoAccept, autoReject, autoOptOut
   */
  public enum AutoResponseMode {
    none, autoAccept, autoReject, autoOptOut
  }

  /**
   * Types of not restored reasons for back-forward cache.
   * Allowed Values: SupportPending, PageSupportNeeded, Circumstantial
   */
  public enum BackForwardCacheNotRestoredReasonType {
    SupportPending, PageSupportNeeded, Circumstantial
  }

  /**
   * List of not restored reasons for back-forward cache.
   * Allowed Values: NotPrimaryMainFrame, BackForwardCacheDisabled, RelatedActiveContentsExist, HTTPStatusNotOK, SchemeNotHTTPOrHTTPS, Loading, WasGrantedMediaAccess, DisableForRenderFrameHostCalled, DomainNotAllowed, HTTPMethodNotGET, SubframeIsNavigating, Timeout, CacheLimit, JavaScriptExecution, RendererProcessKilled, RendererProcessCrashed, SchedulerTrackedFeatureUsed, ConflictingBrowsingInstance, CacheFlushed, ServiceWorkerVersionActivation, SessionRestored, ServiceWorkerPostMessage, EnteredBackForwardCacheBeforeServiceWorkerHostAdded, RenderFrameHostReused_SameSite, RenderFrameHostReused_CrossSite, ServiceWorkerClaim, IgnoreEventAndEvict, HaveInnerContents, TimeoutPuttingInCache, BackForwardCacheDisabledByLowMemory, BackForwardCacheDisabledByCommandLine, NetworkRequestDatapipeDrainedAsBytesConsumer, NetworkRequestRedirected, NetworkRequestTimeout, NetworkExceedsBufferLimit, NavigationCancelledWhileRestoring, NotMostRecentNavigationEntry, BackForwardCacheDisabledForPrerender, UserAgentOverrideDiffers, ForegroundCacheLimit, BrowsingInstanceNotSwapped, BackForwardCacheDisabledForDelegate, UnloadHandlerExistsInMainFrame, UnloadHandlerExistsInSubFrame, ServiceWorkerUnregistration, CacheControlNoStore, CacheControlNoStoreCookieModified, CacheControlNoStoreHTTPOnlyCookieModified, NoResponseHead, Unknown, ActivationNavigationsDisallowedForBug1234857, ErrorDocument, FencedFramesEmbedder, CookieDisabled, HTTPAuthRequired, CookieFlushed, WebSocket, WebTransport, WebRTC, MainResourceHasCacheControlNoStore, MainResourceHasCacheControlNoCache, SubresourceHasCacheControlNoStore, SubresourceHasCacheControlNoCache, ContainsPlugins, DocumentLoaded, DedicatedWorkerOrWorklet, OutstandingNetworkRequestOthers, RequestedMIDIPermission, RequestedAudioCapturePermission, RequestedVideoCapturePermission, RequestedBackForwardCacheBlockedSensors, RequestedBackgroundWorkPermission, BroadcastChannel, WebXR, SharedWorker, WebLocks, WebHID, WebShare, RequestedStorageAccessGrant, WebNfc, OutstandingNetworkRequestFetch, OutstandingNetworkRequestXHR, AppBanner, Printing, WebDatabase, PictureInPicture, Portal, SpeechRecognizer, IdleManager, PaymentManager, SpeechSynthesis, KeyboardLock, WebOTPService, OutstandingNetworkRequestDirectSocket, InjectedJavascript, InjectedStyleSheet, KeepaliveRequest, IndexedDBEvent, Dummy, JsNetworkRequestReceivedCacheControlNoStoreResource, WebRTCSticky, WebTransportSticky, WebSocketSticky, ContentSecurityHandler, ContentWebAuthenticationAPI, ContentFileChooser, ContentSerial, ContentFileSystemAccess, ContentMediaDevicesDispatcherHost, ContentWebBluetooth, ContentWebUSB, ContentMediaSessionService, ContentScreenReader, EmbedderPopupBlockerTabHelper, EmbedderSafeBrowsingTriggeredPopupBlocker, EmbedderSafeBrowsingThreatDetails, EmbedderAppBannerManager, EmbedderDomDistillerViewerSource, EmbedderDomDistillerSelfDeletingRequestDelegate, EmbedderOomInterventionTabHelper, EmbedderOfflinePage, EmbedderChromePasswordManagerClientBindCredentialManager, EmbedderPermissionRequestManager, EmbedderModalDialog, EmbedderExtensions, EmbedderExtensionMessaging, EmbedderExtensionMessagingForOpenPort, EmbedderExtensionSentMessageToCachedFrame
   */
  public enum BackForwardCacheNotRestoredReason {
    NotPrimaryMainFrame,
    BackForwardCacheDisabled,
    RelatedActiveContentsExist,
    HTTPStatusNotOK,
    SchemeNotHTTPOrHTTPS,
    Loading,
    WasGrantedMediaAccess,
    DisableForRenderFrameHostCalled,
    DomainNotAllowed,
    HTTPMethodNotGET,
    SubframeIsNavigating,
    Timeout,
    CacheLimit,
    JavaScriptExecution,
    RendererProcessKilled,
    RendererProcessCrashed,
    SchedulerTrackedFeatureUsed,
    ConflictingBrowsingInstance,
    CacheFlushed,
    ServiceWorkerVersionActivation,
    SessionRestored,
    ServiceWorkerPostMessage,
    EnteredBackForwardCacheBeforeServiceWorkerHostAdded,
    RenderFrameHostReused_SameSite,
    RenderFrameHostReused_CrossSite,
    ServiceWorkerClaim,
    IgnoreEventAndEvict,
    HaveInnerContents,
    TimeoutPuttingInCache,
    BackForwardCacheDisabledByLowMemory,
    BackForwardCacheDisabledByCommandLine,
    NetworkRequestDatapipeDrainedAsBytesConsumer,
    NetworkRequestRedirected,
    NetworkRequestTimeout,
    NetworkExceedsBufferLimit,
    NavigationCancelledWhileRestoring,
    NotMostRecentNavigationEntry,
    BackForwardCacheDisabledForPrerender,
    UserAgentOverrideDiffers,
    ForegroundCacheLimit,
    BrowsingInstanceNotSwapped,
    BackForwardCacheDisabledForDelegate,
    UnloadHandlerExistsInMainFrame,
    UnloadHandlerExistsInSubFrame,
    ServiceWorkerUnregistration,
    CacheControlNoStore,
    CacheControlNoStoreCookieModified,
    CacheControlNoStoreHTTPOnlyCookieModified,
    NoResponseHead,
    Unknown,
    ActivationNavigationsDisallowedForBug1234857,
    ErrorDocument,
    FencedFramesEmbedder,
    CookieDisabled,
    HTTPAuthRequired,
    CookieFlushed,
    WebSocket,
    WebTransport,
    WebRTC,
    MainResourceHasCacheControlNoStore,
    MainResourceHasCacheControlNoCache,
    SubresourceHasCacheControlNoStore,
    SubresourceHasCacheControlNoCache,
    ContainsPlugins,
    DocumentLoaded,
    DedicatedWorkerOrWorklet,
    OutstandingNetworkRequestOthers,
    RequestedMIDIPermission,
    RequestedAudioCapturePermission,
    RequestedVideoCapturePermission,
    RequestedBackForwardCacheBlockedSensors,
    RequestedBackgroundWorkPermission,
    BroadcastChannel,
    WebXR,
    SharedWorker,
    WebLocks,
    WebHID,
    WebShare,
    RequestedStorageAccessGrant,
    WebNfc,
    OutstandingNetworkRequestFetch,
    OutstandingNetworkRequestXHR,
    AppBanner,
    Printing,
    WebDatabase,
    PictureInPicture,
    Portal,
    SpeechRecognizer,
    IdleManager,
    PaymentManager,
    SpeechSynthesis,
    KeyboardLock,
    WebOTPService,
    OutstandingNetworkRequestDirectSocket,
    InjectedJavascript,
    InjectedStyleSheet,
    KeepaliveRequest,
    IndexedDBEvent,
    Dummy,
    JsNetworkRequestReceivedCacheControlNoStoreResource,
    WebRTCSticky,
    WebTransportSticky,
    WebSocketSticky,
    ContentSecurityHandler,
    ContentWebAuthenticationAPI,
    ContentFileChooser,
    ContentSerial,
    ContentFileSystemAccess,
    ContentMediaDevicesDispatcherHost,
    ContentWebBluetooth,
    ContentWebUSB,
    ContentMediaSessionService,
    ContentScreenReader,
    EmbedderPopupBlockerTabHelper,
    EmbedderSafeBrowsingTriggeredPopupBlocker,
    EmbedderSafeBrowsingThreatDetails,
    EmbedderAppBannerManager,
    EmbedderDomDistillerViewerSource,
    EmbedderDomDistillerSelfDeletingRequestDelegate,
    EmbedderOomInterventionTabHelper,
    EmbedderOfflinePage,
    EmbedderChromePasswordManagerClientBindCredentialManager,
    EmbedderPermissionRequestManager,
    EmbedderModalDialog,
    EmbedderExtensions,
    EmbedderExtensionMessaging,
    EmbedderExtensionMessagingForOpenPort,
    EmbedderExtensionSentMessageToCachedFrame
  }

  /**
   * Allowed Values: currentTab, newTab, newWindow, download
   */
  public enum ClientNavigationDisposition {
    currentTab, newTab, newWindow, download
  }

  /**
   * Allowed Values: formSubmissionGet, formSubmissionPost, httpHeaderRefresh, scriptInitiated, metaTagRefresh, pageBlockInterstitial, reload, anchorClick
   */
  public enum ClientNavigationReason {
    formSubmissionGet, formSubmissionPost, httpHeaderRefresh, scriptInitiated, metaTagRefresh, pageBlockInterstitial, reload, anchorClick
  }

  /**
   * 指示帧是否跨原点隔离，以及为什么是这样。
   * Indicates whether the frame is cross-origin isolated and why it is the case.
   * Allowed Values: Isolated, NotIsolated, NotIsolatedFeatureDisabled
   */
  public enum CrossOriginIsolatedContextType {
    Isolated, NotIsolated, NotIsolatedFeatureDisabled
  }

  /**
   * Allowed Values: SharedArrayBuffers, SharedArrayBuffersTransferAllowed, PerformanceMeasureMemory, PerformanceProfile
   */
  public enum GatedAPIFeatures {
    SharedArrayBuffers, SharedArrayBuffersTransferAllowed, PerformanceMeasureMemory, PerformanceProfile
  }

  /**
   * All Permissions Policy features. This enum should match the one defined in third_party/blink/renderer/core/permissions_policy/permissions_policy_features.json5.
   * Allowed Values: accelerometer, ambient-light-sensor, attribution-reporting, autoplay, bluetooth, browsing-topics, camera, ch-dpr, ch-device-memory, ch-downlink, ch-ect, ch-prefers-color-scheme, ch-prefers-reduced-motion, ch-rtt, ch-save-data, ch-ua, ch-ua-arch, ch-ua-bitness, ch-ua-platform, ch-ua-model, ch-ua-mobile, ch-ua-form-factor, ch-ua-full-version, ch-ua-full-version-list, ch-ua-platform-version, ch-ua-wow64, ch-viewport-height, ch-viewport-width, ch-width, clipboard-read, clipboard-write, compute-pressure, cross-origin-isolated, direct-sockets, display-capture, document-domain, encrypted-media, execution-while-out-of-viewport, execution-while-not-rendered, focus-without-user-activation, fullscreen, frobulate, gamepad, geolocation, gyroscope, hid, identity-credentials-get, idle-detection, interest-cohort, join-ad-interest-group, keyboard-map, local-fonts, magnetometer, microphone, midi, otp-credentials, payment, picture-in-picture, private-aggregation, private-state-token-issuance, private-state-token-redemption, publickey-credentials-get, run-ad-auction, screen-wake-lock, serial, shared-autofill, shared-storage, shared-storage-select-url, smart-card, storage-access, sync-xhr, unload, usb, vertical-scroll, web-share, window-management, window-placement, xr-spatial-tracking
   */
  public enum PermissionsPolicyFeature {
    ;
    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        ("accelerometer"),
        ("ambient-light-sensor"),
        ("attribution-reporting"),
        ("autoplay"),
        ("bluetooth"),
        ("browsing-topics"),
        ("camera"),
        ("ch-dpr"),
        ("ch-device-memory"),
        ("ch-downlink"),
        ("ch-ect"),
        ("ch-prefers-color-scheme"),
        ("ch-prefers-reduced-motion"),
        ("ch-rtt"),
        ("ch-save-data"),
        ("ch-ua"),
        ("ch-ua-arch"),
        ("ch-ua-bitness"),
        ("ch-ua-platform"),
        ("ch-ua-model"),
        ("ch-ua-mobile"),
        ("ch-ua-form-factor"),
        ("ch-ua-full-version"),
        ("ch-ua-full-version-list"),
        ("ch-ua-platform-version"),
        ("ch-ua-wow64"),
        ("ch-viewport-height"),
        ("ch-viewport-width"),
        ("ch-width"),
        ("clipboard-read"),
        ("clipboard-write"),
        ("compute-pressure"),
        ("cross-origin-isolated"),
        ("direct-sockets"),
        ("display-capture"),
        ("document-domain"),
        ("encrypted-media"),
        ("execution-while-out-of-viewport"),
        ("execution-while-not-rendered"),
        ("focus-without-user-activation"),
        ("fullscreen"),
        ("frobulate"),
        ("gamepad"),
        ("geolocation"),
        ("gyroscope"),
        ("hid"),
        ("identity-credentials-get"),
        ("idle-detection"),
        ("interest-cohort"),
        ("join-ad-interest-group"),
        ("keyboard-map"),
        ("local-fonts"),
        ("magnetometer"),
        ("microphone"),
        ("midi"),
        ("otp-credentials"),
        ("payment"),
        ("picture-in-picture"),
        ("private-aggregation"),
        ("private-state-token-issuance"),
        ("private-state-token-redemption"),
        ("publickey-credentials-get"),
        ("run-ad-auction"),
        ("screen-wake-lock"),
        ("serial"),
        ("shared-autofill"),
        ("shared-storage"),
        ("shared-storage-select-url"),
        ("smart-card"),
        ("storage-access"),
        ("sync-xhr"),
        ("unload"),
        ("usb"),
        ("vertical-scroll"),
        ("web-share"),
        ("window-management"),
        ("window-placement"),
        ("xr-spatial-tracking")
    ));
  }

  /**
   * The type of a frameNavigated event.
   * Allowed Values: Navigation, BackForwardCacheRestore
   */
  public enum NavigationType {
    Navigation, BackForwardCacheRestore
  }

  /**
   * Status for an Origin Trial.
   * Allowed Values: Enabled, ValidTokenNotProvided, OSNotSupported, TrialNotAllowed
   */
  public enum OriginTrialStatus {
    Enabled, ValidTokenNotProvided, OSNotSupported, TrialNotAllowed
  }

  /**
   * Allowed Values: None, Subset
   */
  public enum OriginTrialUsageRestriction {
    None, Subset
  }

  /**
   * Origin Trial(https://www.chromium.org/blink/origin-trials) support. Status for an Origin Trial token.
   * Allowed Values: Success, NotSupported, Insecure, Expired, WrongOrigin, InvalidSignature, Malformed, WrongVersion, FeatureDisabled, TokenDisabled, FeatureDisabledForUser, UnknownTrial
   * Type: string
   */
  public enum OriginTrialTokenWithStatus {
    Success, NotSupported, Insecure, Expired, WrongOrigin, InvalidSignature, Malformed, WrongVersion, FeatureDisabled, TokenDisabled, FeatureDisabledForUser, UnknownTrial
  }

  /**
   * Reason for a permissions policy feature to be disabled.
   * Allowed Values: Header, IframeAttribute, InFencedFrameTree, InIsolatedApp
   */
  public enum PermissionsPolicyBlockReason {
    Header, IframeAttribute, InFencedFrameTree, InIsolatedApp
  }

  /**
   * 用于导航的引用策略。
   * The referring-policy used for the navigation.
   * Allowed Values: noReferrer, noReferrerWhenDowngrade, origin, originWhenCrossOrigin, sameOrigin, strictOrigin, strictOriginWhenCrossOrigin, unsafeUrl
   */
  public enum ReferrerPolicy {
    noReferrer, noReferrerWhenDowngrade, origin, originWhenCrossOrigin, sameOrigin, strictOrigin, strictOriginWhenCrossOrigin, unsafeUrl
  }

  /**
   * Transition type.
   * Allowed Values: link, typed, address_bar, auto_bookmark, auto_subframe, manual_subframe, generated, auto_toplevel, form_submit, reload, keyword, keyword_generated, other
   */
  public enum TransitionType {
    link, typed, address_bar, auto_bookmark, auto_subframe, manual_subframe, generated, auto_toplevel, form_submit, reload, keyword, keyword_generated, other
  }

  /**
   * 指示框架是否为安全上下文以及为什么为安全上下文。
   * Indicates whether the frame is a secure context and why it is the case.
   * Allowed Values: Secure, SecureLocalhost, InsecureScheme, InsecureAncestor
   */
  public enum SecureContextType {
    Secure, SecureLocalhost, InsecureScheme, InsecureAncestor
  }

}
