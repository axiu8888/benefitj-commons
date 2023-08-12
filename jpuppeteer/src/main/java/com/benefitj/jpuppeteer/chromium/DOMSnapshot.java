package com.benefitj.jpuppeteer.chromium;


import cn.hutool.json.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * This domain facilitates obtaining document snapshots with DOM, layout, and style information.
 */
@ChromiumApi("DOMSnapshot")
public interface DOMSnapshot {

  /**
   * Returns a document snapshot, including the full DOM tree of the root node (including iframes, template contents,
   * and imported documents) in a flattened array, as well as layout and white-listed computed style information for the nodes.
   * Shadow DOM in the returned DOM tree is flattened.
   *
   * @param computedStyles                 array[ string ]
   *                                       Whitelist of computed styles to return.
   * @param includePaintOrder              boolean
   *                                       Whether to include layout object paint orders into the snapshot.
   * @param includeDOMRects                boolean
   *                                       Whether to include DOM rectangles (offsetRects, clientRects, scrollRects) into the snapshot
   * @param includeBlendedBackgroundColors boolean
   *                                       Whether to include blended background colors in the snapshot (default: false). Blended background color is achieved by blending background colors of all elements that overlap with the current element. EXPERIMENTAL
   * @param includeTextColorOpacities      boolean
   *                                       Whether to include text color opacity in the snapshot (default: false). An element might have the opacity property set that affects the text color of the element. The final text color opacity is computed based on the opacity of all overlapping elements. EXPERIMENTAL
   * @return {
   * documents: array[ DocumentSnapshot ]  The nodes in the DOM tree. The DOMNode at index 0 corresponds to the root document.
   * strings: array[ string ]  Shared string table that all string properties refer to with indexes.
   * }
   */
  JSONObject captureSnapshot(List<String> computedStyles, Boolean includePaintOrder, Boolean includeDOMRects,
                             Boolean includeBlendedBackgroundColors, Boolean includeTextColorOpacities);

  /**
   * Disables DOM snapshot agent for the given page.
   */
  void disable();

  /**
   * Enables DOM snapshot agent for the given page.
   */
  void enable();

  /**
   * Returns a document snapshot, including the full DOM tree of the root node (including iframes, template contents, and
   * imported documents) in a flattened array, as well as layout and white-listed computed style information for the nodes.
   * Shadow DOM in the returned DOM tree is flattened.
   *
   * @param computedStyleWhitelist     array[ string ]
   *                                   Whitelist of computed styles to return.
   * @param includeEventListeners      boolean
   *                                   Whether or not to retrieve details of DOM listeners (default false).
   * @param includePaintOrder          boolean
   *                                   Whether to determine and include the paint order index of LayoutTreeNodes (default false).
   * @param includeUserAgentShadowTree boolean
   *                                   Whether to include UA shadow tree in the snapshot (default false).
   * @return {
   * domNodes: array[ DOMNode ] The nodes in the DOM tree. The DOMNode at index 0 corresponds to the root document.
   * layoutTreeNodes: array[ LayoutTreeNode ]  The nodes in the layout tree.
   * computedStyles: array[ ComputedStyle ]  Whitelisted ComputedStyle properties for each node in the layout tree.
   * }
   */
  JSONObject getSnapshot(List<String> computedStyleWhitelist, Boolean includeEventListeners, Boolean includePaintOrder, Boolean includeUserAgentShadowTree);

  /**
   * Index of the string in the strings table.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class ArrayOfStrings extends ArrayList<String> {
  }

  /**
   * A subset of the full ComputedStyle as defined by the request whitelist.
   */
  @Data
  public class ComputedStyle {
    /**
     * Name/value pairs of computed style properties.
     * <p>
     * array[ NameValue ]
     */
    List<NameValue> properties;
  }

