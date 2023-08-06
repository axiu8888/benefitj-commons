package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Network {


  /**
   * The reason why request was blocked.
   * Allowed Values: other, csp, mixed-content, origin, inspector, subresource-filter, content-type, coep-frame-resource-needs-coep-header, coop-sandboxed-iframe-cannot-navigate-to-coop-page, corp-not-same-origin, corp-not-same-origin-after-defaulted-to-same-origin-by-coep, corp-not-same-site
   */
  public enum BlockedReason {
    other,
    csp,
    mixed_content,
    origin,
    inspector,
    subresource_filter,
    content_type,
    coep_frame_resource_needs_coep_header,
    coop_sandboxed_iframe_cannot_navigate_to_coop_page,
    corp_not_same_origin,
    corp_not_same_origin_after_defaulted_to_same_origin_by_coep,
    corp_not_same_site;
    public static List<String> values = Collections.unmodifiableList(Arrays.asList(
        ("other"),
        ("csp"),
        ("mixed-content"),
        ("origin"),
        ("inspector"),
        ("subresource-filter"),
        ("content-type"),
        ("coep-frame-resource-needs-coep-header"),
        ("coop-sandboxed-iframe-cannot-navigate-to-coop-page"),
        ("corp-not-same-origin"),
        ("corp-not-same-origin-after-defaulted-to-same-origin-by-coep"),
        ("corp-not-same-site")
    ));
  }

  /**
   * Information about the cached resource.
   */
  @Data
  public class CachedResource {
    /**
     * Resource URL. This is the url of the original network request.
     */
    String url;
    /**
     * Type of this resource.
     */
    ResourceType type;
    /**
     * Cached response data.
     */
    Response response;
    /**
     * Cached response body size.
     */
    Long bodySize;
  }

  /**
   * Whether the request complied with Certificate Transparency policy.
   * Allowed Values: unknown, not-compliant, compliant
   */
  public enum CertificateTransparencyCompliance {
    @JSONField(name = "unknown")
    @JsonProperty("unknown")
    unknown,
    @JSONField(name = "not-compliant")
    @JsonProperty("not-compliant")
    not_compliant,
    @JSONField(name = "compliant")
    @JsonProperty("compliant")
    compliant;

    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        ("unknown"),
        ("not-compliant"),
        ("compliant")
    ));
  }

  /**
   * The underlying connection technology that the browser is supposedly using.
   * Allowed Values: none, cellular2g, cellular3g, cellular4g, bluetooth, ethernet, wifi, wimax, other
   */
  public enum ConnectionType {
    none, cellular2g, cellular3g, cellular4g, bluetooth, ethernet, wifi, wimax, other
  }

  @Data
  public class Cookie {
    /**
     * Cookie name.
     */
    String name;
    /**
     * Cookie value.
     */
    String value;
    /**
     * Cookie domain.
     */
    String domain;
    /**
     * Cookie path.
     */
    String path;
    /**
     * Cookie expiration date as the number of seconds since the UNIX epoch.
     */
    Integer expires;
    /**
     * Cookie size.
     */
    Integer size;
    /**
     * True if cookie is http-only.
     */
    boolean httpOnly;
    /**
     * True if cookie is secure.
     */
    boolean secure;
    /**
     * True in case of session cookie.
     */
    boolean session;
    /**
     * Cookie SameSite type.
     */
    CookieSameSite sameSite;
    /**
     * Cookie Priority EXPERIMENTAL
     */
    CookiePriority priority;
    /**
     * True if cookie is SameParty. EXPERIMENTAL
     */
    boolean sameParty;
    /**
     * Cookie source scheme type. EXPERIMENTAL
     */
    CookieSourceScheme sourceScheme;
    /**
     * Cookie source port. Valid values are {-1, [1, 65535]}, -1 indicates an unspecified port. An unspecified port value allows protocol clients to emulate legacy cookie scope for the port. This is a temporary ability and it will be removed in the future. EXPERIMENTAL
     */
    Integer sourcePort;
    /**
     * Cookie partition key. The site of the top-level URL the browser was visiting at the start of the request to the endpoint that set the cookie. EXPERIMENTAL
     */

    String partitionKey;
    /**
     * True if cookie partition key is opaque. EXPERIMENTAL
     */
    boolean partitionKeyOpaque;
  }

  /**
   * Cookie parameter object
   */
  @Data
  public class CookieParam {
    /**
     * Cookie name.
     */
    String name;
    /**
     * Cookie value.
     */
    String value;
    /**
     * The request-URI to associate with the setting of the cookie. This value can affect the default domain, path, source port, and source scheme values of the created cookie.
     */
    String url;
    /**
     * Cookie domain.
     */
    String domain;
    /**
     * Cookie path.
     */
    String path;
    /**
     * True if cookie is secure.
     */
    boolean secure;
    /**
     * True if cookie is http-only.
     */
    boolean httpOnly;
    /**
     * Cookie SameSite type.
     */
    CookieSameSite sameSite;
    /**
     * Cookie expiration date, session cookie if not set
     */
    Long expires;
    /**
     * Cookie Priority. EXPERIMENTAL
     */
    CookiePriority priority;
    /**
     * True if cookie is SameParty. EXPERIMENTAL
     */
    boolean sameParty;
    /**
     * Cookie source scheme type. EXPERIMENTAL
     */
    CookieSourceScheme sourceScheme;
    /**
     * Cookie source port. Valid values are {-1, [1, 65535]}, -1 indicates an unspecified port. An unspecified port value allows protocol clients to emulate legacy cookie scope for the port. This is a temporary ability and it will be removed in the future. EXPERIMENTAL
     */
    Integer sourcePort;
    /**
     * Cookie partition key. The site of the top-level URL the browser was visiting at the start of the request to the endpoint that set the cookie. If not set, the cookie will be set as not partitioned. EXPERIMENTAL
     */
    String partitionKey;
  }

  /**
   * Represents the cookie's 'SameSite' status: https://tools.ietf.org/html/draft-west-first-party-cookies
   * Allowed Values: Strict, Lax, None
   */
  public enum CookieSameSite {
    Strict, Lax, None
  }

  /**
   * The reason why request was blocked.
   * Allowed Values: DisallowedByMode, InvalidResponse, WildcardOriginNotAllowed, MissingAllowOriginHeader, MultipleAllowOriginValues, InvalidAllowOriginValue,
   * AllowOriginMismatch, InvalidAllowCredentials, CorsDisabledScheme, PreflightInvalidStatus, PreflightDisallowedRedirect, PreflightWildcardOriginNotAllowed,
   * PreflightMissingAllowOriginHeader, PreflightMultipleAllowOriginValues, PreflightInvalidAllowOriginValue, PreflightAllowOriginMismatch, PreflightInvalidAllowCredentials,
   * PreflightMissingAllowExternal, PreflightInvalidAllowExternal, PreflightMissingAllowPrivateNetwork, PreflightInvalidAllowPrivateNetwork, InvalidAllowMethodsPreflightResponse,
   * InvalidAllowHeadersPreflightResponse, MethodDisallowedByPreflightResponse, HeaderDisallowedByPreflightResponse, RedirectContainsCredentials, InsecurePrivateNetwork,
   * InvalidPrivateNetworkAccess, UnexpectedPrivateNetworkAccess, NoCorsRedirectModeNotFollow, PreflightMissingPrivateNetworkAccessId, PreflightMissingPrivateNetworkAccessName,
   * PrivateNetworkAccessPermissionUnavailable, PrivateNetworkAccessPermissionDenied
   */
  public enum CorsError {
    DisallowedByMode,
    InvalidResponse,
    WildcardOriginNotAllowed,
    MissingAllowOriginHeader,
    MultipleAllowOriginValues,
    InvalidAllowOriginValue,
    AllowOriginMismatch,
    InvalidAllowCredentials,
    CorsDisabledScheme,
    PreflightInvalidStatus,
    PreflightDisallowedRedirect,
    PreflightWildcardOriginNotAllowed,
    PreflightMissingAllowOriginHeader,
    PreflightMultipleAllowOriginValues,
    PreflightInvalidAllowOriginValue,
    PreflightAllowOriginMismatch,
    PreflightInvalidAllowCredentials,
    PreflightMissingAllowExternal,
    PreflightInvalidAllowExternal,
    PreflightMissingAllowPrivateNetwork,
    PreflightInvalidAllowPrivateNetwork,
    InvalidAllowMethodsPreflightResponse,
    InvalidAllowHeadersPreflightResponse,
    MethodDisallowedByPreflightResponse,
    HeaderDisallowedByPreflightResponse,
    RedirectContainsCredentials,
    InsecurePrivateNetwork,
    InvalidPrivateNetworkAccess,
    UnexpectedPrivateNetworkAccess,
    NoCorsRedirectModeNotFollow,
    PreflightMissingPrivateNetworkAccessId,
    PreflightMissingPrivateNetworkAccessName,
    PrivateNetworkAccessPermissionUnavailable,
    PrivateNetworkAccessPermissionDenied
  }

  @Data
  public class CorsErrorStatus {
    CorsError corsError;
    String failedParameter;
  }

  /**
   * Network level fetch failure reason.
   * Allowed Values: Failed, Aborted, TimedOut, AccessDenied, ConnectionClosed, ConnectionReset, ConnectionRefused, ConnectionAborted, ConnectionFailed,
   * NameNotResolved, InternetDisconnected, AddressUnreachable, BlockedByClient, BlockedByResponse
   */
  public enum ErrorReason {
    Failed,
    Aborted,
    TimedOut,
    AccessDenied,
    ConnectionClosed,
    ConnectionReset,
    ConnectionRefused,
    ConnectionAborted,
    ConnectionFailed,
    NameNotResolved,
    InternetDisconnected,
    AddressUnreachable,
    BlockedByClient,
    BlockedByResponse
  }

  /**
   * Request / response headers as keys / values of JSON object
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class Headers extends JSONObject {
  }

  /**
   * Information about the request initiator.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class Initiator extends JSONObject {
    /**
     * TType of this initiator.
     * Allowed Values: parser, script, preload, SignedExchange, preflight, other
     */
    String type;
    /**
     * Initiator JavaScript stack trace, set for Script only.
     */
    Runtime.StackTrace stack;
    /**
     * Initiator URL, set for Parser type or for Script type (when script is importing module) or for SignedExchange type.
     */
    String url;
    /**
     * Initiator line number, set for Parser type or for Script type (when script is importing module) (0-based).
     */
    Integer lineNumber;
    /**
     * Initiator column number, set for Parser type or for Script type (when script is importing module) (0-based).
     */
    Integer columnNumber;
    /**
     * Set if another request triggered this request (e.g. preflight).
     */
    String requestId;
  }

  /**
   * Post data entry for HTTP request
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class PostDataEntry extends JSONObject {
    String bytes;
  }

  /**
   * HTTP request data.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class Request extends JSONObject {
    /**
     * Request URL (without fragment).
     */
    String url;
    /**
     * Fragment of the requested URL starting with hash, if present.
     */
    String urlFragment;
    /**
     * HTTP request method.
     */
    String method;
    /**
     * HTTP request headers.
     */
    Headers headers;
    /**
     * HTTP POST request data.
     */
    String postData;
    /**
     * True when the request has POST data. Note that postData might still be omitted when this flag is true when the data is too long.
     */
    boolean hasPostData;
    /**
     * Request body elements. This will be converted from base64 to binary EXPERIMENTAL
     */
    List<PostDataEntry> postDataEntries;
    /**
     * The mixed content type of the request.
     */
    Security.MixedContentType mixedContentType;
    /**
     * Priority of the resource request at the time request is sent.
     */
    ResourcePriority initialPriority;
    /**
     * The referrer policy of the request, as defined in https://www.w3.org/TR/referrer-policy/
     * Allowed Values: unsafe-url, no-referrer-when-downgrade, no-referrer, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin
     */
    String referrerPolicy;
    /**
     * Whether is loaded via link preload.
     */
    boolean isLinkPreload;
    /**
     * Set for requests when the TrustToken API is used. Contains the parameters passed by the developer (e.g. via "fetch") as understood by the backend. EXPERIMENTAL
     */
    TrustTokenParams trustTokenParams;
    /**
     * True if this resource request is considered to be the 'same site' as the request correspondinfg to the main frame. EXPERIMENTAL
     */
    boolean isSameSite;
  }


  /**
   * HTTP response data.
   */
  @Data
  public class Response {
    /**
     * Response URL. This URL can be different from CachedResource.url in case of redirect.
     */
    String url;
    /**
     * HTTP response status code.
     */
    Integer status;
    /**
     * HTTP response status text.
     */
    String statusText;
    /**
     * HTTP response headers.
     */
    Headers headers;
    /**
     * HTTP response headers text. This has been replaced by the headers in Network.responseReceivedExtraInfo. DEPRECATED
     */
    String headersText;
    /**
     * Resource mimeType as determined by the browser.
     */
    String mimeType;
    /**
     * Refined HTTP request headers that were actually transmitted over the network.
     */
    Headers requestHeaders;
    /**
     * HTTP request headers text. This has been replaced by the headers in Network.requestWillBeSentExtraInfo. DEPRECATED
     */
    String requestHeadersText;
    /**
     * Specifies whether physical connection was actually reused for this request.
     */
    boolean connectionReused;
    /**
     * Physical connection id that was actually used for this request.
     */
    Integer connectionId;
    /**
     * Remote IP address.
     */
    String remoteIPAddress;
    /**
     * Remote port.
     */
    Integer remotePort;
    /**
     * Specifies that the request was served from the disk cache.
     */
    boolean fromDiskCache;
    /**
     * Specifies that the request was served from the ServiceWorker.
     */
    boolean fromServiceWorker;
    /**
     * Specifies that the request was served from the prefetch cache.
     */
    boolean fromPrefetchCache;
    /**
     * Total number of bytes received for this request so far.
     */
    Long encodedDataLength;
    /**
     * Timing information for the given request.
     */
    ResourceTiming timing;
    /**
     * Response source of response from ServiceWorker.
     */
    ServiceWorkerResponseSource serviceWorkerResponseSource;
    /**
     * The time at which the returned response was generated.
     */
    Long responseTime;
    /**
     * Cache Storage Cache Name.
     */
    String cacheStorageCacheName;
    /**
     * Protocol used to fetch this request.
     */
    String protocol;
    /**
     * The reason why Chrome uses a specific transport protocol for HTTP semantics. EXPERIMENTAL
     */
    AlternateProtocolUsage alternateProtocolUsage;
    /**
     * Security state of the request resource.
     */
    Security.SecurityState securityState;
    /**
     * Security details for the request.
     */
    SecurityDetails securityDetails;
  }

  /**
   * Loading priority of a resource request.
   * Allowed Values: VeryLow, Low, Medium, High, VeryHigh
   */
  public enum ResourcePriority {
    VeryLow, Low, Medium, High, VeryHigh
  }

  /**
   * Resource type as it was perceived by the rendering engine.
   * Allowed Values: Document, Stylesheet, Image, Media, Font, Script, TextTrack, XHR, Fetch, Prefetch, EventSource, WebSocket, Manifest, SignedExchange, Ping, CSPViolationReport, Preflight, Other
   */
  public enum ResourceType {
    Document, Stylesheet, Image, Media, Font, Script, TextTrack, XHR, Fetch, Prefetch, EventSource, WebSocket, Manifest, SignedExchange, Ping, CSPViolationReport, Preflight, Other
  }

  /**
   * Timing information for the request.
   */
  @Data
  public class ResourceTiming {
    /**
     * Timing's requestTime is a baseline in seconds, while the other numbers are ticks in milliseconds relatively to this requestTime.
     */
    Long requestTime;
    /**
     * Started resolving proxy.
     */
    Number proxyStart;
    /**
     * Finished resolving proxy.
     */
    Number proxyEnd;
    /**
     * Started DNS address resolve.
     */
    Number dnsStart;
    /**
     * Finished DNS address resolve.
     */
    Number dnsEnd;
    /**
     * Started connecting to the remote host.
     */
    Number connectStart;
    /**
     * Connected to the remote host.
     */
    Number connectEnd;
    /**
     * Started SSL handshake.
     */
    Number sslStart;
    /**
     * Finished SSL handshake.
     */
    Number sslEnd;
    /**
     * Started running ServiceWorker. EXPERIMENTAL
     */
    Number workerStart;
    /**
     * Finished Starting ServiceWorker. EXPERIMENTAL
     */
    Number workerReady;
    /**
     * Started fetch event. EXPERIMENTAL
     */
    Number workerFetchStart;
    /**
     * Settled fetch event respondWith promise. EXPERIMENTAL
     */
    Number workerRespondWithSettled;
    /**
     * Started sending request.
     */
    Number sendStart;
    /**
     * Finished sending request.
     */
    Number sendEnd;
    /**
     * Time the server started pushing request. EXPERIMENTAL
     */
    Number pushStart;
    /**
     * Time the server finished pushing request. EXPERIMENTAL
     */
    Number pushEnd;
    /**
     * Started receiving response headers. EXPERIMENTAL
     */
    Number receiveHeadersStart;
    /**
     * Finished receiving response headers.
     */
    Number receiveHeadersEnd;
  }

  /**
   * Source of serviceworker response.
   * Allowed Values: cache-storage, http-cache, fallback-code, network
   */
  public enum ServiceWorkerResponseSource {
    cache_storage, http_cache, fallback_code, network;

    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        ("cache-storage"),
        ("http-cache"),
        ("fallback-code"),
        ("network")
    ));
  }

  /**
   * Security details about a request.
   */
  @Data
  public class SecurityDetails {
    /**
     * Protocol name (e.g. "TLS 1.2" or "QUIC").
     */
    String protocol;

    /**
     * Key Exchange used by the connection, or the empty string if not applicable.
     */
    String keyExchange;

    /**
     * (EC)DH group used by the connection, if applicable.
     */
    String keyExchangeGroup;

    /**
     * Cipher name.
     */
    String cipher;

    /**
     * TLS MAC. Note that AEAD ciphers do not have separate MACs.
     */
    String mac;
    /**
     * Certificate ID value.
     */
    Security.CertificateId certificateId;
    /**
     * Certificate subject name.
     */
    String subjectName;
    /**
     * Subject Alternative Name (SAN) DNS names and IP addresses.
     */
    List<String> sanList;
    /**
     * Name of the issuing CA.
     */
    String issuer;
    /**
     * Certificate valid from date.
     */
    Long validFrom;
    /**
     * Certificate valid to (expiration) date
     */
    Long validTo;
    /**
     * List of signed certificate timestamps (SCTs).
     */
    List<SignedCertificateTimestamp> signedCertificateTimestampList;
    /**
     * Whether the request complied with Certificate Transparency policy
     */
    CertificateTransparencyCompliance certificateTransparencyCompliance;
    /**
     * The signature algorithm used by the server in the TLS server signature, represented as a TLS SignatureScheme code point. Omitted if not applicable or not known.
     */
    Integer serverSignatureAlgorithm;
    /**
     * Whether the connection used Encrypted ClientHello
     */
    boolean encryptedClientHello;
  }

  /**
   * Details of a signed certificate timestamp (SCT).
   */
  @Data
  public class SignedCertificateTimestamp {
    /**
     * Validation status.
     */
    String status;
    /**
     * Origin.
     */
    String origin;
    /**
     * Log name / description.
     */
    String logDescription;
    /**
     * Log ID.
     */
    String logId;
    /**
     * Issuance date. Unlike TimeSinceEpoch, this contains the number of milliseconds since January 1, 1970, UTC, not the number of seconds.
     */
    Long timestamp;
    /**
     * Hash algorithm.
     */
    String hashAlgorithm;
    /**
     * Signature algorithm.
     */
    String signatureAlgorithm;
    /**
     * Signature data.
     */
    String signatureData;
  }

  /**
   * WebSocket message data. This represents an entire WebSocket message, not just a fragmented frame as the name suggests.
   */
  @Data
  public class WebSocketFrame {
    /**
     * WebSocket message opcode.
     */
    Integer opcode;
    /**
     * WebSocket message mask.
     */
    boolean mask;
    /**
     * WebSocket message payload data. If the opcode is 1, this is a text message and payloadData is a UTF-8 string. If the opcode isn't 1, then payloadData is a base64 encoded string representing binary data.
     */
    String payloadData;
  }

  /**
   * WebSocket request data.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class WebSocketRequest extends JSONObject {
    /**
     * HTTP request headers.
     */
    Headers headers;
  }

  /**
   * WWebSocket response data.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class WebSocketResponse extends JSONObject {
    /**
     * HTTP response status code.
     */
    Integer status;
    /**
     * HTTP response status text.
     */
    String statusText;
    /**
     * HTTP response headers.
     */
    Headers headers;
    /**
     * HTTP response headers text.
     */
    String headersText;
    /**
     * HTTP request headers.
     */
    Headers requestHeaders;
    /**
     * HTTP request headers text.
     */
    String requestHeadersText;
  }

  /**
   * The reason why Chrome uses a specific transport protocol for HTTP semantics.
   * Allowed Values: alternativeJobWonWithoutRace, alternativeJobWonRace, mainJobWonRace, mappingMissing, broken, dnsAlpnH3JobWonWithoutRace, dnsAlpnH3JobWonRace, unspecifiedReason
   */
  public enum AlternateProtocolUsage {
    alternativeJobWonWithoutRace, alternativeJobWonRace, mainJobWonRace, mappingMissing, broken, dnsAlpnH3JobWonWithoutRace, dnsAlpnH3JobWonRace, unspecifiedReason
  }

  /**
   * Authorization challenge for HTTP status code 401 or 407.
   */
  @Data
  public class AuthChallenge {
    /**
     * Source of the authentication challenge.
     * Allowed Values: Server, Proxy
     */
    String source;
    /**
     * Origin of the challenger.
     */
    String origin;
    /**
     * The authentication scheme used, such as basic or digest
     */
    String scheme;
    /**
     * The realm of the challenge. May be empty.
     */
    String realm;
  }

  /**
   * Response to an AuthChallenge.
   */
  @Data
  public class AuthChallengeResponse {
    /**
     * The decision on what to do in response to the authorization challenge. Default means deferring to the default behavior of the net stack, which will likely either the Cancel authentication or display a popup dialog box.
     * Allowed Values: Default, CancelAuth, ProvideCredentials
     */
    String response;
    /**
     * The username to provide, possibly empty. Should only be set if response is ProvideCredentials.
     */
    String username;
    /**
     * The password to provide, possibly empty. Should only be set if response is ProvideCredentials.
     */
    String password;
  }

  /**
   * A cookie with was not sent with a request with the corresponding reason.
   */
  @Data
  public class BlockedCookieWithReason {
    /**
     * The reason(s) the cookie was blocked.
     */
    List<CookieBlockedReason> blockedReasons;
    /**
     * The cookie object representing the cookie which was not sent.
     */
    Cookie cookie;
  }

  /**
   * A cookie which was not stored from a response with the corresponding reason.
   */
  @Data
  public class BlockedSetCookieWithReason {
    /**
     * The reason(s) this cookie was blocked.
     */
    List<SetCookieBlockedReason> blockedReasons;
    /**
     * The string representing this individual cookie as it would appear in the header. This is not the entire "cookie" or "set-cookie" header which could have multiple cookies.
     */
    String cookieLine;
    /**
     * The cookie object which represents the cookie which was not stored. It is optional because sometimes complete cookie information is not available, such as in the case of parsing errors.
     */
    Cookie cookie;
  }

  @Data
  public class ClientSecurityState {
    boolean initiatorIsSecureContext;
    IPAddressSpace initiatorIPAddressSpace;
    PrivateNetworkRequestPolicy privateNetworkRequestPolicy;
  }


  @Data
  public class ContentEncoding {
    /**
     * Timing's requestTime is a baseline in seconds, while the other numbers are ticks in milliseconds relatively to this requestTime.
     * Matches ResourceTiming's requestTime for the same request (but not for redirected requests).
     */
    Long requestTime;
  }

  /**
   * List of content encodings supported by the backend.
   * Allowed Values: deflate, gzip, br, zstd
   */
  public enum ConnectTiming {
    deflate, gzip, br, zstd
  }

  /**
   * Allowed Values: HTTP, Meta
   */
  public enum ContentSecurityPolicySource {
    HTTP, Meta
  }

  @Data
  public class ContentSecurityPolicyStatus {
    String effectiveDirectives;
    boolean isEnforced;
    ContentSecurityPolicySource source;
  }

  /**
   * Types of reasons why a cookie may not be sent with a request.
   * Allowed Values: SecureOnly, NotOnPath, DomainMismatch, SameSiteStrict, SameSiteLax, SameSiteUnspecifiedTreatedAsLax, SameSiteNoneInsecure, UserPreferences,
   * ThirdPartyBlockedInFirstPartySet, UnknownError, SchemefulSameSiteStrict, SchemefulSameSiteLax, SchemefulSameSiteUnspecifiedTreatedAsLax,
   * SamePartyFromCrossPartyContext, NameValuePairExceedsMaxSize
   */
  public enum CookieBlockedReason {
    SecureOnly,
    NotOnPath,
    DomainMismatch,
    SameSiteStrict,
    SameSiteLax,
    SameSiteUnspecifiedTreatedAsLax,
    SameSiteNoneInsecure,
    UserPreferences,
    ThirdPartyBlockedInFirstPartySet,
    UnknownError,
    SchemefulSameSiteStrict,
    SchemefulSameSiteLax,
    SchemefulSameSiteUnspecifiedTreatedAsLax,
    SamePartyFromCrossPartyContext,
    NameValuePairExceedsMaxSize
  }

  /**
   * Represents the cookie's 'Priority' status: https://tools.ietf.org/html/draft-west-cookie-priority-00
   * Allowed Values: Low, Medium, High
   */
  public enum CookiePriority {
    Low, Medium, High
  }

  /**
   * Represents the source scheme of the origin that originally set the cookie. A value of "Unset" allows protocol clients to emulate legacy cookie scope for the scheme. This is a temporary ability and it will be removed in the future.
   * Allowed Values: Unset, NonSecure, Secure
   */
  public enum CookieSourceScheme {
    Unset, NonSecure, Secure
  }

  @Data
  public class CrossOriginEmbedderPolicyStatus {
    CrossOriginEmbedderPolicyValue value;
    CrossOriginEmbedderPolicyValue reportOnlyValue;
    String reportingEndpoint;
    String reportOnlyReportingEndpoint;
  }

  /**
   * Allowed Values: None, Credentialless, RequireCorp
   */
  public enum CrossOriginEmbedderPolicyValue {
    None, Credentialless, RequireCorp
  }

  @Data
  public class CrossOriginOpenerPolicyStatus {
    CrossOriginOpenerPolicyValue value;
    CrossOriginOpenerPolicyValue reportOnlyValue;
    String reportingEndpoint;
    String reportOnlyReportingEndpoint;
  }

  /**
   * Allowed Values: SameOrigin, SameOriginAllowPopups, RestrictProperties, UnsafeNone, SameOriginPlusCoep, RestrictPropertiesPlusCoep
   */
  public enum CrossOriginOpenerPolicyValue {
    SameOrigin, SameOriginAllowPopups, RestrictProperties, UnsafeNone, SameOriginPlusCoep, RestrictPropertiesPlusCoep
  }

  /**
   * Stages of the interception to begin intercepting. Request will intercept before the request is sent. Response will intercept after the response is received.
   * Allowed Values: Request, HeadersReceived
   */
  public enum InterceptionStage {
    Request, HeadersReceived
  }

  /**
   * Allowed Values: Local, Private, Public, Unknown
   */
  public enum IPAddressSpace {
    Local, Private, Public, Unknown
  }

  /**
   * An options object that may be extended later to better support CORS, CORB and streaming.
   */
  @Data
  public class LoadNetworkResourceOptions {
    boolean disableCache;
    boolean includeCredentials;
  }

  /**
   * An object providing the result of a network resource load.
   */
  @Data
  public class LoadNetworkResourcePageResult {
    boolean success;
    /**
     * Optional values used for error reporting.
     */
    Integer netError;
    String netErrorName;
    Integer httpStatusCode;
    /**
     * If successful, one of the following two fields holds the result.
     */
    IO.StreamHandle stream;
    /**
     * Response headers.
     */
    Network.Headers headers;
  }

  /**
   * Allowed Values: Allow, BlockFromInsecureToMorePrivate, WarnFromInsecureToMorePrivate, PreflightBlock, PreflightWarn
   */
  public enum PrivateNetworkRequestPolicy {
    Allow, BlockFromInsecureToMorePrivate, WarnFromInsecureToMorePrivate, PreflightBlock, PreflightWarn
  }

  @Data
  public class ReportingApiEndpoint {
    /**
     * The URL of the endpoint to which reports may be delivered.
     */
    String url;
    /**
     * Name of the endpoint group.
     */
    String groupName;
  }

  /**
   * An object representing a report generated by the Reporting API.
   */
  @Data
  public class ReportingApiReport {
    /**
     * The URL of the endpoint to which reports may be delivered.
     */
    String id;
    /**
     * The URL of the document that triggered the report.
     */
    String initiatorUrl;
    /**
     * The name of the endpoint group that should be used to deliver the report.
     */
    String destination;
    /**
     * The type of the report (specifies the set of data that is contained in the report body).
     */
    String type;
    /**
     * Network.TimeSinceEpoch
     * When the report was generated.
     */
    Long timestamp;
    /**
     * How many uploads deep the related request was.
     */
    Integer depth;
    /**
     * The number of delivery attempts made so far, not including an active attempt.
     */
    Integer completedAttempts;
    JSONObject body;
    ReportStatus status;
  }

  /**
   * The status of a Reporting API report.
   * Allowed Values: Queued, Pending, MarkedForRemoval, Success
   */
  public enum ReportStatus {
    Queued, Pending, MarkedForRemoval, Success
  }

  /**
   * Request pattern for interception.
   */
  @Data
  public class RequestPattern {
    /**
     * Wildcards ('*' -> zero or more, '?' -> exactly one) are allowed. Escape character is backslash. Omitting is equivalent to "*".
     */
    String urlPattern;
    /**
     * If set, only requests for matching resource types will be intercepted.
     */
    ResourceType resourceType;
    /**
     * Stage at which to begin intercepting requests. Default is Request.
     */
    InterceptionStage interceptionStage;
  }

  @Data
  public class SecurityIsolationStatus {
    CrossOriginOpenerPolicyStatus coop;
    CrossOriginEmbedderPolicyStatus coep;
    List<ContentSecurityPolicyStatus> csp;
  }

  /**
   * Types of reasons why a cookie may not be stored from a response.
   * Allowed Values: SecureOnly, SameSiteStrict, SameSiteLax, SameSiteUnspecifiedTreatedAsLax, SameSiteNoneInsecure, UserPreferences, ThirdPartyBlockedInFirstPartySet, SyntaxError, SchemeNotSupported, OverwriteSecure, InvalidDomain, InvalidPrefix, UnknownError, SchemefulSameSiteStrict, SchemefulSameSiteLax, SchemefulSameSiteUnspecifiedTreatedAsLax, SamePartyFromCrossPartyContext, SamePartyConflictsWithOtherAttributes, NameValuePairExceedsMaxSize, DisallowedCharacter
   */
  public enum SetCookieBlockedReason {
    SecureOnly,
    SameSiteStrict,
    SameSiteLax,
    SameSiteUnspecifiedTreatedAsLax,
    SameSiteNoneInsecure,
    UserPreferences,
    ThirdPartyBlockedInFirstPartySet,
    SyntaxError,
    SchemeNotSupported,
    OverwriteSecure,
    InvalidDomain,
    InvalidPrefix,
    UnknownError,
    SchemefulSameSiteStrict,
    SchemefulSameSiteLax,
    SchemefulSameSiteUnspecifiedTreatedAsLax,
    SamePartyFromCrossPartyContext,
    SamePartyConflictsWithOtherAttributes,
    NameValuePairExceedsMaxSize,
    DisallowedCharacter
  }

  /**
   * Information about a signed exchange response.
   */
  @Data
  public class SignedExchangeError {
    /**
     * Error message.
     */
    String message;
    /**
     * The index of the signature which caused the error.
     */
    Integer signatureIndex;
    /**
     * The field which caused the error.
     */
    SignedExchangeErrorField errorField;
  }

  /**
   * Field type for a signed exchange related error.
   * Allowed Values: signatureSig, signatureIntegrity, signatureCertUrl, signatureCertSha256, signatureValidityUrl, signatureTimestamps
   */
  public enum SignedExchangeErrorField {
    signatureSig, signatureIntegrity, signatureCertUrl, signatureCertSha256, signatureValidityUrl, signatureTimestamps
  }

  /**
   * Information about a signed exchange header. https://wicg.github.io/webpackage/draft-yasskin-httpbis-origin-signed-exchanges-impl.html#cbor-representation
   */
  public class SignedExchangeHeader {
    /**
     * Signed exchange request URL.
     */
    String requestUrl;
    /**
     * Signed exchange response code.
     */
    Integer responseCode;
    /**
     * Signed exchange response headers.
     */
    Headers responseHeaders;
    /**
     * Signed exchange response signature.
     */
    List<SignedExchangeSignature> signatures;
    /**
     * Signed exchange header integrity hash in the form of sha256-<base64-hash-value>.
     */
    String headerIntegrity;
  }

  /**
   * Information about a signed exchange response.
   */
  @Data
  public class SignedExchangeInfo {
    /**
     * The outer response of signed HTTP exchange which was received from network.
     */
    Response outerResponse;
    /**
     * Information about the signed exchange header.
     */
    SignedExchangeHeader header;
    /**
     * Security details for the signed exchange header.
     */
    SecurityDetails securityDetails;
    /**
     * Errors occurred while handling the signed exchagne.
     */
    List<SignedExchangeError> errors;
  }

  /**
   * Information about a signed exchange signature. https://wicg.github.io/webpackage/draft-yasskin-httpbis-origin-signed-exchanges-impl.html#rfc.section.3.1
   */
  @Data
  public class SignedExchangeSignature {
    /**
     * Signed exchange signature label.
     */
    String label;
    /**
     * The hex string of signed exchange signature.
     */
    String signature;
    /**
     * Signed exchange signature integrity.
     */
    String integrity;
    /**
     * Signed exchange signature cert Url.
     */
    String certUrl;
    /**
     * The hex string of signed exchange signature cert sha256.
     */
    String certSha256;
    /**
     * Signed exchange signature validity Url.
     */
    String validityUrl;
    /**
     * Signed exchange signature date.
     */
    Long date;
    /**
     * Signed exchange signature expires.
     */
    Long expires;
    /**
     * The encoded certificates.
     */
    List<String> certificates;
  }

  /**
   * Allowed Values: Issuance, Redemption, Signing
   */
  public enum TrustTokenOperationType {
    Issuance, Redemption, Signing
  }

  /**
   * Determines what type of Trust Token operation is executed and depending on the type, some additional parameters. The values are specified in third_party/blink/renderer/core/fetch/trust_token.idl.
   */
  @Data
  public class TrustTokenParams {

    TrustTokenOperationType operation;
    /**
     * Only set for "token-redemption" operation and determine whether to request a fresh SRR or use a still valid cached SRR.
     * Allowed Values: UseCached, Refresh
     */
    String refreshPolicy;
    /**
     * Origins of issuers from whom to request tokens or redemption records.
     */
    List<String> issuers;
  }

}
