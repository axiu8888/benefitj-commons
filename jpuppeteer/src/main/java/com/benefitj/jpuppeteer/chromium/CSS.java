package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import netscape.javascript.JSObject;

import java.util.List;

/**
 * This domain exposes CSS read/write operations. All CSS objects (stylesheets, rules, and styles) have an associated id
 * used in subsequent operations on the related object. Each object type has a specific id structure, and those are not
 * interchangeable between objects of different kinds. CSS objects can be loaded using the get*ForNode() calls (which
 * accept a DOM node id). A client can also keep track of stylesheets via the styleSheetAdded/styleSheetRemoved events
 * and subsequently load the required stylesheet contents using the getStyleSheet[Text]() methods. EXPERIMENTAL
 */
@ChromiumApi("CSS")
public interface CSS {

  /**
   * Inserts a new rule with the given ruleText in a stylesheet with given styleSheetId, at the position specified by location.
   *
   * @param styleSheetId StyleSheetId
   *                     The css style sheet identifier where a new rule should be inserted.
   * @param ruleText     string
   *                     The text of a new rule.
   * @param location     SourceRange
   *                     Text position of a new rule in the target style sheet.
   * @return {
   * rule: CSSRule  The newly created rule.
   * }
   */
  JSObject addRule(String styleSheetId, String ruleText, SourceRange location);

  /**
   * Returns all class names from specified stylesheet.
   *
   * @param styleSheetId StyleSheetId
   * @return {
   * classNames: array[ string ]  Class name list.
   * }
   */
  JSObject collectClassNames(String styleSheetId);

  /**
   * Creates a new special "via-inspector" stylesheet in the frame with given frameId.
   *
   * @param frameId Page.FrameId
   *                Identifier of the frame where "via-inspector" stylesheet should be created.
   * @return {
   * frameId: Page.FrameId  Identifier of the frame where "via-inspector" stylesheet should be created.
   * }
   */
  JSObject createStyleSheet(String frameId);

  /**
   * Disables the CSS agent for the given page.
   */
  void disable();

  /**
   * Enables the CSS agent for the given page. Clients should not assume that the CSS agent has been enabled until the result of this command is received.
   */
  void enable();

  /**
   * Ensures that the given node will have specified pseudo-classes whenever its style is computed by the browser.
   *
   * @param nodeId              DOM.NodeId
   *                            The element id for which to force the pseudo state.
   * @param forcedPseudoClasses array[ string ]
   *                            Element pseudo classes to force when computing the element's style.
   */
  void forcePseudoState(String nodeId, List<String> forcedPseudoClasses);

  /**
   * Id of the node to get background colors for.
   *
   * @param backgroundColors   array[ string ]
   *                           The range of background colors behind this element, if it contains any visible text. If no visible text is present, this will be undefined. In the case of a flat background color, this will consist of simply that color. In the case of a gradient, this will consist of each of the color stops. For anything more complicated, this will be an empty array. Images will be ignored (as if the image had failed to load).
   * @param computedFontSize   string
   *                           The computed font size for this node, as a CSS computed value string (e.g. '12px').
   * @param computedFontWeight string
   *                           The computed font weight for this node, as a CSS computed value string (e.g. 'normal' or '100').
   */
  void getBackgroundColors(String backgroundColors, String computedFontSize, String computedFontWeight);

  /**
   * Returns the computed style for a DOM node identified by nodeId.
   *
   * @param nodeId
   * @return {
   * computedStyle array[ CSSComputedStyleProperty ] Computed style for the specified DOM node.
   * }
   */
  JSObject getComputedStyleForNode(String nodeId);

  /**
   * Returns the styles defined inline (explicitly in the "style" attribute and implicitly, using DOM attributes) for a DOM node identified by nodeId.
   *
   * @param nodeId
   * @return {
   * inlineStyle: CSSStyle  Inline style for the specified DOM node.
   * attributesStyle:  CSSStyle  Attribute-defined element style (e.g. resulting from "width=20 height=100%").
   * }
   */
  JSObject getInlineStylesForNode(String nodeId);

