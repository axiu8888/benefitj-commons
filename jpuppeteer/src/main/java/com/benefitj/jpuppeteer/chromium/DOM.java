package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.Serializable;
import java.util.List;

/**
 * This domain exposes DOM read/write operations. Each DOM Node is represented with its mirror object that has an id.
 * This id can be used to get additional information on the Node, resolve it into the JavaScript object wrapper, etc.
 * It is important that client receives DOM events only for the nodes that are known to the client. Backend keeps track of
 * the nodes that were sent to the client and never sends the same node twice. It is client's responsibility to collect
 * information about the nodes that were sent to the client. Note that iframe owner elements will return corresponding
 * document elements as their child nodes.
 */
@ChromiumApi("DOM")
public interface DOM {

  /**
   * Describes node given its id, does not require domain to be enabled. Does not start tracking any objects, can be used for automation.
   *
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   * @param depth         integer
   *                      The maximum depth at which children should be retrieved, defaults to 1. Use -1 for the entire subtree or provide an integer larger than 0.
   * @param pierce        boolean
   *                      Whether or not iframes and shadow roots should be traversed when returning the subtree (default is false).
   * @return {
   * node:  Node  Node description.
   * }
   */
  JSONObject describeNode(String nodeId, String backendNodeId, String objectId, Long depth, Boolean pierce);

  /**
   * Disables DOM agent for the given page.
   */
  void disable();

  /**
   * Enables DOM agent for the given page.
   *
   * @param includeWhitespace string
   *                          Whether to include whitespaces in the children array of returned Nodes.
   *                          Allowed Values: none, all
   */
  void enable(String includeWhitespace);

  /**
   * Focuses the given element.
   *
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   */
  void focus(String nodeId, String backendNodeId, String objectId);

  /**
   * Returns attributes for the specified node.
   *
   * @param nodeId NodeId
   *               Id of the node to retrieve attibutes for.
   * @return {
   * attributes: array[ string ]  An interleaved array of node attribute names and values.
   * }
   */
  JSONObject getAttributes(String nodeId);

  /**
   * Returns boxes for the given node.
   *
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   * @return {
   * model: BoxModel  Box model for the node.
   * }
   */
  JSONObject getBoxModel(String nodeId, String backendNodeId, String objectId);

  /**
   * Returns the root DOM node (and optionally the subtree) to the caller. Implicitly enables the DOM domain events for the current target.
   *
   * @param depth  integer
   *               The maximum depth at which children should be retrieved, defaults to 1. Use -1 for the entire subtree or provide an integer larger than 0.
   * @param pierce boolean
   *               Whether or not iframes and shadow roots should be traversed when returning the subtree (default is false).
   * @return {
   * root: Node  Resulting node.
   * }
   */
  JSONObject getDocument(Long depth, Boolean pierce);

  /**
   * Returns node id at given location. Depending on whether DOM domain is enabled, nodeId is either returned or not.
   *
   * @param x                         integer
   *                                  X coordinate.
   * @param y                         integer
   *                                  Y coordinate.
   * @param includeUserAgentShadowDOM boolean
   *                                  False to skip to the nearest non-UA shadow root ancestor (default: false).
   * @param ignorePointerEventsNone   boolean
   *                                  Whether to ignore pointer-events: none on elements and hit test them.
   * @return {
   * backendNodeId: BackendNodeId  Resulting node.
   * frameId: Page.FrameId  Frame this node belongs to.
   * nodeId: NodeId Id of the node at given coordinates, only when enabled and requested document.
   * }
   */
  JSONObject getNodeForLocation(Integer x, Integer y, Boolean includeUserAgentShadowDOM, Boolean ignorePointerEventsNone);

  /**
   * Returns node's HTML markup.
   *
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   * @return
   */
  JSONObject getOuterHTML(String nodeId, String backendNodeId, String objectId);

  /**
   * Hides any highlight.
   */
  void hideHighlight();

  /**
   * Highlights DOM node.
   */
  void highlightNode();

  /**
   * Highlights given rectangle.
   */
  void highlightRect();

