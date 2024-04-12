package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import org.apache.commons.io.input.BOMInputStream;

import java.util.List;

/**
 * Audits domain allows investigation of page violations and possible improvements.
 */
@ChromiumApi("Audits")
public interface Audits {

  /**
   * Runs the contrast check for the target page. Found issues are reported using Audits.issueAdded event.
   *
   * @param reportAAA boolean
   *                  Whether to report WCAG AAA level issues. Default is false.
   */
  void checkContrast(String reportAAA);

  /**
   * Runs the form issues check for the target page. Found issues are reported using Audits.issueAdded event.
   *
   * @return {
   * formIssues: array[ GenericIssueDetails ]
   * }
   */
  JSONObject checkFormsIssues(String reportAAA);

  /**
   * Disables issues domain, prevents further issues from being reported to the client.
   */
  void disable();

  /**
   * Enables issues domain, sends the issues collected so far to the client by means of the issueAdded event.
   */
  void enable();

  /**
   * Returns the response body and size if it were re-encoded with the specified settings. Only applies to images.
   *
   * @param requestId Network.RequestId
   *                  Identifier of the network request to get content for.
   * @param encoding  string
   *                  The encoding to use.
   *                  Allowed Values: webp, jpeg, png
   * @param quality   number
   *                  The quality of the encoding (0-1). (defaults to 1)
   * @param sizeOnly  boolean
   *                  Whether to only return the size information (defaults to false).
   * @return {
   * body: string  The encoded body as a base64 string. Omitted if sizeOnly is true. (Encoded as a base64 string when passed over JSON)
   * originalSize: integer  Size before re-encoding.
   * encodedSize: integer  Size after re-encoding.
   * }
   */
  JSONObject getEncodedResponse(String requestId, String encoding, String quality, BOMInputStream sizeOnly);

  @Event("Audits")
  public interface Events {

    /**
     * @param issue InspectorIssue
     */
    @Event("issueAdded")
    void issueAdded(InspectorIssue issue);
  }

  /**
   * Information about a cookie that is affected by an inspector issue.
   */
  @Data
  public class AffectedCookie {
    /**
     * The following three properties uniquely identify a cookie
     */
    String name;
    /**
     *
     */
    String path;
    /**
     *
     */
    String domain;
  }

  /**
   * Information about the frame affected by an inspector issue.
   */
  @Data
  public class AffectedFrame {
    /**
     * Page.FrameId
     */
    String frameId;
  }

  /**
   * Information about a request that is affected by an inspector issue.
   */
  @Data
  public class AffectedRequest {
    /**
     * Network.RequestId
     * The unique request id.
     */
    String requestId;
    /**
     *
     */
    String url;
  }

  /**
   * Details for issues around "Attribution Reporting API" usage. Explainer: https://github.com/WICG/attribution-reporting-api
   */
  @Data
  public class AttributionReportingIssueDetails {
    AttributionReportingIssueType violationType;
    AffectedRequest request;
    /**
     * DOM.BackendNodeId
     */
    String violatingNodeId;
    String invalidParameter;
  }

  /**
   * Allowed Values: PermissionPolicyDisabled, UntrustworthyReportingOrigin, InsecureContext, InvalidHeader, InvalidRegisterTriggerHeader,
   * SourceAndTriggerHeaders, SourceIgnored, TriggerIgnored, OsSourceIgnored, OsTriggerIgnored, InvalidRegisterOsSourceHeader,
   * InvalidRegisterOsTriggerHeader, WebAndOsHeaders, NoWebOrOsSupport, NavigationRegistrationWithoutTransientUserActivation
   */
  public enum AttributionReportingIssueType {
    PermissionPolicyDisabled, UntrustworthyReportingOrigin, InsecureContext, InvalidHeader, InvalidRegisterTriggerHeader,
    SourceAndTriggerHeaders, SourceIgnored, TriggerIgnored, OsSourceIgnored, OsTriggerIgnored, InvalidRegisterOsSourceHeader,
    InvalidRegisterOsTriggerHeader, WebAndOsHeaders, NoWebOrOsSupport, NavigationRegistrationWithoutTransientUserActivation
  }