  /**
   * Returns requested styles for a DOM node identified by nodeId.
   *
   * @param nodeId DOM.NodeId
   * @return {
   * inlineStyle: CSSStyle Inline style for the specified DOM node.
   * attributesStyle: CSSStyle Attribute-defined element style (e.g. resulting from "width=20 height=100%").
   * matchedCSSRules: array[ RuleMatch ]  CSS rules matching this node, from all applicable stylesheets.
   * pseudoElements: array[ PseudoElementMatches ]  Pseudo style matches for this node.
   * inherited: array[ InheritedStyleEntry ]  A chain of inherited styles (from the immediate node parent up to the DOM tree root).
   * inheritedPseudoElements: array[ InheritedPseudoElementMatches ]  A chain of inherited pseudo element styles (from the immediate node parent up to the DOM tree root).
   * cssKeyframesRules: array[ CSSKeyframesRule ]  A list of CSS keyframed animations matching this node.
   * cssPositionFallbackRules: array[ CSSPositionFallbackRule ]  A list of CSS position fallbacks matching this node.
   * cssPropertyRules: array[ CSSPropertyRule ]  A list of CSS at-property rules matching this node.
   * cssPropertyRegistrations:array[ CSSPropertyRegistration ]  A list of CSS property registrations matching this node.
   * parentLayoutNodeId: DOM.NodeId  Id of the first parent element that does not have display: contents.
   * }
   */
  JSObject getMatchedStylesForNode(String nodeId);

  /**
   * Returns all media queries parsed by the rendering engine.
   *
   * @param medias array[ CSSMedia ]
   */
  void getMediaQueries(List<CSSMedia> medias);

  /**
   * Requests information about platform fonts which we used to render child TextNodes in the given node.
   *
   * @param nodeId
   */
  void getPlatformFontsForNode(String nodeId);

  /**
   * Returns the current textual content for a stylesheet.
   *
   * @param styleSheetId StyleSheetId
   * @return {
   * text: string  The stylesheet text.
   * }
   */
  JSObject getStyleSheetText(String styleSheetId);

  /**
   * Find a rule with the given active property for the given node and set the new value for this property
   *
   * @param nodeId       DOM.NodeId
   *                     The element id for which to set property.
   * @param propertyName string
   * @param value        string
   */
  JSObject setEffectivePropertyValueForNode(String nodeId, String propertyName, String value);

  /**
   * Modifies the keyframe rule key text.
   *
   * @param styleSheetId StyleSheetId
   * @param range        SourceRange
   * @param keyText      string
   * @return {
   * keyText: Value  The resulting key text after modification.
   * }
   */
  JSObject setKeyframeKey(String styleSheetId, SourceRange range, String keyText);

  /**
   * Modifies the rule selector.
   *
   * @param styleSheetId StyleSheetId
   * @param range        SourceRange
   * @param text         string
   * @return {
   * media: CSSMedia  The resulting CSS media rule after modification.
   * }
   */
  JSObject setMediaText(String styleSheetId, SourceRange range, String text);

  /**
   * Modifies the rule selector.
   *
   * @param styleSheetId StyleSheetId
   * @param range        SourceRange
   * @param selector     string
   * @return {
   * selectorList: SelectorList  The resulting selector list after modification.
   * }
   */
  JSObject setRuleSelector(String styleSheetId, SourceRange range, String selector);

  /**
   * Sets the new stylesheet text.
   *
   * @param styleSheetId StyleSheetId
   * @param text         string
   * @return {
   * sourceMapURL: string URL of source map associated with script (if any).
   * }
   */
  JSObject setStyleSheetText(String styleSheetId, String text);

  /**
   * Applies specified style edits one after another in the given order.
   *
   * @param edits array[ StyleDeclarationEdit ]
   * @return {
   * styles: array[ CSSStyle ]  The resulting styles after modification.
   * }
   */
  JSObject setStyleTexts(List<StyleDeclarationEdit> edits);

