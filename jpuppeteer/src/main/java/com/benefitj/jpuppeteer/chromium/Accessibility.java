package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Accessibility Domain
 */
@Deprecated
public interface Accessibility {

  /**
   * Disables the accessibility domain.
   */
  void disable();

  /**
   * Enables the accessibility domain which causes AXNodeIds to remain consistent between method calls. This turns on accessibility for the page, which can impact performance until accessibility is disabled.
   */
  void enable();


  /**
   * Fetches a node and all ancestors up to and including the root. Requires enable() to have been called previously.
   *
   * @param nodeId        DOM.NodeId
   *                      Identifier of the node to get.
   * @param backendNodeId DOM.BackendNodeId
   *                      Identifier of the backend node to get.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper to get.
   * @return {
   * nodes:  array[ AXNode ]
   * }
   */
  JSONObject getAXNodeAndAncestors(String nodeId, String backendNodeId, String objectId);

  /**
   * Fetches a particular accessibility node by AXNodeId. Requires enable() to have been called previously.
   *
   * @param id      AXNodeId
   * @param frameId Page.FrameId
   *                The frame in whose document the node resides. If omitted, the root frame is used.
   * @return {
   * nodes:  array[ AXNode ]
   * }
   */
  JSONObject getChildAXNodes(String id, String frameId);

  /**
   * Fetches the entire accessibility tree for the root Document
   *
   * @param depth   integer
   *                The maximum depth at which descendants of the root node should be retrieved. If omitted, the full tree is returned.
   * @param frameId Page.FrameId
   *                The frame for whose document the AX tree should be retrieved. If omited, the root frame is used.
   * @return {
   * nodes:  array[ AXNode ]
   * }
   */
  JSONObject getFullAXTree(Long depth, String frameId);

  /**
   * Fetches the accessibility node and partial accessibility tree for this DOM node, if it exists.
   *
   * @param nodeId         DOM.NodeId
   *                       Identifier of the node to get the partial accessibility tree for.
   * @param backendNodeId  DOM.BackendNodeId
   *                       Identifier of the backend node to get the partial accessibility tree for.
   * @param objectId       Runtime.RemoteObjectId
   *                       JavaScript object id of the node wrapper to get the partial accessibility tree for.
   * @param fetchRelatives boolean
   *                       Whether to fetch this node's ancestors, siblings and children. Defaults to true.
   * @return {
   * nodes: array[ AXNode ] The Accessibility.AXNode for this DOM node, if it exists, plus its ancestors, siblings and children, if requested.
   * }
   */
  JSONObject getPartialAXTree(String nodeId, String backendNodeId, String objectId, String fetchRelatives);

  /**
   * Fetches the root node. Requires enable() to have been called previously.
   *
   * @param frameId Page.FrameId
   *                The frame in whose document the node resides. If omitted, the root frame is used.
   * @return {
   * node: AXNode
   * }
   */
  JSONObject getRootAXNode(String frameId);

  /**
   * Query a DOM node's accessibility subtree for accessible name and role. This command computes the name and role for all
   * nodes in the subtree, including those that are ignored for accessibility, and returns those that mactch the specified
   * name and role. If no DOM node is specified, or the DOM node does not exist, the command returns an error. If neither
   * accessibleName or role is specified, it returns all the accessibility nodes in the subtree.
   *
   * @param nodeId         DOM.NodeId
   *                       Identifier of the node for the root to query.
   * @param backendNodeId  DOM.BackendNodeId
   *                       Identifier of the backend node for the root to query.
   * @param objectId       Runtime.RemoteObjectId
   *                       JavaScript object id of the node wrapper for the root to query.
   * @param accessibleName string
   *                       Find nodes with this computed name.
   * @param role           string
   *                       Find nodes with this computed role.
   * @return {
   * nodes: array[ AXNode ]  A list of Accessibility.AXNode matching the specified attributes, including nodes that are ignored for accessibility.
   * }
   */
  JSONObject queryAXTree(String nodeId, String backendNodeId, String objectId, String accessibleName, String role);

  @Event("Accessibility")
  public interface Events {

    /**
     * The loadComplete event mirrors the load complete event sent by the browser to assistive technology when the web page has finished loading.
     *
     * @param root AXNode
     *             New document root node.
     */
    @Event("loadComplete")
    void loadComplete(AXNode root);

    /**
     * The nodesUpdated event is sent every time a previously requested node has changed the in tree.
     *
     * @param nodes array[ AXNode ]
     *              Updated node data.
     */
    @Event("nodesUpdated")
    void nodesUpdated(List<AXNode> nodes);

  }