  /**
   * Details for a request that has been blocked with the BLOCKED_BY_RESPONSE code. Currently only used for COEP/COOP, but may be extended to include some CSP errors in the future.
   */
  @Data
  public class BlockedByResponseIssueDetails {
    AffectedRequest request;
    AffectedFrame parentFrame;
    AffectedFrame blockedFrame;
    BlockedByResponseReason reason;
  }

  /**
   * Enum indicating the reason a response has been blocked. These reasons are refinements of the net error BLOCKED_BY_RESPONSE.
   * Allowed Values: CoepFrameResourceNeedsCoepHeader, CoopSandboxedIFrameCannotNavigateToCoopPage, CorpNotSameOrigin, CorpNotSameOriginAfterDefaultedToSameOriginByCoep, CorpNotSameSite
   */
  public enum BlockedByResponseReason {
    CoepFrameResourceNeedsCoepHeader, CoopSandboxedIFrameCannotNavigateToCoopPage, CorpNotSameOrigin, CorpNotSameOriginAfterDefaultedToSameOriginByCoep, CorpNotSameSite
  }

  /**
   * This issue warns about sites in the redirect chain of a finished navigation that may be flagged as trackers and have their state cleared
   * if they don't receive a user interaction. Note that in this context 'site' means eTLD+1. For example, if the
   * URL https://example.test:80/bounce was in the redirect chain, the site reported would be example.test.
   */
  @Data
  public class BounceTrackingIssueDetails {
    List<String> trackingSites;
  }

  /**
   * This issue tracks client hints related issues. It's used to deprecate old features, encourage the use of new ones, and provide general guidance.
   */
  @Data
  public class ClientHintIssueDetails {
    SourceCodeLocation sourceCodeLocation;
    ClientHintIssueReason clientHintIssueReason;
  }

  /**
   * Allowed Values: MetaTagAllowListInvalidOrigin, MetaTagModifiedHTML
   */
  public enum ClientHintIssueReason {
    MetaTagAllowListInvalidOrigin, MetaTagModifiedHTML
  }

  /**
   *
   */
  @Data
  public class ContentSecurityPolicyIssueDetails {
    /**
     * The url not included in allowed sources.
     */
    String blockedURL;
    /**
     * Specific directive that is violated, causing the CSP issue.
     */
    String violatedDirective;
    boolean isReportOnly;
    ContentSecurityPolicyViolationType contentSecurityPolicyViolationType;
    AffectedFrame frameAncestor;
    SourceCodeLocation sourceCodeLocation;
    /**
     * DOM.BackendNodeId
     */
    String violatingNodeId;
  }

  /**
   * Allowed Values: kInlineViolation, kEvalViolation, kURLViolation, kTrustedTypesSinkViolation, kTrustedTypesPolicyViolation, kWasmEvalViolation
   */
  public enum ContentSecurityPolicyViolationType {
    kInlineViolation, kEvalViolation, kURLViolation, kTrustedTypesSinkViolation, kTrustedTypesPolicyViolation, kWasmEvalViolation
  }

  /**
   * Allowed Values: ExcludeSameSiteUnspecifiedTreatedAsLax, ExcludeSameSiteNoneInsecure, ExcludeSameSiteLax, ExcludeSameSiteStrict, ExcludeInvalidSameParty,
   * ExcludeSamePartyCrossPartyContext, ExcludeDomainNonASCII, ExcludeThirdPartyCookieBlockedInFirstPartySet, ExcludeThirdPartyPhaseout
   */
  public enum CookieExclusionReason {
    ExcludeSameSiteUnspecifiedTreatedAsLax, ExcludeSameSiteNoneInsecure, ExcludeSameSiteLax, ExcludeSameSiteStrict, ExcludeInvalidSameParty,
    ExcludeSamePartyCrossPartyContext, ExcludeDomainNonASCII, ExcludeThirdPartyCookieBlockedInFirstPartySet, ExcludeThirdPartyPhaseout
  }