  /**
   * Enables the selector recording.
   */
  void startRuleUsageTracking();

  /**
   * Stop tracking rule usage and return the list of rules that were used since last call to takeCoverageDelta (or since start of coverage instrumentation).
   *
   * @return {
   * ruleUsage: array[ RuleUsage ]
   * }
   */
  JSObject stopRuleUsageTracking();

  /**
   * Obtain list of rules that became used since last call to this method (or since start of coverage instrumentation).
   *
   * @param coverage  array[ RuleUsage ]
   * @param timestamp number
   *                  Monotonically increasing time, in seconds.
   */
  JSObject takeCoverageDelta(List<RuleUsage> coverage, Long timestamp);

  /**
   * Returns all layers parsed by the rendering engine for the tree scope of a node. Given a DOM element identified by nodeId,
   * getLayersForNode returns the root layer for the nearest ancestor document or shadow root. The layer root contains the
   * full layer tree for the tree scope and their ordering.
   *
   * @param nodeId DOM.NodeId
   * @return {
   * rootLayer: CSSLayerData
   * }
   */
  JSObject getLayersForNode(String nodeId);

  /**
   * Modifies the expression of a container query.
   *
   * @param styleSheetId StyleSheetId
   * @param range        SourceRange
   * @param text         string
   * @return {
   * containerQuery: CSSContainerQuery  The resulting CSS container query rule after modification.
   * }
   */
  JSObject setContainerQueryText(String styleSheetId, SourceRange range, String text);

  /**
   * Enables/disables rendering of local CSS fonts (enabled by default).
   *
   * @param enabled boolean
   *                Whether rendering of local fonts is enabled.
   */
  JSObject setLocalFontsEnabled(Boolean enabled);

  /**
   * Modifies the expression of a scope at-rule.
   *
   * @param styleSheetId StyleSheetId
   * @param range        SourceRange
   * @param text         string
   * @return {
   * scope: CSSScope The resulting CSS Scope rule after modification.
   * }
   */
  JSObject setScopeText(String styleSheetId, SourceRange range, String text);

  /**
   * Modifies the expression of a supports at-rule.
   *
   * @param styleSheetId StyleSheetId
   * @param range        SourceRange
   * @param text         string
   * @return {
   * supports: CSSSupports  The resulting CSS Supports rule after modification.
   * }
   */
  JSObject setSupportsText(String styleSheetId, SourceRange range, String text);

  /**
   * Polls the next batch of computed style updates.
   *
   * @return {
   * nodeIds: array[ DOM.NodeId ]  The list of node Ids that have their tracked computed styles updated.
   * }
   */
  JSObject takeComputedStyleUpdates(List<String> nodeIds);

  /**
   * Starts tracking the given computed styles for updates. The specified array of properties replaces the one previously specified.
   * Pass empty array to disable tracking. Use takeComputedStyleUpdates to retrieve the list of nodes that had properties modified.
   * The changes to computed style properties are only tracked for nodes pushed to the front-end by the DOM agent. If no changes to
   * the tracked properties occur after the node has been pushed to the front-end, no updates will be issued for the node.
   *
   * @param propertiesToTrack array[ CSSComputedStyleProperty ]
   */
  JSObject trackComputedStyleUpdates(List<CSSComputedStyleProperty> propertiesToTrack);

  /**
   * 事件
   */
  @Event("CSS")
  public interface Events {
    /**
     * Fires whenever a web font is updated. A non-empty font parameter indicates a successfully loaded web font.
     *
     * @param font FontFace
     *             The web font that has loaded.
     */
    @Event("fontsUpdated")
    void fontsUpdated(FontFace font);

    /**
     * Fires whenever a MediaQuery result changes (for example, after a browser window has been resized.) The current
     * implementation considers only viewport-dependent media features.
     */
    @Event("mediaQueryResultChanged")
    void mediaQueryResultChanged();