  /**
   * Moves node into the new container, places it before the given anchor.
   *
   * @param nodeId             NodeId
   *                           Id of the node to move.
   * @param targetNodeId       NodeId
   *                           Id of the element to drop the moved node into.
   * @param insertBeforeNodeId NodeId
   *                           Drop node before this one (if absent, the moved node becomes the last child of targetNodeId).
   * @return {
   * nodeId: NodeId New id of the moved node.
   * }
   */
  JSONObject moveTo(String nodeId, String targetNodeId, String insertBeforeNodeId);

  /**
   * Executes querySelector on a given node.
   *
   * @param nodeId   NodeId
   *                 Id of the node to query upon.
   * @param selector string
   *                 Selector string.
   * @return {
   * nodeId:  NodeId  Query selector result.
   * }
   */
  JSONObject querySelector(String nodeId, String selector);

  /**
   * Executes querySelectorAll on a given node.
   *
   * @param nodeId   NodeId
   *                 Id of the node to query upon.
   * @param selector string
   *                 Selector string.
   * @return {
   * nodeIds: array[ NodeId ]  Query selector result.
   * }
   */
  JSONObject querySelectorAll(String nodeId, String selector);

  /**
   * Removes attribute with given name from an element with given id.
   *
   * @param nodeId NodeId
   *               Id of the element to remove attribute from.
   * @param name   string
   *               Name of the attribute to remove.
   */
  void removeAttribute(String nodeId, String name);

  /**
   * Removes node with given id.
   *
   * @param nodeId NodeId
   *               Id of the node to remove.
   */
  void removeNode(String nodeId);

  /**
   * Requests that children of the node with given id are returned to the caller in form of setChildNodes events
   * where not only immediate children are retrieved, but all children down to the specified depth.
   *
   * @param nodeId NodeId
   *               Id of the node to get children for.
   * @param depth  integer
   *               The maximum depth at which children should be retrieved, defaults to 1. Use -1 for the entire subtree or provide an integer larger than 0.
   * @param pierce boolean
   *               Whether or not iframes and shadow roots should be traversed when returning the sub-tree (default is false).
   */
  void requestChildNodes(String nodeId, Long depth, Boolean pierce);

  /**
   * Requests that the node is sent to the caller given the JavaScript node object reference. All nodes that form the path
   * from the node to the root are also sent to the client as a series of setChildNodes notifications.
   *
   * @param objectId Runtime.RemoteObjectId
   *                 JavaScript object id to convert into node.
   * @return {
   * nodeId: NodeId  Node id for given object.
   * }
   */
  JSONObject requestNode(String objectId);

  /**
   * Resolves the JavaScript node object for a given NodeId or BackendNodeId.
   *
   * @param nodeId             NodeId
   *                           Id of the node to resolve.
   * @param backendNodeId      DOM.BackendNodeId
   *                           Backend identifier of the node to resolve.
   * @param objectGroup        string
   *                           Symbolic group name that can be used to release multiple objects.
   * @param executionContextId Runtime.ExecutionContextId
   *                           Execution context in which to resolve the node.
   * @return {
   * object: Runtime.RemoteObject  JavaScript object wrapper for given node.
   * }
   */
  JSONObject resolveNode(String nodeId, String backendNodeId, String objectGroup, String executionContextId);

  /**
   * Sets attributes on element with given id. This method is useful when user edits some existing attribute value and types in several attribute name/value pairs.
   *
   * @param nodeId NodeId
   *               Id of the element to set attributes for.
   * @param text   string
   *               Text with a number of attributes. Will parse this text using HTML parser.
   * @param name   string
   *               Attribute name to replace with new attributes derived from text in case text parsed successfully.
   */
  JSONObject setAttributesAsText(String nodeId, String text, String name);

  /**
   * Sets attribute for an element with given id.
   *
   * @param nodeId NodeId
   *               Id of the element to set attribute for.
   * @param name   string
   *               Attribute name.
   * @param value  string
   *               Attribute value.
   */
  JSONObject setAttributeValue(String nodeId, String name, String value);

  /**
   * Sets files for the given file input element.
   *
   * @param files         array[ string ]
   *                      Array of file paths to set.
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   */
  JSONObject setFileInputFiles(List<String> files, String nodeId, String backendNodeId, String objectId);