  /**
   * This information is currently necessary, as the front-end has a difficult time finding a specific cookie. With this, we can convey specific error information without the cookie.
   */
  @Data
  public class CookieIssueDetails {
    /**
     * If AffectedCookie is not set then rawCookieLine contains the raw Set-Cookie header string. This hints at a problem where the cookie line is syntactically or semantically malformed in a way that no valid cookie could be created.
     */
    AffectedCookie cookie;
    /**
     *
     */
    String rawCookieLine;
    List<CookieWarningReason> cookieWarningReasons;
    List<CookieExclusionReason> cookieExclusionReasons;
    /**
     * Optionally identifies the site-for-cookies and the cookie url, which may be used by the front-end as additional context.
     */
    CookieOperation operation;
    String siteForCookies;
    String cookieUrl;
    AffectedRequest request;
  }

  /**
   * Allowed Values: SetCookie, ReadCookie
   */
  public enum CookieOperation {
    SetCookie, ReadCookie
  }

  /**
   * Allowed Values: WarnSameSiteUnspecifiedCrossSiteContext, WarnSameSiteNoneInsecure, WarnSameSiteUnspecifiedLaxAllowUnsafe,
   * WarnSameSiteStrictLaxDowngradeStrict, WarnSameSiteStrictCrossDowngradeStrict, WarnSameSiteStrictCrossDowngradeLax,
   * WarnSameSiteLaxCrossDowngradeStrict, WarnSameSiteLaxCrossDowngradeLax, WarnAttributeValueExceedsMaxSize, WarnDomainNonASCII, WarnThirdPartyPhaseout
   */
  public enum CookieWarningReason {
    WarnSameSiteUnspecifiedCrossSiteContext, WarnSameSiteNoneInsecure, WarnSameSiteUnspecifiedLaxAllowUnsafe, WarnSameSiteStrictLaxDowngradeStrict,
    WarnSameSiteStrictCrossDowngradeStrict, WarnSameSiteStrictCrossDowngradeLax, WarnSameSiteLaxCrossDowngradeStrict,
    WarnSameSiteLaxCrossDowngradeLax, WarnAttributeValueExceedsMaxSize, WarnDomainNonASCII, WarnThirdPartyPhaseout
  }

  /**
   * Details for a CORS related issue, e.g. a warning or error related to CORS RFC1918 enforcement.
   */
  @Data
  public class CorsIssueDetails {
    Network.CorsErrorStatus corsErrorStatus;
    boolean isWarning;
    AffectedRequest request;
    SourceCodeLocation location;
    String initiatorOrigin;
    Network.IPAddressSpace resourceIPAddressSpace;
    Network.ClientSecurityState clientSecurityState;
  }

  /**
   * This issue tracks information needed to print a deprecation message.
   * https://source.chromium.org/chromium/chromium/src/+/main:third_party/blink/renderer/core/frame/third_party/blink/renderer/core/frame/deprecation/README.md
   */
  @Data
  public class DeprecationIssueDetails {
    AffectedFrame affectedFrame;
    SourceCodeLocation sourceCodeLocation;
    /**
     * One of the deprecation names from third_party/blink/renderer/core/frame/deprecation/deprecation.json5
     */
    String type;
  }

  /**
   *
   */
  @Data
  public class FailedRequestInfo {
    /**
     * The URL that failed to load.
     */
    String url;
    /**
     * The failure message for the failed request.
     */
    String failureMessage;
    /**
     * Network.RequestId
     */
    String requestId;
  }

  /**
   *
   */
  @Data
  public class FederatedAuthRequestIssueDetails {
    FederatedAuthRequestIssueReason federatedAuthRequestIssueReason;
  }