    /**
     * Fired whenever an active document stylesheet is added.
     *
     * @param header CSSStyleSheetHeader
     *               Added stylesheet metainfo.
     */
    @Event("styleSheetAdded")
    void styleSheetAdded(CSSStyleSheetHeader header);

    /**
     * Fired whenever a stylesheet is changed as a result of the client operation.
     *
     * @param styleSheetId StyleSheetId
     */
    @Event("styleSheetChanged")
    void styleSheetChanged(String styleSheetId);

    /**
     * Fired whenever an active document stylesheet is removed.
     *
     * @param styleSheetId StyleSheetId
     *                     Identifier of the removed stylesheet.
     */
    @Event("styleSheetRemoved")
    void styleSheetRemoved(String styleSheetId);
  }

  /**
   *
   */
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
   * CSS keyframe rule representation.
   */
  @Data
  public class CSSKeyframeRule {
    /**
     * The css style sheet identifier (absent for user agent stylesheet and user-specified stylesheet rules) this rule came from.
     * <p>
     * StyleSheetId
     */
    String styleSheetId;
    /**
     * Parent stylesheet's origin.
     */
    StyleSheetOrigin origin;
    /**
     * Associated key text.
     */
    Value keyText;
    /**
     * Associated style declaration.
     */
    CSSStyle style;
  }

  /**
   * CSS keyframes rule representation.
   */
  @Data
  public class CSSKeyframesRule {
    /**
     * Animation name.
     */
    Value animationName;
    /**
     * array[ CSSKeyframeRule ]
     * List of keyframes.
     */
    List<CSSKeyframeRule> keyframes;
  }

  /**
   * CSS media rule descriptor.
   */
  @Data
  public class CSSMedia {
    /**
     * Media query text.
     */
    String text;
    /**
     * Source of the media query: "mediaRule" if specified by a @media rule, "importRule" if specified by an @import rule, "linkedSheet" if specified by a "media" attribute in a linked stylesheet's LINK tag, "inlineSheet" if specified by a "media" attribute in an inline stylesheet's STYLE tag.
     * Allowed Values: mediaRule, importRule, linkedSheet, inlineSheet
     */
    String source;
    /**
     * URL of the document containing the media query description.
     */
    String sourceURL;
    /**
     * The associated rule (@media or @import) header range in the enclosing stylesheet (if available).
     */
    SourceRange range;
    /**
     * StyleSheetId
     * Identifier of the stylesheet containing this object (if exists).
     */
    String styleSheetId;
    /**
     * array[ MediaQuery ]
     * Array of media queries.
     */
    List<MediaQuery> mediaList;
  }

  /**
   * CSS position-fallback rule representation.
   */
  @Data
  public class CSSPositionFallbackRule {
    Value name;
    /**
     * array[ CSSTryRule ]
     * List of keyframes.
     */
    List<CSSTryRule> tryRules;
  }

  /**
   * CSS property declaration data.
   */
  @Data
  public class CSSProperty {
    /**
     * The property name.
     */
    String name;
    /**
     * The property value.
     */
    String value;
    /**
     * Whether the property has "!important" annotation (implies false if absent).
     */
    boolean important;
    /**
     * Whether the property is implicit (implies false if absent).
     */
    boolean implicit;
    /**
     * The full property text as specified in the style.
     */
    String text;
    /**
     * Whether the property is understood by the browser (implies true if absent).
     */
    boolean parsedOk;
    /**
     * Whether the property is disabled by the user (present for source-based properties only).
     */
    boolean disabled;
    /**
     * The entire property range in the enclosing style declaration (if available).
     */
    SourceRange range;
    /**
     * array[ CSSProperty ]
     * Parsed longhand components of this property if it is a shorthand. This field will be empty if the given property is not a shorthand.
     */
    List<CSSProperty> longhandProperties;
  }

  /**
   * Representation of a custom property registration through CSS.registerProperty
   */
  @Data
  public class CSSPropertyRegistration {
    String propertyName;
    Value initialValue;
    boolean inherits;
    String syntax;
  }