  /**
   * Sets node name for a node with given id.
   *
   * @param nodeId NodeId
   *               Id of the node to set name for.
   * @param name   string
   *               New node's name.
   * @return {
   * nodeId: NodeId New node's id.
   * }
   */
  JSONObject setNodeName(String nodeId, String name);

  /**
   * Sets node value for a node with given id.
   *
   * @param nodeId NodeId
   *               Id of the node to set value for.
   * @param value  string
   *               New node's value.
   */
  JSONObject setNodeValue(String nodeId, String value);

  /**
   * Sets node HTML markup, returns new node id.
   *
   * @param nodeId    NodeId
   *                  Id of the node to set markup for.
   * @param outerHTML string
   *                  Outer HTML markup to set.
   */
  JSONObject setOuterHTML(String nodeId, String outerHTML);

  /**
   * Returns the root DOM node (and optionally the subtree) to the caller. Deprecated, as it is not designed to work well
   * with the rest of the DOM agent. Use DOMSnapshot.captureSnapshot instead.
   *
   * @param depth  integer
   *               The maximum depth at which children should be retrieved, defaults to 1. Use -1 for the entire subtree or provide an integer larger than 0.
   * @param pierce boolean
   *               Whether or not iframes and shadow roots should be traversed when returning the subtree (default is false).
   * @return {
   * nodes: array[ Node ]  Resulting node.
   * }
   */
  JSONObject getFlattenedDocument(Long depth, Boolean pierce);

  /**
   * Collects class names for the node with given id and all of it's child nodes.
   *
   * @param nodeId NodeId
   *               Id of the node to collect class names.
   * @return {
   * classNames: array[ string ]  Class name list.
   * }
   */
  JSONObject collectClassNamesFromSubtree(String nodeId);

  /**
   * Creates a deep copy of the specified node and places it into the target container before the given anchor.
   *
   * @param nodeId             NodeId
   *                           Id of the node to copy.
   * @param targetNodeId       NodeId
   *                           Id of the element to drop the copy into.
   * @param insertBeforeNodeId NodeId
   *                           Drop the copy before this node (if absent, the copy becomes the last child of targetNodeId).
   * @return {
   * nodeId: NodeId  Id of the node clone.
   * }
   */
  JSONObject copyTo(String nodeId, String targetNodeId, String insertBeforeNodeId);

  /**
   * Discards search results from the session with the given id. getSearchResults should no longer be called for that search.
   *
   * @param searchId string
   *                 Unique search session identifier.
   */
  void discardSearchResults(String searchId);

  /**
   * Returns the query container of the given node based on container query conditions: containerName, physical, and logical axes.
   * If no axes are provided, the style container is returned, which is the direct parent or the closest element with a matching container-name.
   *
   * @param nodeId        NodeId
   * @param containerName string
   * @param physicalAxes  PhysicalAxes
   * @param logicalAxes   LogicalAxes
   * @return {
   * nodeId: NodeId  The container node for the given node, or null if not found.
   * }
   */
  JSONObject getContainerForNode(String nodeId, String containerName, PhysicalAxes physicalAxes, LogicalAxes logicalAxes);

  /**
   * Returns quads that describe node position on the page. This method might return multiple quads for inline nodes.
   *
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   * @return {
   * quads: array[ Quad ] Quads that describe node layout relative to viewport.
   * }
   */
  JSONObject getContentQuads(String nodeId, String backendNodeId, String objectId);

  /**
   * Returns file information for the given File wrapper.
   *
   * @param objectId Runtime.RemoteObjectId
   *                 JavaScript object id of the node wrapper.
   * @return path
   */
  String getFileInfo(String objectId);

  /**
   * Returns iframe node that owns iframe with the given domain.
   *
   * @param frameId Page.FrameId
   * @return {
   * backendNodeId: BackendNodeId  Resulting node.
   * nodeId: NodeId  Id of the node at given coordinates, only when enabled and requested document.
   * }
   */
  JSONObject getFrameOwner(String frameId);

