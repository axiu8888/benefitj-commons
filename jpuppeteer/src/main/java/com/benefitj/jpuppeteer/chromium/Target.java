package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

public interface Target {

  /**
   * Activates (focuses) the target.
   */
  void activateTarget(String targetId);

  /**
   * Attaches to the target with given id.
   *
   * @param targetId
   * @param flatten  Enables "flat" access to the session via specifying sessionId attribute in the commands. We plan to make this the default, deprecate non-flattened mode, and eventually retire it. See crbug.com/991325.
   */
  String attachToTarget(String targetId, boolean flatten);

  /**
   * Closes the target. If the target is a page that gets closed too.
   *
   * @return {
   * success: boolean Always set to true. If an error occurs, the response indicates protocol error.
   * }
   */
  boolean closeTarget(String targetId);

  /**
   * Creates a new page.
   *
   * @param targetId                The initial URL the page will be navigated to. An empty string indicates about:blank.
   * @param width                   Frame width in DIP (headless chrome only).
   * @param height                  Frame height in DIP (headless chrome only).
   * @param browserContextId        The browser context to create the page in. EXPERIMENTAL
   * @param enableBeginFrameControl Whether BeginFrames for this target will be controlled via DevTools (headless chrome only, not supported on MacOS yet, false by default). EXPERIMENTAL
   * @param newWindow               Whether to create a new Window or Tab (chrome-only, false by default).
   * @param background              Whether to create the target in background or foreground (chrome-only, false by default).
   * @param forTab                  Whether to create the target of type "tab". EXPERIMENTAL
   * @return {
   *   targetId: String
   * }
   */
  JSONObject createTarget(String targetId, Integer width, Integer height, String browserContextId, boolean enableBeginFrameControl, boolean newWindow, boolean background, boolean forTab);

  /**
   * Detaches session with given id.
   *
   * @param sessionId Session to detach.
   * @param targetId  TargetID
   *                  Deprecated. DEPRECATED
   */
  void detachFromTarget(String sessionId, String targetId);

  /**
   * Retrieves a list of available targets.
   *
   * @param filter Only targets matching filter will be reported. If filter is not specified and target discovery is currently enabled, a filter used for target discovery is used for consistency.
   * @return {
   *   targetInfo: array[TargetInfo]
   * }
   */
  JSONObject getTargets(TargetFilter filter);

  /**
   * Controls whether to discover available targets and notify via targetCreated/targetInfoChanged/targetDestroyed events.
   *
   * @param discover Whether to discover available targets.
   * @param filter   TargetFilter
   *                 Only targets matching filter will be attached. If discover is false, filter must be omitted or empty.
   */
  void setDiscoverTargets(boolean discover, TargetFilter filter);

  /**
   * Sends protocol message over session with given id. Consider using flat mode instead; see commands attachToTarget, setAutoAttach, and crbug.com/991325.
   *
   * @param message
   * @param sessionId SessionID
   *                  Identifier of the session.
   * @param targetId  TargetID  Deprecated.
   */
  void sendMessageToTarget(String message, String sessionId, String targetId);

  /**
   * Attaches to the browser target, only uses flat sessionId mode.
   */
  String attachToBrowserTarget();

  /**
   * Adds the specified target to the list of targets that will be monitored for any related target creation (such as child frames,
   * child workers and new versions of service worker) and reported through attachedToTarget. The specified target is also auto-attached.
   * This cancels the effect of any previous setAutoAttach and is also cancelled by subsequent setAutoAttach. Only available at the Browser target.
   *
   * @param targetId
   * @param waitForDebuggerOnStart boolean
   *                               Whether to pause new targets when attaching to them. Use Runtime.runIfWaitingForDebugger to run paused targets.
   * @param filter                 TargetFilter
   *                               Only targets matching filter will be attached. EXPERIMENTAL
   */
  void autoAttachRelated(String targetId, boolean waitForDebuggerOnStart, TargetFilter filter);

  /**
   * Creates a new empty BrowserContext. Similar to an incognito profile but you can have more than one.
   *
   * @param disposeOnDetach                   If specified, disposes this context when debugging session disconnects.
   * @param proxyServer                       Proxy server, similar to the one passed to --proxy-server
   * @param proxyBypassList                   Proxy bypass list, similar to the one passed to --proxy-bypass-list
   * @param originsWithUniversalNetworkAccess An optional list of origins to grant unlimited cross-origin access to. Parts of the URL other than those constituting origin are ignored.
   */
  String createBrowserContext(boolean disposeOnDetach, String proxyServer, String proxyBypassList, List<String> originsWithUniversalNetworkAccess);

  /**
   * Deletes a BrowserContext. All the belonging pages will be closed without calling their beforeunload hooks.
   *
   * @param browserContextId
   */
  void disposeBrowserContext(String browserContextId);