  /**
   * CSS property at-rule representation.
   */
  @Data
  public class CSSPropertyRule {
    /**
     * StyleSheetId
     * The css style sheet identifier (absent for user agent stylesheet and user-specified stylesheet rules) this rule came from.
     */
    String styleSheetId;
    /**
     * Parent stylesheet's origin.
     */
    StyleSheetOrigin origin;
    /**
     * Associated property name.
     */
    Value propertyName;
    /**
     * Associated style declaration.
     */
    CSSStyle style;
  }

  /**
   * CSS rule representation.
   */
  @Data
  public class CSSRule {
    /**
     * StyleSheetId
     * The css style sheet identifier (absent for user agent stylesheet and user-specified stylesheet rules) this rule came from.
     */
    String styleSheetId;
    /**
     * Rule selector data.
     */
    SelectorList selectorList;
    /**
     * array[ string ]
     * Array of selectors from ancestor style rules, sorted by distance from the current rule. EXPERIMENTAL
     */
    List<String> nestingSelectors;
    /**
     * Parent stylesheet's origin.
     */
    StyleSheetOrigin origin;
    /**
     * Associated style declaration.
     */
    CSSStyle style;
    /**
     * Media list array (for rules involving media queries). The array enumerates media queries starting with the innermost one, going outwards.
     * array[ CSSMedia ]
     */
    List<CSSMedia> media;
    /**
     * Container query list array (for rules involving container queries). The array enumerates container queries starting with the innermost one, going outwards. EXPERIMENTAL
     * array[ CSSContainerQuery ]
     */
    List<CSSContainerQuery> containerQueries;
    /**
     * @supports CSS at-rule array. The array enumerates @supports at-rules starting with the innermost one, going outwards. EXPERIMENTAL
     * array[ CSSSupports ]
     */
    List<CSSSupports> supports;
    /**
     * array[ CSSLayer ]
     * Cascade layer array. Contains the layer hierarchy that this rule belongs to starting with the innermost layer and going outwards. EXPERIMENTAL
     */
    List<CSSLayer> layers;
    /**
     * array[ CSSScope ]
     *
     * @scope CSS at-rule array. The array enumerates @scope at-rules starting with the innermost one, going outwards. EXPERIMENTAL
     */
    List<CSSRuleType> scopes;

    /**
     * The array keeps the types of ancestor CSSRules from the innermost going outwards.
     * array[ CSSRuleType ]
     */
    List<CSSRuleType> ruleTypes;
  }

  /**
   * CSS style representation.
   */
  @Data
  public class CSSStyle {
    /**
     * The css style sheet identifier (absent for user agent stylesheet and user-specified stylesheet rules) this rule came from.
     * StyleSheetId
     */
    String styleSheetId;
    /**
     * CSS properties in the style.
     * array[ CSSProperty ]
     */
    List<CSSProperty> cssProperties;
    /**
     * Computed values for all shorthands found in the style.
     * array[ ShorthandEntry ]
     */
    List<ShorthandEntry> shorthandEntries;
    /**
     * Style declaration text (if available).
     */
    String cssText;
    /**
     * Style declaration range in the enclosing stylesheet (if available).
     */
    SourceRange range;
  }