  /**
   * Represents the failure reason when a federated authentication reason fails. Should be updated alongside RequestIdTokenStatus
   * in third_party/blink/public/mojom/devtools/inspector_issue.mojom to include all cases except for success.
   * Allowed Values: ShouldEmbargo, TooManyRequests, WellKnownHttpNotFound, WellKnownNoResponse, WellKnownInvalidResponse,
   * WellKnownListEmpty, WellKnownInvalidContentType, ConfigNotInWellKnown, WellKnownTooBig, ConfigHttpNotFound, ConfigNoResponse,
   * ConfigInvalidResponse, ConfigInvalidContentType, ClientMetadataHttpNotFound, ClientMetadataNoResponse, ClientMetadataInvalidResponse,
   * ClientMetadataInvalidContentType, DisabledInSettings, ErrorFetchingSignin, InvalidSigninResponse, AccountsHttpNotFound,
   * AccountsNoResponse, AccountsInvalidResponse, AccountsListEmpty, AccountsInvalidContentType, IdTokenHttpNotFound, IdTokenNoResponse,
   * IdTokenInvalidResponse, IdTokenInvalidRequest, IdTokenInvalidContentType, ErrorIdToken, Canceled, RpPageNotVisible, SilentMediationFailure, ThirdPartyCookiesBlocked
   */
  public enum FederatedAuthRequestIssueReason {
    ShouldEmbargo, TooManyRequests, WellKnownHttpNotFound, WellKnownNoResponse, WellKnownInvalidResponse, WellKnownListEmpty,
    WellKnownInvalidContentType, ConfigNotInWellKnown, WellKnownTooBig, ConfigHttpNotFound, ConfigNoResponse, ConfigInvalidResponse,
    ConfigInvalidContentType, ClientMetadataHttpNotFound, ClientMetadataNoResponse, ClientMetadataInvalidResponse,
    ClientMetadataInvalidContentType, DisabledInSettings, ErrorFetchingSignin, InvalidSigninResponse, AccountsHttpNotFound,
    AccountsNoResponse, AccountsInvalidResponse, AccountsListEmpty, AccountsInvalidContentType, IdTokenHttpNotFound, IdTokenNoResponse,
    IdTokenInvalidResponse, IdTokenInvalidRequest, IdTokenInvalidContentType, ErrorIdToken, Canceled, RpPageNotVisible, SilentMediationFailure, ThirdPartyCookiesBlocked
  }

  @Data
  public class FederatedAuthUserInfoRequestIssueDetails {
    FederatedAuthUserInfoRequestIssueReason federatedAuthUserInfoRequestIssueReason;
  }

  /**
   * Represents the failure reason when a getUserInfo() call fails. Should be updated alongside FederatedAuthUserInfoRequestResult in third_party/blink/public/mojom/devtools/inspector_issue.mojom.
   * Allowed Values: NotSameOrigin, NotIframe, NotPotentiallyTrustworthy, NoApiPermission, NotSignedInWithIdp, NoAccountSharingPermission, InvalidConfigOrWellKnown, InvalidAccountsResponse, NoReturningUserFromFetchedAccounts
   */
  public enum FederatedAuthUserInfoRequestIssueReason {
    NotSameOrigin, NotIframe, NotPotentiallyTrustworthy, NoApiPermission, NotSignedInWithIdp, NoAccountSharingPermission,
    InvalidConfigOrWellKnown, InvalidAccountsResponse, NoReturningUserFromFetchedAccounts
  }

  /**
   * Depending on the concrete errorType, different properties are set.
   */
  @Data
  public class GenericIssueDetails {
    /**
     * Issues with the same errorType are aggregated in the frontend.
     */
    GenericIssueErrorType errorType;
    /**
     * Page.FrameId
     */
    String frameId;
    /**
     * DOM.BackendNodeId
     */
    String violatingNodeId;
    String violatingNodeAttribute;
    AffectedRequest request;
  }

  /**
   * Allowed Values: CrossOriginPortalPostMessageError, FormLabelForNameError, FormDuplicateIdForInputError, FormInputWithNoLabelError,
   * FormAutocompleteAttributeEmptyError, FormEmptyIdAndNameAttributesForInputError, FormAriaLabelledByToNonExistingId,
   * FormInputAssignedAutocompleteValueToIdOrNameAttributeError, FormLabelHasNeitherForNorNestedInput, FormLabelForMatchesNonExistingIdError,
   * FormInputHasWrongButWellIntendedAutocompleteValueError, ResponseWasBlockedByORB
   */
  public enum GenericIssueErrorType {
    CrossOriginPortalPostMessageError, FormLabelForNameError, FormDuplicateIdForInputError, FormInputWithNoLabelError,
    FormAutocompleteAttributeEmptyError, FormEmptyIdAndNameAttributesForInputError, FormAriaLabelledByToNonExistingId,
    FormInputAssignedAutocompleteValueToIdOrNameAttributeError, FormLabelHasNeitherForNorNestedInput, FormLabelForMatchesNonExistingIdError,
    FormInputHasWrongButWellIntendedAutocompleteValueError, ResponseWasBlockedByORB
  }

