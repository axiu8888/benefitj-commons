package com.benefitj.jpuppeteer.chromium;

import cn.hutool.json.JSONArray;
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
   * Called when shadow root is popped from the element.
   *
   * @param hostId Host element id.
   * @param rootId Shadow root id.
   */
  void shadowRootPopped(Serializable hostId, Serializable rootId);

  /**
   * Called when shadow root is pushed into the element.
   *
   * @param hostId Host element id.
   * @param rootId Shadow root id.
   */
  void shadowRootPushed(Serializable hostId, Serializable rootId);

  /**
   * Called when top layer elements are changed.
   *
   * @param hostId Host element id.
   * @param rootId Shadow root id.
   */
  void topLayerElementsUpdated(Serializable hostId, Serializable rootId);


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