  /**
   * CSS stylesheet metainformation.
   */
  @Data
  public class CSSStyleSheetHeader {
    /**
     * StyleSheetId
     * The stylesheet identifier.
     */
    String styleSheetId;
    /**
     * Page.FrameId
     * Owner frame identifier.
     */
    String frameId;
    /**
     * Stylesheet resource URL. Empty if this is a constructed stylesheet created using new CSSStyleSheet() (but non-empty if this is a constructed sylesheet imported as a CSS module script).
     */
    String sourceURL;
    /**
     * URL of source map associated with the stylesheet (if any).
     */
    String sourceMapURL;
    /**
     * Stylesheet origin.
     */
    StyleSheetOrigin origin;
    /**
     * Stylesheet title.
     */
    String title;
    /**
     * The backend id for the owner node of the stylesheet.
     * DOM.BackendNodeId
     */
    String ownerNode;
    /**
     * Denotes whether the stylesheet is disabled.
     */
    boolean disabled;
    /**
     * Whether the sourceURL field value comes from the sourceURL comment.
     */
    boolean hasSourceURL;
    /**
     * Whether this stylesheet is created for STYLE tag by parser. This flag is not set for document.written STYLE tags.
     */
    boolean isInline;
    /**
     * Whether this stylesheet is mutable. Inline stylesheets become mutable after they have been modified via CSSOM API. <link> element's stylesheets become mutable only if DevTools modifies them. Constructed stylesheets (new CSSStyleSheet()) are mutable immediately after creation.
     */
    boolean isMutable;
    /**
     * True if this stylesheet is created through new CSSStyleSheet() or imported as a CSS module script.
     */
    boolean isConstructed;
    /**
     * Line offset of the stylesheet within the resource (zero based).
     */
    Integer startLine;
    /**
     * Column offset of the stylesheet within the resource (zero based).
     */
    Integer startColumn;
    /**
     * Size of the content (in characters).
     */
    Long length;
    /**
     * Line offset of the end of the stylesheet within the resource (zero based).
     */
    Integer endLine;
    /**
     * Column offset of the end of the stylesheet within the resource (zero based).
     */
    Integer endColumn;
    /**
     * If the style sheet was loaded from a network resource, this indicates when the resource failed to load
     */
    boolean loadingFailed;
  }

  /**
   * CSS try rule representation.
   */
  @Data
  public class CSSTryRule {
    /**
     * StyleSheetId
     * The css style sheet identifier (absent for user agent stylesheet and user-specified stylesheet rules) this rule came from.
     */
    String styleSheetId;
    /**
     * Parent stylesheet's origin.
     */
    StyleSheetOrigin origin;
    /**
     * Associated style declaration.
     */
    CSSStyle style;
  }

  /**
   * Properties of a web font: https://www.w3.org/TR/2008/REC-CSS2-20080411/fonts.html#font-descriptions
   * and additional information such as platformFontFamily and fontVariationAxes.
   */
  @Data
  public class FontFace {
    /**
     * The font-family.
     */
    String fontFamily;
    /**
     * The font-style.
     */
    String fontStyle;
    /**
     * The font-variant.
     */
    String fontVariant;
    /**
     * The font-weight.
     */
    String fontWeight;
    /**
     * The font-stretch.
     */
    String fontStretch;
    /**
     * The font-display.
     */
    String fontDisplay;
    /**
     * The unicode-range.
     */
    String unicodeRange;
    /**
     * The src.
     */
    String src;
    /**
     * The resolved platform font family
     */
    String platformFontFamily;
    /**
     * Available variation settings (a.k.a. "axes").
     * array[ FontVariationAxis ]
     */
    List<FontVariationAxis> fontVariationAxes;
  }

  /**
   * Information about font variation axes for variable fonts
   */
  @Data
  public class FontVariationAxis {
    /**
     * The font-variation-setting tag (a.k.a. "axis tag").
     */
    String tag;
    /**
     * Human-readable variation name in the default language (normally, "en").
     */
    String name;
    /**
     * The minimum value (inclusive) the font supports for this tag.
     */
    Number minValue;
    /**
     * The maximum value (inclusive) the font supports for this tag.
     */
    Number maxValue;
    /**
     * The default value.
     */
    Number defaultValue;
  }

  /**
   * Inherited pseudo element matches from pseudos of an ancestor node.
   */
  @Data
  public class InheritedPseudoElementMatches {
    /**
     * Matches of pseudo styles from the pseudos of an ancestor node.
     * array[ PseudoElementMatches ]
     */
    List<String> pseudoElements;
  }

  /**
   * Inherited CSS rule collection from ancestor node.
   */
  @Data
  public class InheritedStyleEntry {
    /**
     * The ancestor node's inline style, if any, in the style inheritance chain.
     */
    CSSStyle inlineStyle;
    /**
     * Matches of CSS rules matching the ancestor node in the style inheritance chain.
     * array[ RuleMatch ]
     */
    List<RuleMatch> matchedCSSRules;
  }