  /**
   * A node in the accessibility tree.
   */
  @Data
  public class AXNode {
    /**
     * AXNodeId
     * Unique identifier for this node.
     */
    String nodeId;
    /**
     * Whether this node is ignored for accessibility
     */
    boolean ignored;
    /**
     * Collection of reasons why this node is hidden.
     */
    List<AXProperty> ignoredReasons;
    /**
     * This Node's role, whether explicit or implicit.
     */
    AXValue role;
    /**
     * This Node's Chrome raw role.
     */
    AXValue chromeRole;
    /**
     * The accessible name for this Node.
     */
    AXValue name;
    /**
     * The accessible description for this Node.
     */
    AXValue description;
    /**
     * The value for this Node.
     */
    AXValue value;
    /**
     * All other properties
     */
    List<AXProperty> properties;
    /**
     * AXNodeId
     * ID for this node's parent.
     */
    String parentId;
    /**
     * array[ AXNodeId ]
     * IDs for each of this node's child nodes.
     */
    List<String> childIds;
    /**
     * DOM.BackendNodeId
     * The backend ID for the associated DOM node, if any.
     */
    String backendDOMNodeId;
    /**
     * Page.FrameId
     * The frame ID for the frame associated with this nodes document.
     */
    String frameId;
  }

  /**
   *
   */
  @Data
  public class AXProperty {
    /**
     * The name of this property.
     */
    AXPropertyName name;
    /**
     * The value of this property.
     */
    AXValue value;
  }

  /**
   * Values of AXProperty name:
   * from 'busy' to 'roledescription': states which apply to every AX node
   * from 'live' to 'root': attributes which apply to nodes in live regions
   * from 'autocomplete' to 'valuetext': attributes which apply to widgets
   * from 'checked' to 'selected': states which apply to widgets
   * from 'activedescendant' to 'owns' - relationships between elements other than parent/child/sibling.
   * Allowed Values: busy, disabled, editable, focusable, focused, hidden, hiddenRoot, invalid, keyshortcuts, settable,
   * roledescription, live, atomic, relevant, root, autocomplete, hasPopup, level, multiselectable, orientation, multiline,
   * readonly, required, valuemin, valuemax, valuetext, checked, expanded, modal, pressed, selected, activedescendant,
   * controls, describedby, details, errormessage, flowto, labelledby, owns
   */
  public enum AXPropertyName {
    busy, disabled, editable, focusable, focused, hidden, hiddenRoot, invalid, keyshortcuts, settable, roledescription,
    live, atomic, relevant, root, autocomplete, hasPopup, level, multiselectable, orientation, multiline, readonly,
    required, valuemin, valuemax, valuetext, checked, expanded, modal, pressed, selected, activedescendant, controls,
    describedby, details, errormessage, flowto, labelledby, owns
  }

  /**
   *
   */
  @Data
  public class AXRelatedNode {
    /**
     * DOM.BackendNodeId
     * The BackendNodeId of the related DOM node.
     */
    String backendDOMNodeId;
    /**
     * string
     * The IDRef value provided, if any.
     */
    String idref;
    /**
     * The text alternative of this node in the current context.
     */
    String text;
  }

  /**
   * A single computed AX property.
   */
  @Data
  public class AXValue {
    /**
     * The type of this value.
     */
    AXValueType type;
    /**
     * The computed value of this property.
     */
    JSONObject value;
    /**
     * One or more related nodes, if applicable.
     */
    List<AXRelatedNode> relatedNodes;
    /**
     * The sources which contributed to the computation of this property.
     */
    List<AXValueSource> sources;
  }

  /**
   * Enum of possible native property sources (as a subtype of a particular AXValueSourceType).
   * Allowed Values: description, figcaption, label, labelfor, labelwrapped, legend, rubyannotation, tablecaption, title, other
   */
  public enum AXValueNativeSourceType {
    description, figcaption, label, labelfor, labelwrapped, legend, rubyannotation, tablecaption, title, other
  }

  /**
   * A single source for a computed AX property.
   */
  @Data
  public class AXValueSource {
    /**
     * What type of source this is.
     */
    AXValueSourceType type;
    /**
     * The value of this property source.
     */
    AXValue value;
    /**
     * The name of the relevant attribute, if any.
     */
    String attribute;
    /**
     * The value of the relevant attribute, if any.
     */
    AXValue attributeValue;
    /**
     * Whether this source is superseded by a higher priority source.
     */
    boolean superseded;
    /**
     * The native markup source for this value, e.g. a <label> element.
     */
    AXValueNativeSourceType nativeSource;
    /**
     * The value, such as a node or node list, of the native source.
     */
    AXValue nativeSourceValue;
    /**
     * Whether the value for this property is invalid.
     */
    boolean invalid;
    /**
     * Reason for the value being invalid, if it is.
     */
    String invalidReason;
  }

  /**
   * Enum of possible property sources.
   * Allowed Values: attribute, implicit, style, contents, placeholder, relatedElement
   */
  public enum AXValueSourceType {
    attribute, implicit, style, contents, placeholder, relatedElement
  }

  /**
   * Enum of possible property types.
   * Allowed Values: boolean, tristate, booleanOrUndefined, idref, idrefList, integer, node, nodeList, number, string, computedString, token, tokenList, domRelation, role, internalRole, valueUndefined
   */
  public enum AXValueType {
    _boolean, tristate, booleanOrUndefined, idref, idrefList, integer, node, nodeList, number, string, computedString, token, tokenList, domRelation, role, internalRole, valueUndefined;

    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        "boolean, tristate, booleanOrUndefined, idref, idrefList, integer, node, nodeList, number, string, computedString, token, tokenList, domRelation, role, internalRole, valueUndefined".split(",")
    ));
  }

}