  /**
   * Finds nodes with a given computed style in a subtree.
   *
   * @param nodeId         NodeId
   *                       Node ID pointing to the root of a subtree.
   * @param computedStyles array[ CSSComputedStyleProperty ]
   *                       The style to filter nodes by (includes nodes if any of properties matches).
   * @param pierce         boolean
   *                       Whether or not iframes and shadow roots in the same target should be traversed when returning the results (default is false).
   * @return {
   * nodeIds: array[ NodeId ]  Resulting nodes.
   * }
   */
  JSONObject getNodesForSubtreeByStyle(String nodeId, List<CSSComputedStyleProperty> computedStyles, Boolean pierce);

  /**
   * Gets stack traces associated with a Node. As of now, only provides stack trace for Node creation.
   *
   * @param nodeId NodeId
   *               Id of the node to get stack traces for.
   * @return {
   * creation: Runtime.StackTrace  Creation stack trace, if available.
   * }
   */
  JSONObject getNodeStackTraces(String nodeId);

  /**
   * Returns the descendants of a container query container that have container queries against this container.
   *
   * @param nodeId NodeId
   *               Id of the container node to find querying descendants from.
   * @return {
   * nodeIds: array[ NodeId ] Descendant nodes with container queries against the given container.
   * }
   */
  JSONObject getQueryingDescendantsForContainer(String nodeId);

  /**
   * Returns the id of the nearest ancestor that is a relayout boundary.
   *
   * @param nodeId NodeId
   *               Id of the node.
   * @return {
   * nodeId: NodeId  Relayout boundary node id for the given node.
   * }
   */
  JSONObject getRelayoutBoundary(String nodeId);

  /**
   * Returns search results from given fromIndex to given toIndex from the search with the given identifier.
   *
   * @param searchId  string
   *                  Unique search session identifier.
   * @param fromIndex integer
   *                  Start index of the search result to be returned.
   * @param toIndex   integer
   *                  End index of the search result to be returned.
   * @return {
   * nodeIds: array[ NodeId ]  Ids of the search result nodes.
   * }
   */
  JSONObject getSearchResults(String searchId, Integer fromIndex, Integer toIndex);

  /**
   * Returns NodeIds of current top layer elements. Top layer is rendered closest to the user within a viewport, therefore its elements always appear on top of all other content.
   *
   * @param nodeIds array[ NodeId ]
   *                NodeIds of top layer elements
   */
  void getTopLayerElements(List<String> nodeIds);

  /**
   * Marks last undoable state.
   */
  void markUndoableState();

  /**
   * Searches for a given string in the DOM tree. Use getSearchResults to access search results or cancelSearch to end this search session.
   *
   * @param query                     string
   *                                  Plain text or query selector or XPath search query.
   * @param includeUserAgentShadowDOM boolean
   *                                  True to search in user agent shadow DOM.
   * @return {
   * searchId: string  Unique search session identifier.
   * resultCount: integer  Number of search results.
   * }
   */
  JSONObject performSearch(String query, Boolean includeUserAgentShadowDOM);

  /**
   * Requests that the node is sent to the caller given its path. // FIXME, use XPath
   *
   * @param path string
   *             Path to node in the proprietary format.
   * @return {
   * nodeId:  NodeId  Id of the node for given path.
   * }
   */
  JSONObject pushNodeByPathToFrontend(String path);

  /**
   * Requests that a batch of nodes is sent to the caller given their backend node ids.
   *
   * @param backendNodeIds array[ BackendNodeId ]
   *                       The array of backend node ids.
   * @return {
   * nodeIds: array[ NodeId ]  The array of ids of pushed nodes that correspond to the backend ids specified in backendNodeIds.
   * }
   */
  JSONObject pushNodesByBackendIdsToFrontend(List<String> backendNodeIds);

  /**
   * Re-does the last undone action.
   */
  void redo();

  /**
   * Scrolls the specified rect of the given node into view if not already visible. Note: exactly one between nodeId, backendNodeId and objectId should be passed to identify the node.
   *
   * @param nodeId        NodeId
   *                      Identifier of the node.
   * @param backendNodeId BackendNodeId
   *                      Identifier of the backend node.
   * @param objectId      Runtime.RemoteObjectId
   *                      JavaScript object id of the node wrapper.
   * @param rect          Rect
   *                      The rect to be scrolled into view, relative to the node's border box, in CSS pixels. When omitted, center of the node will be used, similar to Element.scrollIntoView.
   */
  void scrollIntoViewIfNeeded(String nodeId, String backendNodeId, String objectId, Rect rect);