  /**
   * Media query descriptor.
   */
  @Data
  public class MediaQuery {
    /**
     * Array of media query expressions.
     * array[ MediaQueryExpression ]
     */
    List<MediaQueryExpression> expressions;
    /**
     * Whether the media query condition is satisfied.
     */
    boolean active;
  }

  /**
   * Media query expression descriptor.
   */
  @Data
  public class MediaQueryExpression {
    /**
     * Media query expression value.
     */
    Number value;
    /**
     * Media query expression units.
     */
    String unit;
    /**
     * Media query expression feature.
     */
    String feature;
    /**
     * The associated range of the value text in the enclosing stylesheet (if available).
     */
    SourceRange valueRange;
    /**
     * Computed length of media query expression (if applicable)
     */
    Long computedLength;
  }

  /**
   * Information about amount of glyphs that were rendered with given font.
   */
  @Data
  public class PlatformFontUsage {
    /**
     * Font's family name reported by platform.
     */
    String familyName;
    /**
     * Indicates if the font was downloaded or resolved locally.
     */
    boolean isCustomFont;
    /**
     * Amount of glyphs that were rendered with this font.
     */
    Number glyphCount;
  }

  /**
   * CSS rule collection for a single pseudo style.
   */
  @Data
  public class PseudoElementMatches {
    /**
     * Pseudo element type.
     */
    DOM.PseudoType pseudoType;
    /**
     * Pseudo element custom ident.
     */
    String pseudoIdentifier;
    /**
     * Matches of CSS rules applicable to the pseudo style.
     * array[ RuleMatch ]
     */
    List<RuleMatch> matches;
  }

  /**
   * Match data for a CSS rule.
   */
  @Data
  public class RuleMatch {
    /**
     * CSS rule in the match.
     */
    CSSRule rule;
    /**
     * Matching selector indices in the rule's selectorList selectors (0-based).
     * array[ integer ]
     */
    List<Integer> matchingSelectors;
  }

  /**
   * CSS coverage information.
   */
  @Data
  public class RuleUsage {
    /**
     * The css style sheet identifier (absent for user agent stylesheet and user-specified stylesheet rules) this rule came from.
     * StyleSheetId
     */
    String styleSheetId;
    /**
     * Offset of the start of the rule (including selector) from the beginning of the stylesheet.
     */
    Integer startOffset;
    /**
     * Offset of the end of the rule body from the beginning of the stylesheet.
     */
    Integer endOffset;
    /**
     * Indicates whether the rule was actually used by some element in the page.
     */
    boolean used;
  }

  /**
   * Selector list data.
   */
  @Data
  public class SelectorList {
    /**
     * array[ Value ]
     * Selectors in the list.
     */
    List<Value> selectors;
    /**
     * Rule selector text.
     */
    String text;
  }

  /**
   *
   */
  @Data
  public class ShorthandEntry {
    /**
     * Shorthand name.
     */
    String name;
    /**
     * Shorthand value.
     */
    String value;
    /**
     * Whether the property has "!important" annotation (implies false if absent).
     */
    boolean important;
  }

  /**
   * Text range within a resource. All numbers are zero-based.
   */
  @Data
  public class SourceRange {
    /**
     * Start line of range.
     */
    Integer startLine;
    /**
     * Start column of range (inclusive).
     */
    Integer startColumn;
    /**
     * End line of range
     */
    Integer endLine;
    /**
     * End column of range (exclusive).
     */
    Integer endColumn;
  }

  /**
   * A descriptor of operation to mutate style declaration text.
   */
  @Data
  public class StyleDeclarationEdit {
    /**
     * The css style sheet identifier.
     * StyleSheetId
     */
    String styleSheetId;
    /**
     * The range of the style text in the enclosing stylesheet.
     */
    SourceRange range;
    /**
     * New style text.
     */
    String text;
  }