  /**
   * Document snapshot.
   */
  @Data
  public class DocumentSnapshot {
    /**
     * Document URL that Document or FrameOwner node points to.
     */
    Integer documentURL;
    /**
     * Document title.
     */
    Integer title;
    /**
     * Base URL that Document or FrameOwner node uses for URL completion.
     */
    Integer baseURL;
    /**
     * Contains the document's content language.
     */
    Integer contentLanguage;
    /**
     * Contains the document's character set encoding.
     */
    Integer encodingName;
    /**
     * DocumentType node's publicId.
     */
    Integer publicId;
    /**
     * DocumentType node's systemId.
     */
    Integer systemId;
    /**
     * Frame ID for frame owner elements and also for the document node.
     */
    Integer frameId;
    /**
     * A table with dom nodes.
     */
    NodeTreeSnapshot nodes;
    /**
     * The nodes in the layout tree.
     */
    LayoutTreeSnapshot layout;
    /**
     * The post-layout inline text nodes.
     */
    TextBoxSnapshot textBoxes;
    /**
     * Horizontal scroll offset.
     */
    Integer scrollOffsetX;
    /**
     * Vertical scroll offset.
     */
    Integer scrollOffsetY;
    /**
     * Document content width.
     */
    Integer contentWidth;
    /**
     * Document content height.
     */
    Integer contentHeight;
  }

  /**
   * A Node in the DOM tree.
   */
  @Data
  public class DOMNode {
    /**
     * Node's nodeType.
     */
    Integer nodeType;
    /**
     * Node's nodeName.
     */
    String nodeName;
    /**
     * Node's nodeValue.
     */
    String nodeValue;
    /**
     * Only set for textarea elements, contains the text value.
     */
    String textValue;
    /**
     * Only set for input elements, contains the input's associated text value.
     */
    String inputValue;
    /**
     * Only set for radio and checkbox input elements, indicates if the element has been checked
     */
    boolean inputChecked;
    /**
     * Only set for option elements, indicates if the element has been selected
     */
    boolean optionSelected;
    /**
     * Node's id, corresponds to DOM.Node.backendNodeId.
     * DOM.BackendNodeId
     */
    String backendNodeId;
    /**
     * The indexes of the node's child nodes in the domNodes array returned by getSnapshot, if any.
     * array[ integer ]
     */
    List<Integer> childNodeIndexes;
    /**
     * Attributes of an Element node.
     * array[ NameValue ]
     */
    List<NameValue> attributes;
    /**
     * Indexes of pseudo elements associated with this node in the domNodes array returned by getSnapshot, if any.
     * array[ integer ]
     */
    List<Integer> pseudoElementIndexes;
    /**
     * The index of the node's related layout tree node in the layoutTreeNodes array returned by getSnapshot, if any.
     */
    Integer layoutNodeIndex;
    /**
     * Document URL that Document or FrameOwner node points to.
     */
    String documentURL;
    /**
     * Base URL that Document or FrameOwner node uses for URL completion.
     */
    String baseURL;
    /**
     * Only set for documents, contains the document's content language.
     */
    String contentLanguage;
    /**
     * Only set for documents, contains the document's character set encoding.
     */
    String documentEncoding;
    /**
     * DocumentType node's publicId.
     */
    String publicId;
    /**
     * DocumentType node's systemId.
     */
    String systemId;
    /**
     * Frame ID for frame owner elements and also for the document node.
     * Page.FrameId
     */
    String frameId;
    /**
     * The index of a frame owner element's content document in the domNodes array returned by getSnapshot, if any.
     */
    Integer contentDocumentIndex;
    /**
     * Type of a pseudo element node.
     */
    DOM.PseudoType pseudoType;
    /**
     * Shadow root type.
     */
    DOM.ShadowRootType shadowRootType;
    /**
     * Whether this DOM node responds to mouse clicks. This includes nodes that have had click event listeners attached via JavaScript as well as anchor tags that naturally navigate when clicked.
     */
    boolean isClickable;
    /**
     * Details of the node's event listeners, if any.
     * array[ DOMDebugger.EventListener ]
     */
    List<DOMDebugger.EventListener> eventListeners;
    /**
     * The selected url for nodes with a srcset attribute.
     */
    String currentSourceURL;
    /**
     * The url of the script (if any) that generates this node.
     */
    String originURL;
    /**
     * Scroll offsets, set when this node is a Document.
     */
    Integer scrollOffsetX;
    /**
     *
     */
    Integer scrollOffsetY;
  }