  /**
   * Enables console to refer to the node with given id via $x (see Command Line API for more details $x functions).
   *
   * @param nodeId NodeId
   *               DOM node id to be accessible by means of $x command line API.
   */
  void setInspectedNode(String nodeId);

  /**
   * Sets if stack traces should be captured for Nodes. See Node.getNodeStackTraces. Default is disabled.
   *
   * @param enable boolean
   *               Enable or disable.
   */
  void setNodeStackTracesEnabled(Boolean enable);

  /**
   * Undoes the last performed action.
   */
  void undo();

  /**
   * 事件
   */
  @Event("DOM")
  public interface Events {
    /**
     * Fired when Element's attribute is modified.
     *
     * @param nodeId NodeId
     *               Id of the node that has changed.
     * @param name   string
     *               Attribute name.
     * @param value  string
     *               Attribute value.
     */
    @Event("attributeModified")
    void attributeModified(String nodeId, String name, String value);

    /**
     * Fired when Element's attribute is removed.
     *
     * @param nodeId NodeId
     *               Id of the node that has changed.
     * @param name   string
     *               A ttribute name.
     */
    @Event("attributeRemoved")
    void attributeRemoved(String nodeId, String name);

    /**
     * Mirrors DOMCharacterDataModified event.
     *
     * @param nodeId        NodeId
     *                      Id of the node that has changed.
     * @param characterData string
     *                      New text value.
     */
    @Event("characterDataModified")
    void characterDataModified(String nodeId, String characterData);

    /**
     * Fired when Container's child node count has changed.
     *
     * @param nodeId         NodeId
     *                       Id of the node that has changed.
     * @param childNodeCount integer
     *                       New node count.
     */
    @Event("childNodeCountUpdated")
    void childNodeCountUpdated(String nodeId, Integer childNodeCount);

    /**
     * Mirrors DOMNodeInserted event.
     *
     * @param parentNodeId   NodeId
     *                       Id of the node that has changed.
     * @param previousNodeId NodeId
     *                       Id of the previous sibling.
     * @param node           Node
     *                       Inserted node data.
     */
    @Event("childNodeInserted")
    void childNodeInserted(String parentNodeId, String previousNodeId, Node node);

    /**
     * Mirrors DOMNodeRemoved event.
     *
     * @param parentNodeId NodeId
     *                     Parent id.
     * @param nodeId       NodeId
     *                     Id of the node that has been removed.
     */
    @Event("childNodeRemoved")
    void childNodeRemoved(String parentNodeId, String nodeId);

    /**
     * Fired when Document has been totally updated. Node ids are no longer valid.
     */
    @Event("documentUpdated")
    void documentUpdated();

    /**
     * Fired when backend wants to provide client with the missing DOM structure. This happens upon most of the calls requesting node ids.
     *
     * @param parentId NodeId
     *                 Parent node id to populate with children.
     * @param nodes    array[ Node ]
     *                 Child nodes array.
     */
    @Event("setChildNodes")
    void setChildNodes(String parentId, List<Node> nodes);

    /**
     * Called when distribution is changed.
     *
     * @param insertionPointId NodeId
     *                         Insertion point where distributed nodes were updated.
     * @param distributedNodes array[ BackendNode ]
     *                         Distributed nodes for given insertion point.
     */
    @Event("distributedNodesUpdated")
    void distributedNodesUpdated(String insertionPointId, List<BackendNode> distributedNodes);

    /**
     * Fired when Element's inline style is modified via a CSS property modification.
     *
     * @param nodeIds array[ NodeId ]
     *                Ids of the nodes for which the inline styles have been invalidated.
     */
    @Event("inlineStyleInvalidated")
    void inlineStyleInvalidated(List<String> nodeIds);