  /**
   * Stylesheet type: "injected" for stylesheets injected via extension, "user-agent" for user-agent stylesheets, "inspector" for stylesheets created by the inspector (i.e. those holding the "via inspector" rules), "regular" for regular stylesheets.
   * Allowed Values: injected, user-agent, inspector, regular
   */
  public enum StyleSheetOrigin {
    injected, user_agent, inspector, regular
  }

  /**
   * Data for a simple selector (these are delimited by commas in a selector list).
   */
  @Data
  public class Value {
    /**
     * Value text.
     */
    String text;
    /**
     * Value range in the underlying resource (if available).
     */
    SourceRange range;
    /**
     * Specificity of the selector.
     */
    Specificity specificity;
  }

  /**
   * CSS container query rule descriptor.
   */
  @Data
  public class CSSContainerQuery {
    /**
     * Container query text.
     */
    String text;
    /**
     * The associated rule header range in the enclosing stylesheet (if available).
     */
    SourceRange range;
    /**
     * Identifier of the stylesheet containing this object (if exists).
     * <p>
     * StyleSheetId
     */
    String styleSheetId;
    /**
     * Optional name for the container.
     */
    String name;
    /**
     * Optional physical axes queried for the container.
     */
    DOM.PhysicalAxes physicalAxes;
    /**
     * Optional logical axes queried for the container.
     */
    DOM.LogicalAxes logicalAxes;
  }

  /**
   * CSS Layer at-rule descriptor.
   */
  @Data
  public class CSSLayer {
    /**
     * Layer name.
     */
    String text;
    /**
     * The associated rule header range in the enclosing stylesheet (if available).
     */
    SourceRange range;
    /**
     * Identifier of the stylesheet containing this object (if exists).
     * StyleSheetId
     */
    String styleSheetId;
  }

  /**
   * CSS Layer data.
   */
  @Data
  public class CSSLayerData {
    /**
     * Layer name.
     */
    String name;
    /**
     * Direct sub-layers
     * array[ CSSLayerData ]
     */
    List<CSSLayerData> subLayers;
    /**
     * Layer order. The order determines the order of the layer in the cascade order. A higher number has higher priority in the cascade order.
     */
    Integer order;
  }

  /**
   * Enum indicating the type of a CSS rule, used to represent the order of a style rule's ancestors. This list only contains rule types that are collected during the ancestor rule collection.
   * Allowed Values: MediaRule, SupportsRule, ContainerRule, LayerRule, ScopeRule, StyleRule
   */
  public enum CSSRuleType {
    MediaRule, SupportsRule, ContainerRule, LayerRule, ScopeRule, StyleRule
  }

  /**
   * CSS Scope at-rule descriptor.
   */
  @Data
  public class CSSScope {
    /**
     * Scope rule text.
     */
    String text;
    /**
     * The associated rule header range in the enclosing stylesheet (if available).
     */
    SourceRange range;
    /**
     * Identifier of the stylesheet containing this object (if exists).
     * StyleSheetId
     */
    String styleSheetId;
  }

  /**
   * CSS Supports at-rule descriptor.
   */
  @Data
  public class CSSSupports {
    /**
     * Supports rule text.
     */
    String text;
    /**
     * Whether the supports condition is satisfied.
     */
    boolean active;
    /**
     * The associated rule header range in the enclosing stylesheet (if available).
     */
    SourceRange range;
    /**
     * Identifier of the stylesheet containing this object (if exists).
     * <p>
     * StyleSheetId
     */
    String styleSheetId;
  }

  /**
   * Specificity: https://drafts.csswg.org/selectors/#specificity-rules
   */
  @Data
  public class Specificity {
    /**
     * The a component, which represents the number of ID selectors.
     */
    Integer a;
    /**
     * The b component, which represents the number of class selectors, attributes selectors, and pseudo-classes.
     */
    Integer b;
    /**
     * The c component, which represents the number of type selectors and pseudo-elements.
     */
    Integer c;
  }

}