  /**
   * Details of post layout rendered text positions. The exact layout should not be regarded as stable and may change between versions.
   */
  @Data
  public class InlineTextBox {
    /**
     * The bounding box in document coordinates. Note that scroll offset of the document is ignored.
     */
    DOM.Rect boundingBox;
    /**
     * The starting index in characters, for this post layout textbox substring. Characters that would be represented as a surrogate pair in UTF-16 have length 2.
     */
    Integer startCharacterIndex;
    /**
     * The number of characters in this post layout textbox substring. Characters that would be represented as a surrogate pair in UTF-16 have length 2.
     */
    Integer numCharacters;
  }

  /**
   * Details of an element in the DOM tree with a LayoutObject.
   */
  @Data
  public class LayoutTreeNode {
    /**
     * The index of the related DOM node in the domNodes array returned by getSnapshot.
     */
    Integer domNodeIndex;
    /**
     * The bounding box in document coordinates. Note that scroll offset of the document is ignored.
     */
    DOM.Rect boundingBox;
    /**
     * Contents of the LayoutText, if any.
     */
    String layoutText;
    /**
     * The post-layout inline text nodes, if any.
     * array[ InlineTextBox ]
     */
    List<InlineTextBox> inlineTextNodes;
    /**
     * Index into the computedStyles array returned by getSnapshot.
     */
    Integer styleIndex;
    /**
     * Global paint order index, which is determined by the stacking order of the nodes. Nodes that are painted together will have the same index. Only provided if includePaintOrder in getSnapshot was true.
     */
    Integer paintOrder;
    /**
     * Set to true to indicate the element begins a new stacking context.
     */
    boolean isStackingContext;
  }

  /**
   * Table of details of an element in the DOM tree with a LayoutObject.
   */
  @Data
  public class LayoutTreeSnapshot {
    /**
     * Index of the corresponding node in the NodeTreeSnapshot array returned by captureSnapshot.
     * array[ integer ]
     */
    List<Integer> nodeIndex;
    /**
     * Array of indexes specifying computed style strings, filtered according to the computedStyles parameter passed to captureSnapshot.
     * array[ ArrayOfStrings ]
     */
    List<ArrayOfStrings> styles;
    /**
     * The absolute position bounding box.
     * array[ Rectangle ]
     */
    List<Rectangle> bounds;
    /**
     * Contents of the LayoutText, if any.
     * array[ Integer ]
     */
    List<Integer> text;
    /**
     * Stacking context information.
     */
    RareBooleanData stackingContexts;
    /**
     * Global paint order index, which is determined by the stacking order of the nodes. Nodes that are painted together will have the same index. Only provided if includePaintOrder in captureSnapshot was true.
     * array[ integer ]
     */
    List<Integer> paintOrders;
    /**
     * The offset rect of nodes. Only available when includeDOMRects is set to true
     * array[ Rectangle ]
     */
    List<Rectangle> offsetRects;
    /**
     * The scroll rect of nodes. Only available when includeDOMRects is set to true
     * array[ Rectangle ]
     */
    List<Rectangle> scrollRects;
    /**
     * The client rect of nodes. Only available when includeDOMRects is set to true
     * array[ Rectangle ]
     */
    List<Rectangle> clientRects;
    /**
     * The list of background colors that are blended with colors of overlapping elements. EXPERIMENTAL
     * array[ Integer ]
     */
    List<Integer> blendedBackgroundColors;
    /**
     * The list of computed text opacities.
     * array[ number ]
     */
    List<Integer> textColorOpacities;
  }