    /**
     * Called when a pseudo element is added to an element.
     *
     * @param parentId      NodeId
     *                      Pseudo element's parent element id.
     * @param pseudoElement Node
     *                      The added pseudo element.
     */
    @Event("pseudoElementAdded")
    void pseudoElementAdded(String parentId, Node pseudoElement);

    /**
     * Called when a pseudo element is removed from an element.
     *
     * @param parentId        NodeId
     *                        Pseudo element's parent element id.
     * @param pseudoElementId NodeId
     *                        The removed pseudo element id.
     */
    @Event("pseudoElementRemoved")
    void pseudoElementRemoved(String parentId, String pseudoElementId);

    /**
     * Called when shadow root is popped from the element.
     *
     * @param hostId NodeId
     *               Host element id.
     * @param rootId NodeId
     *               Shadow root id.
     */
    @Event("shadowRootPopped")
    void shadowRootPopped(String hostId, String rootId);

    /**
     * Called when shadow root is pushed into the element.
     *
     * @param hostId Host element id.
     * @param rootId Shadow root id.
     */
    @Event("shadowRootPushed")
    void shadowRootPushed(Serializable hostId, Serializable rootId);

    /**
     * Called when top layer elements are changed.
     *
     * @param hostId Host element id.
     * @param rootId Shadow root id.
     */
    @Event("topLayerElementsUpdated")
    void topLayerElementsUpdated(Serializable hostId, Serializable rootId);

  }

  /**
   * Backend node with a friendly name.
   */
  @Data
  public class BackendNode {
    /**
     * Node's nodeType.
     */
    Integer nodeType;
    /**
     * Node's nodeName.
     */
    String nodeName;
    Integer backendNodeId;
  }

  /**
   * Box model.
   */
  @Data
  public class BoxModel {
    /**
     * Content box
     */
    Quad content;
    /**
     * Padding box
     */
    Quad padding;
    /**
     * Border box
     */
    Quad border;
    /**
     * Margin box
     */
    Quad margin;
    /**
     * Node width
     */
    Integer width;
    /**
     * Node height
     */
    Integer height;
    /**
     * Shape outside coordinates
     */
    ShapeOutsideInfo shapeOutside;
  }

  /**
   * Document compatibility mode.
   * Allowed Values: QuirksMode, LimitedQuirksMode, NoQuirksMode
   */
  public enum CompatibilityMode {
    QuirksMode, LimitedQuirksMode, NoQuirksMode
  }

  @Data
  public class CSSComputedStyleProperty {
    /**
     * Computed style property name.
     */
    String name;
    /**
     * Computed style property value.
     */
    String value;
  }

  /**
   * ContainerSelector logical axes
   * Allowed Values: Inline, Block, Both
   */
  public enum LogicalAxes {
    Inline, Block, Both
  }

  /**
   * DOM interaction is implemented in terms of mirror objects that represent the actual DOM nodes. DOMNode is a base node mirror type.
   */
  @Data
  public class Node {
    /**
     * Node identifier that is passed into the rest of the DOM messages as the nodeId. Backend will only push node with given id once. It is aware of all requested nodes and will only fire DOM events for nodes known to the client.
     */
    Long nodeId;
    /**
     * The id of the parent node if any.
     */
    Long parentId;
    /**
     * The BackendNodeId for this node.
     */
    Long backendNodeId;
    /**
     * Node's nodeType.
     */
    Integer nodeType;
    /**
     * Node's nodeName.
     */
    String nodeName;
    /**
     * Node's localName.
     */
    String localName;
    /**
     * Node's nodeValue.
     */
    String nodeValue;
    /**
     * Child count for Container nodes.
     */
    Integer childNodeCount;
    /**
     * Child nodes of this node when requested with children.
     */
    java.util.List<Node> children;
    /**
     * Attributes of the Element node in the form of flat array [name1, value1, name2, value2].
     */
    List<String> attributes;
    /**
     * Document URL that Document or FrameOwner node points to.
     */
    String documentURL;
    /**
     * Base URL that Document or FrameOwner node uses for URL completion.
     */
    String baseURL;
    /**
     * DocumentType's publicId.
     */
    String publicId;
    /**
     * DocumentType's systemId.
     */
    String systemId;
    /**
     * DocumentType's internalSubset.
     */
    String internalSubset;
    /**
     * Document's XML version in case of XML documents.
     */
    String xmlVersion;
    /**
     * Attr's name.
     */
    String name;
    /**
     * Attr's value.
     */
    String value;
    /**
     * Pseudo element type for this node.
     */
    PseudoType pseudoType;
    /**
     * Pseudo element identifier for this node. Only present if there is a valid pseudoType.
     */
    String pseudoIdentifier;
    /**
     * Shadow root type.
     */
    ShadowRootType shadowRootType;
    /**
     * Frame ID for frame owner elements.
     */
    String frameId;
    /**
     * Content document for frame owner elements.
     */
    Node contentDocument;
    /**
     * Shadow root list for given element host.
     */
    List<Node> shadowRoots;
    /**
     * Content document fragment for template elements.
     */
    Node templateContent;
    /**
     * Pseudo elements associated with this node.
     */
    List<Node> pseudoElements;
    /**
     * Deprecated, as the HTML Imports API has been removed (crbug.com/937746). This property used to return the imported document for the HTMLImport links. The property is always undefined now. DEPRECATED
     */
    Node importedDocument;
    /**
     * Distributed nodes for given insertion point.
     */
    List<BackendNode> distributedNodes;