  /**
   *
   */
  @Data
  public class HeavyAdIssueDetails {
    /**
     * The resolution status, either blocking the content or warning.
     */
    HeavyAdResolutionStatus resolution;
    /**
     * The reason the ad was blocked, total network or cpu or peak cpu.
     */
    HeavyAdReason reason;
    /**
     * The frame that was blocked.
     */
    AffectedFrame frame;
  }

  /**
   * Allowed Values: NetworkTotalLimit, CpuTotalLimit, CpuPeakLimit
   */
  public enum HeavyAdReason {
    NetworkTotalLimit, CpuTotalLimit, CpuPeakLimit
  }

  /**
   * Allowed Values: HeavyAdBlocked, HeavyAdWarning
   */
  public enum HeavyAdResolutionStatus {
    HeavyAdBlocked, HeavyAdWarning
  }

  /**
   * An inspector issue reported from the back-end.
   */
  @Data
  public class InspectorIssue {
    InspectorIssueDetails code;
    InspectorIssueCode details;
    /**
     * A unique id for this issue. May be omitted if no other entity (e.g. exception, CDP message, etc.) is referencing this issue.
     * IssueId
     */
    String issueId;
  }

  /**
   * A unique identifier for the type of issue. Each type may use one of the optional fields in InspectorIssueDetails to convey more specific information about the kind of issue.
   * Allowed Values: CookieIssue, MixedContentIssue, BlockedByResponseIssue, HeavyAdIssue, ContentSecurityPolicyIssue, SharedArrayBufferIssue,
   * LowTextContrastIssue, CorsIssue, AttributionReportingIssue, QuirksModeIssue, NavigatorUserAgentIssue, GenericIssue, DeprecationIssue,
   * ClientHintIssue, FederatedAuthRequestIssue, BounceTrackingIssue, StylesheetLoadingIssue, FederatedAuthUserInfoRequestIssue
   */
  public enum InspectorIssueCode {
    CookieIssue, MixedContentIssue, BlockedByResponseIssue, HeavyAdIssue, ContentSecurityPolicyIssue, SharedArrayBufferIssue,
    LowTextContrastIssue, CorsIssue, AttributionReportingIssue, QuirksModeIssue, NavigatorUserAgentIssue, GenericIssue,
    DeprecationIssue, ClientHintIssue, FederatedAuthRequestIssue, BounceTrackingIssue, StylesheetLoadingIssue, FederatedAuthUserInfoRequestIssue
  }

  /**
   * This struct holds a list of optional fields with additional information specific to the kind of issue. When adding a new issue code, please also add a new optional field to this type.
   */
  @Data
  public class InspectorIssueDetails {
    CookieIssueDetails cookieIssueDetails;
    MixedContentIssueDetails mixedContentIssueDetails;
    BlockedByResponseIssueDetails blockedByResponseIssueDetails;
    HeavyAdIssueDetails heavyAdIssueDetails;
    ContentSecurityPolicyIssueDetails contentSecurityPolicyIssueDetails;
    SharedArrayBufferIssueDetails sharedArrayBufferIssueDetails;
    LowTextContrastIssueDetails lowTextContrastIssueDetails;
    CorsIssueDetails corsIssueDetails;
    AttributionReportingIssueDetails attributionReportingIssueDetails;
    QuirksModeIssueDetails quirksModeIssueDetails;
    NavigatorUserAgentIssueDetails navigatorUserAgentIssueDetails;
    GenericIssueDetails genericIssueDetails;
    DeprecationIssueDetails deprecationIssueDetails;
    ClientHintIssueDetails clientHintIssueDetails;
    FederatedAuthRequestIssueDetails federatedAuthRequestIssueDetails;
    BounceTrackingIssueDetails bounceTrackingIssueDetails;
    StylesheetLoadingIssueDetails stylesheetLoadingIssueDetails;
    FederatedAuthRequestIssueDetails federatedAuthUserInfoRequestIssueDetails;
  }