  /**
   * A name/value pair.
   */
  @Data
  public class NameValue {
    /**
     * Attribute/property name.
     */
    String name;
    /**
     * Attribute/property value.
     */
    String value;
  }

  /**
   * Table containing nodes.
   */
  @Data
  public class NodeTreeSnapshot {
    /**
     * Parent node index.
     * array[ integer ]
     */
    List<Integer> parentIndex;
    /**
     * Node's nodeType.
     * array[ integer ]
     */
    List<Integer> nodeType;
    /**
     * Type of the shadow root the Node is in. String values are equal to the ShadowRootType enum.
     */
    RareStringData shadowRootType;
    /**
     * Node's nodeName.
     * array[ Integer ]
     */
    List<Integer> nodeName;
    /**
     * Node's nodeValue.
     * array[ Integer ]
     */
    List<Integer> nodeValue;
    /**
     * Node's id, corresponds to DOM.Node.backendNodeId.
     * array[ DOM.BackendNodeId ]
     */
    List<String> backendNodeId;
    /**
     * Attributes of an Element node. Flatten name, value pairs.
     * array[ ArrayOfStrings ]
     */
    List<ArrayOfStrings> attributes;
    /**
     * Only set for textarea elements, contains the text value.
     */
    RareStringData textValue;
    /**
     * Only set for input elements, contains the input's associated text value.
     */
    RareStringData inputValue;
    /**
     * Only set for radio and checkbox input elements, indicates if the element has been checked
     */
    RareBooleanData inputChecked;
    /**
     * Only set for option elements, indicates if the element has been selected
     */
    RareBooleanData optionSelected;
    /**
     * The index of the document in the list of the snapshot documents.
     */
    RareIntegerData contentDocumentIndex;
    /**
     * Type of a pseudo element node.
     */
    RareStringData pseudoType;
    /**
     * Pseudo element identifier for this node. Only present if there is a valid pseudoType.
     */
    RareStringData pseudoIdentifier;
    /**
     * Whether this DOM node responds to mouse clicks. This includes nodes that have had click event listeners attached via JavaScript as well as anchor tags that naturally navigate when clicked.
     */
    RareBooleanData isClickable;
    /**
     * The selected url for nodes with a srcset attribute.
     */
    RareStringData currentSourceURL;
    /**
     * The url of the script (if any) that generates this node.
     */
    RareStringData originURL;
  }

  /**
   *
   */
  @Data
  public class RareBooleanData {
    /**
     * array[ integer ]
     */
    List<Integer> index;
  }

  /**
   *
   */
  @Data
  public class RareIntegerData {
    List<Integer> index;
    List<Integer> value;
  }

  /**
   * Data that is only present on rare nodes.
   */
  @Data
  public class RareStringData {
    List<Integer> index;
    List<Integer> value;
  }

  /**
   *
   */
  public class Rectangle extends JSONArray {
  }

  /**
   * Table of details of the post layout rendered text positions. The exact layout should not be regarded as stable and may change between versions.
   */
  @Data
  public class TextBoxSnapshot {
    /**
     * Index of the layout tree node that owns this box collection.
     * array[ integer ]
     */
    List<Integer> layoutIndex;
    /**
     * The absolute position bounding box.
     * array[ Rectangle ]
     */
    List<Rectangle> bounds;
    /**
     * The starting index in characters, for this post layout textbox substring. Characters that would be represented as a surrogate pair in UTF-16 have length 2.
     * array[ integer ]
     */
    List<Integer> start;
    /**
     * The number of characters in this post layout textbox substring. Characters that would be represented as a surrogate pair in UTF-16 have length 2.
     * array[ integer ]
     */
    List<Integer> length;
  }

}