  /**
   * Inject object to the target's main frame that provides a communication channel with browser target. Injected object will be available as window[bindingName]. The object has the follwing API:
   * binding.send(json) - a method to send messages over the remote debugging protocol
   * binding.onmessage = json => handleMessage(json) - a callback that will be called for the protocol notifications and command responses.
   *
   * @param targetId
   * @param bindingName Binding name, 'cdp' if not specified.
   */
  void exposeDevToolsProtocol(String targetId, String bindingName);

  /**
   * Returns all browser contexts created with Target.createBrowserContext method.
   */
  List<String> getBrowserContexts();

  /**
   * Returns information about a target.
   */
  TargetInfo getTargetInfo(String targetId);

  /**
   * Controls whether to automatically attach to new targets which are considered to be related to this one.
   * When turned on, attaches to all existing related targets as well. When turned off, automatically detaches from all currently attached targets.
   * This also clears all targets added by autoAttachRelated from the list of targets to watch for creation of related targets.
   *
   * @param autoAttach             Whether to auto-attach to related targets.
   * @param waitForDebuggerOnStart Whether to pause new targets when attaching to them. Use Runtime.runIfWaitingForDebugger to run paused targets.
   * @param flatten                Enables "flat" access to the session via specifying sessionId attribute in the commands. We plan to make this the default, deprecate non-flattened mode, and eventually retire it. See crbug.com/991325.
   * @param filter                 TargetFilter
   *                               Only targets matching filter will be attached.
   */
  void setAutoAttach(boolean autoAttach, boolean waitForDebuggerOnStart, boolean flatten, TargetFilter filter);


  /**
   * Enables target discovery for the specified locations, when setDiscoverTargets was set to true.
   */
  String setRemoteLocations(List<RemoteLocation> locations);

  /**
   * Notifies about a new protocol message received from the session (as reported in attachedToTarget event).
   *
   * @param sessionId SessionID
   *                  Identifier of a session which sends a message.
   * @param message   string
   * @param targetId  TargetID
   *                  Deprecated.
   */
  @Event("receivedMessageFromTarget")
  void receivedMessageFromTarget(String sessionId, String message, String targetId);

  /**
   * Issued when a target has crashed.
   *
   * @param targetId  TargetID
   * @param status    string
   *                  Termination status type.
   * @param errorCode integer
   *                  Termination error code.
   */
  @Event("targetCrashed")
  void targetCrashed(String targetId, String status, Integer errorCode);

  /**
   * Issued when a possible inspection target is created.
   */
  @Event("targetCreated")
  void targetCreated(TargetInfo targetInfo);

  /**
   * Issued when a target is destroyed.
   */
  @Event("targetDestroyed")
  void targetDestroyed(String targetId);

  /**
   * Issued when some information about a target has changed. This only happens between targetCreated and targetDestroyed.
   */
  @Event("targetInfoChanged")
  void targetInfoChanged(TargetInfo targetInfo);

  /**
   * Issued when attached to target because of auto-attach or attachToTarget command.
   *
   * @param sessionId          SessionID
   *                           Identifier assigned to the session used to send/receive messages.
   * @param targetInfo         TargetInfo
   * @param waitingForDebugger boolean
   */
  @Event("attachedToTarget")
  void attachedToTarget(String sessionId, TargetInfo targetInfo, boolean waitingForDebugger);

  /**
   * Issued when detached from target for any reason (including detachFromTarget command). Can be issued multiple times per target if multiple sessions have been attached to it.
   *
   * @param sessionId SessionID
   *                  Detached session identifier.
   * @param targetId  TargetID
   */
  @Event("detachedFromTarget")
  void detachedFromTarget(String sessionId, String targetId);


  /**
   *
   */
  @Data
  public class TargetInfo {
    String targetId;
    String type;
    String title;
    String url;
    /**
     * Whether the target has an attached client.
     */
    boolean attached;
    /**
     * Opener target Id
     */
    String openerId;
    /**
     * Whether the target has access to the originating window. EXPERIMENTAL
     */
    boolean canAccessOpener;
    /**
     * Frame id of originating window (is only set if target has an opener). EXPERIMENTAL
     */
    String openerFrameId;
    /**
     * Browser.BrowserContextID
     * EXPERIMENTAL
     */
    String browserContextId;
    /**
     * Provides additional details for specific target types. For example, for the type of "page", this may be set to "portal" or "prerender". EXPERIMENTAL
     */
    String subtype;
  }

  /**
   * A filter used by target query/discovery/auto-attach operations.
   */
  @Data
  public class FilterEntry {
    /**
     * If set, causes exclusion of mathcing targets from the list.
     */
    boolean exclude;
    /**
     * If not present, matches any type.
     */
    String type;
  }

  @Data
  public class RemoteLocation {
    String host;
    Integer port;
  }

  /**
   * The entries in TargetFilter are matched sequentially against targets and the first entry that matches determines if the target is included or not,
   * depending on the value of exclude field in the entry. If filter is not specified, the one assumed is [{type: "browser", exclude: true}, {type: "tab", exclude: true}, {}]
   * (i.e. include everything but browser and tab).
   */
  public class TargetFilter extends JSONArray {
  }

}