  @Data
  public class LowTextContrastIssueDetails {
    /**
     * DOM.BackendNodeId
     */
    String violatingNodeId;
    String violatingNodeSelector;
    Number contrastRatio;
    Number thresholdAA;
    Number thresholdAAA;
    String fontSize;
    String fontWeight;
  }

  /**
   *
   */
  @Data
  public class MixedContentIssueDetails {
    /**
     * The type of resource causing the mixed content issue (css, js, iframe, form,...). Marked as optional because it is mapped to from blink::mojom::RequestContextType, which will be replaced by network::mojom::RequestDestination
     */
    MixedContentResourceType resourceType;
    /**
     * The way the mixed content issue is being resolved.
     */
    MixedContentResolutionStatus resolutionStatus;
    /**
     * The unsafe http url causing the mixed content issue.
     */
    String insecureURL;
    /**
     * The url responsible for the call to an unsafe url.
     */
    String mainResourceURL;
    /**
     * The mixed content request. Does not always exist (e.g. for unsafe form submission urls).
     */
    AffectedRequest request;
    /**
     * Optional because not every mixed content issue is necessarily linked to a frame.
     */
    AffectedFrame frame;
  }

  /**
   * Allowed Values: MixedContentBlocked, MixedContentAutomaticallyUpgraded, MixedContentWarning
   */
  public enum MixedContentResolutionStatus {
    MixedContentBlocked, MixedContentAutomaticallyUpgraded, MixedContentWarning
  }

  /**
   * Allowed Values: AttributionSrc, Audio, Beacon, CSPReport, Download, EventSource, Favicon, Font, Form, Frame, Image, Import,
   * Manifest, Ping, PluginData, PluginResource, Prefetch, Resource, Script, ServiceWorker, SharedWorker, Stylesheet, Track, Video, Worker, XMLHttpRequest, XSLT
   */
  public enum MixedContentResourceType {
    AttributionSrc, Audio, Beacon, CSPReport, Download, EventSource, Favicon, Font, Form, Frame, Image, Import, Manifest, Ping, PluginData,
    PluginResource, Prefetch, Resource, Script, ServiceWorker, SharedWorker, Stylesheet, Track, Video, Worker, XMLHttpRequest, XSLT
  }

  /**
   * Details for issues about documents in Quirks Mode or Limited Quirks Mode that affects page layouting.
   */
  @Data
  public class QuirksModeIssueDetails {
    /**
     * If false, it means the document's mode is "quirks" instead of "limited-quirks".
     */
    boolean isLimitedQuirksMode;
    /**
     * DOM.BackendNodeId
     */
    String documentNodeId;
    /**
     *
     */
    String url;
    /**
     * Page.FrameId
     */
    String frameId;
    /**
     * Network.LoaderId
     */
    String loaderId;
  }

  /**
   * Details for a issue arising from an SAB being instantiated in, or transferred to a context that is not cross-origin isolated.
   */
  @Data
  public class SharedArrayBufferIssueDetails {
    SourceCodeLocation sourceCodeLocation;
    boolean isWarning;
    SharedArrayBufferIssueType type;
  }

  /**
   * Allowed Values: TransferIssue, CreationIssue
   */
  public enum SharedArrayBufferIssueType {
    TransferIssue, CreationIssue
  }

  /**
   *
   */
  @Data
  public class SourceCodeLocation {
    /**
     * Runtime.ScriptId
     */
    String scriptId;
    String url;
    Integer lineNumber;
    Integer columnNumber;
  }

  /**
   * This issue warns when a referenced stylesheet couldn't be loaded.
   */
  @Data
  public class StylesheetLoadingIssueDetails {
    /**
     * Source code position that referenced the failing stylesheet.
     */
    SourceCodeLocation sourceCodeLocation;
    /**
     * Reason why the stylesheet couldn't be loaded.
     */
    StyleSheetLoadingIssueReason styleSheetLoadingIssueReason;
    /**
     * Contains additional info when the failure was due to a request.
     */
    FailedRequestInfo failedRequestInfo;
  }

  /**
   * Allowed Values: LateImportRule, RequestFailed
   */
  public enum StyleSheetLoadingIssueReason {
    LateImportRule, RequestFailed
  }

  @Data
  public class NavigatorUserAgentIssueDetails {
    String url;
    SourceCodeLocation location;
  }

}
