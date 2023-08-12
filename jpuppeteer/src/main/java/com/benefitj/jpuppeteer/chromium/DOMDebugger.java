package com.benefitj.jpuppeteer.chromium;

import lombok.Data;

import java.util.List;

/**
 * DOM debugging allows setting breakpoints on particular DOM operations and events. JavaScript execution will stop on
 * these operations as if there was a regular breakpoint set.
 */
@ChromiumApi("DOMDebugger")
public interface DOMDebugger {

  /**
   * Returns event listeners of the given object.
   *
   * @param objectId Runtime.RemoteObjectId
   *                 Identifier of the object to return listeners for.
   * @param depth    integer
   *                 The maximum depth at which Node children should be retrieved, defaults to 1. Use -1 for the entire subtree or provide an integer larger than 0.
   * @param pierce   boolean
   *                 Whether or not iframes and shadow roots should be traversed when returning the subtree (default is false). Reports listeners for all contexts if pierce is enabled.
   * @return {
   * listeners: array[ EventListener ] Array of relevant listeners.
   * }
   */
  void getEventListeners(String objectId, Integer depth, Boolean pierce);

  /**
   * Removes DOM breakpoint that was set using setDOMBreakpoint.
   *
   * @param nodeId DOM.NodeId
   *               Identifier of the node to remove breakpoint from.
   * @param type   DOMBreakpointType
   *               Type of the breakpoint to remove.
   */
  void removeDOMBreakpoint(String nodeId, DOMBreakpointType type);

  /**
   * Removes breakpoint on particular DOM event
   *
   * @param eventName  string
   *                   Event name.
   * @param targetName string
   *                   EventTarget interface name. EXPERIMENTAL
   */
  void removeEventListenerBreakpoint(String eventName, String targetName);

  /**
   * Removes breakpoint from XMLHttpRequest.
   *
   * @param url string
   *            Resource URL substring.
   */
  void removeXHRBreakpoint(String url);

  /**
   * Sets breakpoint on particular operation with DOM.
   *
   * @param nodeId DOM.NodeId
   *               Identifier of the node to set breakpoint on.
   * @param type   DOMBreakpointType
   *               Type of the operation to stop upon.
   */
  void setDOMBreakpoint(String nodeId, DOMBreakpointType type);

  /**
   * Sets breakpoint on particular DOM event.
   *
   * @param eventName  string
   *                   DOM Event name to stop on (any DOM event will do).
   * @param targetName string
   *                   EventTarget interface name to stop on. If equal to "*" or not provided, will stop on any EventTarget.
   */
  void setEventListenerBreakpoint(String eventName, String targetName);

  /**
   * Sets breakpoint on XMLHttpRequest.
   *
   * @param url string
   *            Resource URL substring. All XHRs having this substring in the URL will get stopped upon.
   */
  void setXHRBreakpoint(String url);

  /**
   * Removes breakpoint on particular native event.
   *
   * @param eventName string
   *                  Instrumentation name to stop on.
   */
  void removeInstrumentationBreakpoint(String eventName);

  /**
   * Sets breakpoint on particular CSP violations.
   *
   * @param violationTypes array[ CSPViolationType ]
   *                       CSP Violations to stop upon.
   */
  void setBreakOnCSPViolation(List<CSPViolationType> violationTypes);

  /**
   * Sets breakpoint on particular native event.
   *
   * @param eventName string
   *                  Instrumentation name to stop on.
   */
  void setInstrumentationBreakpoint(String eventName);

  /**
   * DOM breakpoint type.
   * Allowed Values: subtree-modified, attribute-modified, node-removed
   */
  public enum DOMBreakpointType {
    subtree_modified, attribute_modified, node_removed
  }

  /**
   * Object event listener.
   */
  @Data
  public class EventListener {
    /**
     * EventListener's type.
     */
    String type;
    /**
     * EventListener's useCapture.
     */
    boolean useCapture;
    /**
     * EventListener's passive flag.
     */
    boolean passive;
    /**
     * EventListener's once flag.
     */
    boolean once;
    /**
     * Runtime.ScriptId
     * Script id of the handler code.
     */
    String scriptId;
    /**
     * Line number in the script (0-based).
     */
    Integer lineNumber;
    /**
     * Column number in the script (0-based).
     */
    Integer columnNumber;
    /**
     * Event handler function value.
     */
    Runtime.RemoteObject handler;
    /**
     * Event original handler function value.
     */
    Runtime.RemoteObject originalHandler;
    /**
     * Node the listener is added to (if any).
     * DOM.BackendNodeId
     */
    String backendNodeId;
  }

  /**
   * CSP Violation type.
   * Allowed Values: trustedtype-sink-violation, trustedtype-policy-violation
   */
  public enum CSPViolationType {
    trustedtype_sink_violation, trustedtype_policy_violation
  }

}