    /**
     * Whether the node is SVG.
     */
    boolean isSVG;
    CompatibilityMode compatibilityMode;
    BackendNode assignedSlot;

  }

  /**
   * ContainerSelector physical axes
   * Allowed Values: Horizontal, Vertical, Both
   */
  public enum PhysicalAxes {
    Horizontal, Vertical, Both
  }

  /**
   * Pseudo element type.
   * Allowed Values: first-line, first-letter, before, after, marker, backdrop, selection, target-text, spelling-error, grammar-error, highlight, first-line-inherited, scrollbar, scrollbar-thumb, scrollbar-button, scrollbar-track, scrollbar-track-piece, scrollbar-corner, resizer, input-list-button, view-transition, view-transition-group, view-transition-image-pair, view-transition-old, view-transition-new
   */
  public enum PseudoType {
    first_line,
    first_letter,
    before,
    after,
    marker,
    backdrop,
    selection,
    target_text,
    spelling_error,
    grammar_error,
    highlight,
    first_line_inherited,
    scrollbar,
    scrollbar_thumb,
    scrollbar_button,
    scrollbar_track,
    scrollbar_track_piece,
    scrollbar_corner,
    resizer,
    input_list_button,
    view_transition,
    view_transition_group,
    view_transition_image_pair,
    view_transition_old,
    view_transition_new
  }

  /**
   * An array of quad vertices, x immediately followed by y for each point, points clock-wise.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class Quad extends JSONArray {
  }

  @Data
  public class Rect {
    /**
     * X coordinate
     */
    Number x;
    /**
     * Y coordinate
     */
    Number y;
    /**
     * Rectangle width
     */
    Number width;
    /**
     * Rectangle height
     */
    Number height;
  }

  public class RGBA extends Color {

    public RGBA(int r, int g, int b) {
      super(r, g, b);
    }

    public RGBA(int r, int g, int b, int a) {
      super(r, g, b, a);
    }

    public RGBA(int rgb) {
      super(rgb);
    }

    public RGBA(int rgba, boolean hasalpha) {
      super(rgba, hasalpha);
    }

    public RGBA(float r, float g, float b) {
      super(r, g, b);
    }

    public RGBA(float r, float g, float b, float a) {
      super(r, g, b, a);
    }

    public RGBA(ColorSpace cspace, float[] components, float alpha) {
      super(cspace, components, alpha);
    }
  }

  /**
   * Shadow root type.
   * Allowed Values: user-agent, open, closed
   */
  public enum ShadowRootType {
    user_agent, open, closed
  }

  /**
   * CSS Shape Outside details.
   */
  @Data
  public class ShapeOutsideInfo {
    /**
     * Shape bounds
     */
    Quad bounds;
    /**
     * Shape coordinate details
     */
    JSONArray shape;
    /**
     * Margin shape bounds
     */
    JSONArray marginShape;
  }
}
