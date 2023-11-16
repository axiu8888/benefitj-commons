package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.jpuppeteer.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Network domain allows tracking network activities of the page. It exposes information about http, file, data and other requests and responses, their headers, bodies, timing, etc.
 */
@ChromiumApi("Network")
public interface Network {

  /**
   * Clears browser cache.
   */
  void clearBrowserCache();

  /**
   * Clears browser cookies.
   */
  void clearBrowserCookies();

  /**
   * Deletes browser cookies with matching name and url or domain/path pair.
   *
   * @param name   string  Name of the cookies to remove.
   * @param url    string  If specified, deletes all the cookies with the given name where domain and path match provided URL.
   * @param domain string  If specified, deletes only cookies with the exact domain.
   * @param path   string  If specified, deletes only cookies with the exact path.
   */
  void deleteCookies(String name, String url, String domain, String path);

  /**
   * Disables network tracking, prevents network events from being sent to the client.
   */
  void disable();

  /**
   * Activates emulation of network conditions.
   *
   * @param offline            boolean
   *                           True to emulate internet disconnection.
   * @param latency            number
   *                           Minimum latency from request sent to response headers received (ms).
   * @param downloadThroughput number
   *                           Maximal aggregated download throughput (bytes/sec). -1 disables download throttling.
   * @param uploadThroughput   number
   *                           Maximal aggregated upload throughput (bytes/sec). -1 disables upload throttling.
   * @param connectionType     ConnectionType
   *                           Connection type if known.
   */
  void emulateNetworkConditions(Boolean offline, Long latency, Long downloadThroughput, Long uploadThroughput, ConnectionType connectionType);

  /**
   * Enables network tracking, network events will now be delivered to the client.
   *
   * @param maxTotalBufferSize    integer
   *                              Buffer size in bytes to use when preserving network payloads (XHRs, etc). EXPERIMENTAL
   * @param maxResourceBufferSize integer
   *                              Per-resource buffer size in bytes to use when preserving network payloads (XHRs, etc). EXPERIMENTAL
   * @param maxPostDataSize       integer
   *                              Longest post body size (in bytes) that would be included in requestWillBeSent notification
   */
  void enable(Long maxTotalBufferSize, Long maxResourceBufferSize, Long maxPostDataSize);

  /**
   * Returns all browser cookies for the current URL. Depending on the backend support, will return detailed cookie information in the cookies field.
   *
   * @param urls array[ string ]
   *             The list of URLs for which applicable cookies will be fetched. If not specified, it's assumed to be set to the list containing the URLs of the page and all of its subframes.
   * @return {
   * cookies: array[ Cookie ] Array of cookie objects.
   * }
   */
  JSONObject getCookies(List<String> urls);

  /**
   * Returns post data sent with the request. Returns an error when no data was sent with the request.
   *
   * @param requestId RequestId
   *                  Identifier of the network request to get content for.
   * @return {
   * postData: string  Request body string, omitting files from multipart requests
   * }
   */
  JSONObject getRequestPostData(String requestId);

  /**
   * Returns content served for the given request.
   *
   * @param requestId RequestId
   *                  Identifier of the network request to get content for.
   * @return {
   * body: string  Response body.
   * base64Encoded: boolean  True, if content was sent as base64.
   * }
   */
  JSONObject getResponseBody(String requestId);

  /**
   * Toggles ignoring cache for each request. If true, cache will not be used.
   *
   * @param name         string
   *                     Cookie name.
   * @param value        string
   *                     Cookie value.
   * @param url          string
   *                     The request-URI to associate with the setting of the cookie. This value can affect the default domain, path, source port, and source scheme values of the created cookie.
   * @param domain       string
   *                     Cookie domain.
   * @param path         string
   *                     Cookie path.
   * @param secure       boolean
   *                     True if cookie is secure.
   * @param httpOnly     boolean
   *                     True if cookie is http-only.
   * @param sameSite     CookieSameSite
   *                     Cookie SameSite type.
   * @param expires      TimeSinceEpoch
   *                     Cookie expiration date, session cookie if not set
   * @param priority     CookiePriority
   *                     Cookie Priority type. EXPERIMENTAL
   * @param sameParty    boolean
   *                     True if cookie is SameParty. EXPERIMENTAL
   * @param sourceScheme CookieSourceScheme
   *                     Cookie source scheme type. EXPERIMENTAL
   * @param sourcePort   integer
   *                     Cookie source port. Valid values are {-1, [1, 65535]}, -1 indicates an unspecified port. An unspecified port value allows protocol clients to emulate legacy cookie scope for the port. This is a temporary ability and it will be removed in the future. EXPERIMENTAL
   * @param partitionKey string
   *                     Cookie partition key. The site of the top-level URL the browser was visiting at the start of the request to the endpoint that set the cookie. If not set, the cookie will be set as not partitioned. EXPERIMENTAL
   * @return {
   * success: boolean  Always set to true. If an error occurs, the response indicates protocol error.
   * }
   */
  JSONObject setCacheDisabled(String name, String value, String url, String domain, String path, Boolean secure, Boolean httpOnly,
                              CookieSameSite sameSite, Long expires, CookiePriority priority, Boolean sameParty, CookieSourceScheme sourceScheme, Integer sourcePort, String partitionKey);

  /**
   * Sets given cookies.
   *
   * @param cookies array[ CookieParam ]
   *                Cookies to be set.
   */
  void setCookies(List<CookieParam> cookies);

  /**
   * Specifies whether to always send extra HTTP headers with the requests from this page.
   *
   * @param headers Headers
   *                Map with extra HTTP headers.
   */
  void setExtraHTTPHeaders(Headers headers);

  /**
   * Allows overriding user agent with the given string.
   *
   * @param userAgent         string
   *                          User agent to use.
   * @param acceptLanguage    string
   *                          Browser langugage to emulate.
   * @param platform          string
   *                          The platform navigator.platform should return.
   * @param userAgentMetadata Emulation.UserAgentMetadata
   *                          To be sent in Sec-CH-UA-* headers and returned in navigator.userAgentData
   */
  void setUserAgentOverride(String userAgent, String acceptLanguage, String platform, Emulation.UserAgentMetadata userAgentMetadata);

  /**
   * Tells whether clearing browser cache is supported.
   *
   * @return {
   * result: boolean  True if browser cache can be cleared.
   * }
   */
  JSONObject canClearBrowserCache();

  /**
   * Tells whether clearing browser cookies is supported.
   *
   * @return {
   * result: boolean  True if browser cookies can be cleared.
   * }
   */
  JSONObject canClearBrowserCookies();

  /**
   * Tells whether emulation of network conditions is supported.
   *
   * @return {
   * result: boolean  True if emulation of network conditions is supported.
   * }
   */
  JSONObject canEmulateNetworkConditions();

  /**
   * Returns all browser cookies. Depending on the backend support, will return detailed cookie information in the cookies field. Deprecated. Use Storage.getCookies instead.
   *
   * @return {
   * cookies: array[ Cookie ]  Array of cookie objects.
   * }
   */
  JSONObject getAllCookies();

  /**
   * Clears accepted encodings set by setAcceptedEncodings
   */
  JSONObject clearAcceptedEncodingsOverride();

  /**
   * Enables tracking for the Reporting API, events generated by the Reporting API will now be delivered to the client. Enabling triggers 'reportingApiReportAdded' for all existing reports.
   *
   * @param enable Whether to enable or disable events for the Reporting API
   */
  JSONObject enableReportingApi(boolean enable);

  /**
   * Returns the DER-encoded certificate.
   *
   * @param origin Origin to get certificate for.
   * @return {
   * tableNames: array[ string ]
   * }
   */
  JSONObject getCertificate(String origin);

  /**
   * Returns content served for the given currently intercepted request.
   *
   * @param interceptionId InterceptionId
   *                       Identifier for the intercepted request to get body for.
   * @return {
   * body: string Response body.
   * base64Encoded: boolean True, if content was sent as base64.
   * }
   */
  JSONObject getResponseBodyForInterception(String interceptionId);

  /**
   * Returns information about the COEP/COOP isolation status.
   *
   * @param frameId Page.FrameId
   *                If no frameId is provided, the status of the target is provided.
   * @return {
   * status: SecurityIsolationStatus
   * }
   */
  JSONObject getSecurityIsolationStatus(String frameId);

  /**
   * Fetches the resource and returns the content.
   *
   * @param frameId Page.FrameId
   *                Frame id to get the resource for. Mandatory for frame targets, and should be omitted for worker targets.
   * @param url     string
   *                URL of the resource to get content for.
   * @param options LoadNetworkResourceOptions
   *                Options for the request.
   * @return {
   * resource: LoadNetworkResourcePageResult
   * }
   */
  JSONObject loadNetworkResource(String frameId, String url, LoadNetworkResourceOptions options);

  /**
   * This method sends a new XMLHttpRequest which is identical to the original one. The following parameters should
   * be identical: method, url, async, request body, extra headers, withCredentials attribute, user, password.
   *
   * @param requestId RequestId
   *                  Identifier of XHR to replay.
   */
  void replayXHR(String requestId);

  /**
   * Searches for given string in response content.
   *
   * @param requestId     RequestId
   *                      Identifier of the network response to search.
   * @param query         string
   *                      String to search for.
   * @param caseSensitive boolean
   *                      If true, search is case sensitive.
   * @param isRegex       boolean
   *                      If true, treats string parameter as regex.
   * @return {
   * result: array[ Debugger.SearchMatch ]  List of search matches.
   * }
   */
  JSONObject searchInResponseBody(String requestId, String query, Boolean caseSensitive, Boolean isRegex);

  /**
   * Sets a list of content encodings that will be accepted. Empty list means no encoding is accepted.
   *
   * @param encodings array[ ContentEncoding ]  List of accepted content encodings.
   */
  void setAcceptedEncodings(List<ContentEncoding> encodings);

  /**
   * Specifies whether to attach a page script stack id in requests
   *
   * @param enabled boolean
   *                Whether to attach a page script stack for debugging purpose.
   */
  void setAttachDebugStack(boolean enabled);

  /**
   * Blocks URLs from loading.
   *
   * @param urls array[ string ]
   *             URL patterns to block. Wildcards ('*') are allowed.
   */
  void setBlockedURLs(List<String> urls);

  /**
   * Toggles ignoring of service worker for each request.
   *
   * @param bypass boolean
   *               Bypass service worker and load from network.
   */
  void setBypassServiceWorker(boolean bypass);

  /**
   * Returns a handle to the stream representing the response body. Note that after this command, the intercepted request
   * can't be continued as is -- you either need to cancel it or to provide the response body. The stream only supports
   * sequential read, IO.read will fail if the position is specified.
   *
   * @param interceptionId InterceptionId
   * @return {
   * stream: IO.StreamHandle
   * }
   */
  JSONObject takeResponseBodyForInterceptionAsStream(String interceptionId);

  /**
   * Response to Network.requestIntercepted which either modifies the request to continue with any modifications, or blocks it,
   * or completes it with the provided response bytes. If a network fetch occurs as a result which encounters a redirect an additional
   * Network.requestIntercepted event will be sent with the same InterceptionId. Deprecated, use Fetch.continueRequest,
   * Fetch.fulfillRequest and Fetch.failRequest instead.
   *
   * @param interceptionId        InterceptionId
   * @param errorReason           ErrorReason
   *                              If set this causes the request to fail with the given reason. Passing Aborted for requests marked with isNavigationRequest also cancels the navigation. Must not be set in response to an authChallenge.
   * @param rawResponse           string
   *                              If set the requests completes using with the provided base64 encoded raw response, including HTTP status line and headers etc... Must not be set in response to an authChallenge. (Encoded as a base64 string when passed over JSON)
   * @param url                   string
   *                              If set the request url will be modified in a way that's not observable by page. Must not be set in response to an authChallenge.
   * @param method                string
   *                              If set this allows the request method to be overridden. Must not be set in response to an authChallenge.
   * @param postData              string
   *                              If set this allows postData to be set. Must not be set in response to an authChallenge.
   * @param headers               Headers
   *                              If set this allows the request headers to be changed. Must not be set in response to an authChallenge.
   * @param authChallengeResponse AuthChallengeResponse
   *                              Response to a requestIntercepted with an authChallenge. Must not be set otherwise.
   */
  void continueInterceptedRequest(String interceptionId, ErrorReason errorReason, String rawResponse, String url, String method, String postData, Headers headers, AuthChallengeResponse authChallengeResponse);

  /**
   * Sets the requests to intercept that match the provided patterns and optionally resource types. Deprecated, please use Fetch.enable instead.
   *
   * @param patterns array[ RequestPattern ]
   *                 Requests matching any of these patterns will be forwarded and wait for the corresponding continueInterceptedRequest call.
   */
  void setRequestInterception(List<RequestPattern> patterns);

  /**
   * 事件
   */
  @Event("Network")
  public interface Events {
    /**
     * Fired when data chunk was received over the network.
     *
     * @param requestId         RequestId
     *                          Request identifier.
     * @param timestamp         MonotonicTime
     *                          Timestamp.
     * @param dataLength        integer
     *                          Data chunk length.
     * @param encodedDataLength integer
     *                          Actual bytes received (might be less than dataLength for compressed encodings).
     */
    @Event("dataReceived")
    void dataReceived(String requestId, Long timestamp, Long dataLength, Long encodedDataLength);

    /**
     * Fired when EventSource message is received.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param timestamp MonotonicTime
     *                  Timestamp.
     * @param eventName string
     *                  Message type.
     * @param eventId   string
     *                  Message identifier.
     * @param data      string
     *                  Message content.
     */
    @Event("eventSourceMessageReceived")
    void eventSourceMessageReceived(String requestId, Long timestamp, String eventName, String eventId, String data);

    /**
     * Fired when HTTP request has failed to load.
     *
     * @param requestId       RequestId
     *                        Request identifier.
     * @param timestamp       MonotonicTime
     *                        Timestamp.
     * @param type            ResourceType
     *                        Resource type.
     * @param errorText       string
     *                        User friendly error message.
     * @param canceled        boolean
     *                        True if loading was canceled.
     * @param blockedReason   BlockedReason
     *                        The reason why loading was blocked, if any.
     * @param corsErrorStatus CorsErrorStatus
     *                        The reason why loading was blocked by CORS, if any.
     */
    @Event("loadingFailed")
    void loadingFailed(String requestId, Long timestamp, ResourceType type, String errorText, Boolean canceled, BlockedReason blockedReason, CorsErrorStatus corsErrorStatus);

    /**
     * Fired when HTTP request has finished loading.
     *
     * @param requestId         RequestId
     *                          Request identifier.
     * @param timestamp         MonotonicTime
     *                          Timestamp.
     * @param encodedDataLength number
     *                          Total number of bytes received for this request.
     */
    @Event("loadingFinished")
    void loadingFinished(String requestId, Long timestamp, Long encodedDataLength);

    /**
     * Fired if request ended up loading from cache.
     *
     * @param requestId RequestId
     *                  Request identifier.
     */
    @Event("requestServedFromCache")
    void requestServedFromCache(String requestId);

    /**
     * Fired when page is about to send HTTP request.
     *
     * @param requestId            RequestId
     *                             Request identifier.
     * @param loaderId             LoaderId
     *                             Loader identifier. Empty string if the request is fetched from worker.
     * @param documentURL          string
     *                             URL of the document this request is loaded for.
     * @param request              Request
     *                             Request data.
     * @param timestamp            MonotonicTime
     *                             Timestamp.
     * @param wallTime             TimeSinceEpoch
     *                             Timestamp.
     * @param initiator            Initiator
     *                             Request initiator.
     * @param redirectHasExtraInfo boolean
     *                             In the case that redirectResponse is populated, this flag indicates whether requestWillBeSentExtraInfo and responseReceivedExtraInfo events will be or were emitted for the request which was just redirected. EXPERIMENTAL
     * @param redirectResponse     Response
     *                             Redirect response data.
     * @param type                 ResourceType
     *                             Type of this resource.
     * @param frameId              Page.FrameId
     *                             Frame identifier.
     * @param hasUserGesture       boolean
     *                             Whether the request is initiated by a user gesture. Defaults to false.
     */
    @Event("requestWillBeSent")
    void requestWillBeSent(String requestId, String loaderId, String documentURL, Request request, Long timestamp, Long wallTime,
                           Initiator initiator, Boolean redirectHasExtraInfo, Response redirectResponse, ResourceType type, String frameId, Boolean hasUserGesture);

    /**
     * Fired when HTTP response is available.
     *
     * @param requestId    RequestId
     *                     Request identifier.
     * @param loaderId     LoaderId
     *                     Loader identifier. Empty string if the request is fetched from worker.
     * @param timestamp    MonotonicTime
     *                     Timestamp.
     * @param type         ResourceType
     *                     Resource type.
     * @param response     Response
     *                     Response data.
     * @param hasExtraInfo boolean
     *                     Indicates whether requestWillBeSentExtraInfo and responseReceivedExtraInfo events will be or were emitted for this request. EXPERIMENTAL
     * @param frameId      Page.FrameId
     *                     Frame identifier.
     */
    @Event("responseReceived")
    void responseReceived(String requestId, String loaderId, Long timestamp, ResourceType type, Response response, Boolean hasExtraInfo, String frameId);

    /**
     * Fired when WebSocket is closed.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param timestamp MonotonicTime
     *                  Timestamp.
     */
    @Event("webSocketClosed")
    void webSocketClosed(String requestId, Long timestamp);

    /**
     * Fired upon WebSocket creation.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param url       string
     *                  WebSocket request URL.
     * @param initiator Initiator
     *                  Request initiator.
     */
    @Event("webSocketCreated")
    void webSocketCreated(String requestId, String url, Long initiator);

    /**
     * Fired when WebSocket message error occurs.
     *
     * @param requestId    RequestId
     *                     Request identifier.
     * @param timestamp    MonotonicTime
     *                     Timestamp.
     * @param errorMessage string
     *                     WebSocket error message.
     */
    @Event("webSocketFrameError")
    void webSocketFrameError(String requestId, Long timestamp, String errorMessage);

    /**
     * Fired when WebSocket message is received.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param timestamp MonotonicTime
     *                  Timestamp.
     * @param response  WebSocketFrame
     *                  WebSocket response data.
     */
    @Event("webSocketFrameReceived")
    void webSocketFrameReceived(String requestId, Long timestamp, WebSocketFrame response);

    /**
     * Fired when WebSocket message is sent.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param timestamp MonotonicTime
     *                  Timestamp.
     * @param response  WebSocketFrame
     *                  WebSocket response data.
     */
    @Event("webSocketFrameSent")
    void webSocketFrameSent(String requestId, Long timestamp, WebSocketFrame response);

    /**
     * Fired when WebSocket handshake response becomes available.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param timestamp MonotonicTime
     *                  Timestamp.
     * @param response  WebSocketResponse
     *                  WebSocket response data.
     */
    @Event("webSocketHandshakeResponseReceived")
    void webSocketHandshakeResponseReceived(String requestId, Long timestamp, WebSocketFrame response);

    /**
     * Fired when WebSocket is about to initiate handshake.
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param timestamp MonotonicTime
     *                  Timestamp.
     * @param wallTime  TimeSinceEpoch
     *                  UTC Timestamp.
     * @param request   WebSocketRequest
     *                  WebSocket request data.
     */
    @Event("webSocketWillSendHandshakeRequest")
    void webSocketWillSendHandshakeRequest(String requestId, Long timestamp, Long wallTime, WebSocketRequest request);

    /**
     * Fired when WebTransport is disposed.
     *
     * @param transportId RequestId
     *                    WebTransport identifier.
     * @param timestamp   MonotonicTime
     *                    Timestamp.
     */
    @Event("webTransportClosed")
    void webTransportClosed(String transportId, Long timestamp);

    /**
     * Fired when WebTransport handshake is finished.
     *
     * @param transportId RequestId
     *                    WebTransport identifier.
     * @param timestamp   MonotonicTime
     *                    Timestamp.
     */
    @Event("webTransportConnectionEstablished")
    void webTransportConnectionEstablished(String transportId, Long timestamp);

    /**
     * Fired upon WebTransport creation.
     *
     * @param transportId RequestId
     *                    WebTransport identifier.
     * @param url         string
     *                    WebTransport request URL.
     * @param timestamp   MonotonicTime
     *                    Timestamp.
     * @param initiator   Initiator
     *                    Request initiator.
     */
    @Event("webTransportCreated")
    void webTransportCreated(String transportId, String url, Long timestamp, Initiator initiator);

    /**
     * @param origin    string
     *                  Origin of the document(s) which configured the endpoints.
     * @param endpoints array[ ReportingApiEndpoint ]
     */
    @Event("reportingApiEndpointsChangedForOrigin")
    void reportingApiEndpointsChangedForOrigin(String origin, List<ReportingApiEndpoint> endpoints);

    /**
     * Is sent whenever a new report is added. And after 'enableReportingApi' for all existing reports.
     *
     * @param report ReportingApiReport
     */
    @Event("reportingApiReportAdded")
    void reportingApiReportAdded(ReportingApiReport report);

    /**
     * @param report ReportingApiReport
     */
    @Event("reportingApiReportUpdated")
    void reportingApiReportUpdated(ReportingApiReport report);

    /**
     * Fired when additional information about a requestWillBeSent event is available from the network stack. Not every requestWillBeSent
     * event will have an additional requestWillBeSentExtraInfo fired for it, and there is no guarantee whether requestWillBeSent or
     * requestWillBeSentExtraInfo will be fired first for the same request.
     *
     * @param requestId                     RequestId
     *                                      Request identifier. Used to match this information to an existing requestWillBeSent event.
     * @param associatedCookies             array[ BlockedCookieWithReason ]
     *                                      A list of cookies potentially associated to the requested URL. This includes both cookies sent with the request and the ones not sent; the latter are distinguished by having blockedReason field set.
     * @param headers                       Headers
     *                                      Raw request headers as they will be sent over the wire.
     * @param connectTiming                 ConnectTiming
     *                                      Connection timing information for the request. EXPERIMENTAL
     * @param clientSecurityState           ClientSecurityState
     *                                      The client security state set for the request.
     * @param siteHasCookieInOtherPartition boolean
     *                                      Whether the site has partitioned cookies stored in a partition different than the current one.
     */
    @Event("requestWillBeSentExtraInfo")
    void requestWillBeSentExtraInfo(String requestId, List<BlockedCookieWithReason> associatedCookies, Headers headers, ConnectTiming connectTiming, ClientSecurityState clientSecurityState, Boolean siteHasCookieInOtherPartition);

    /**
     * Fired when resource loading priority is changed
     *
     * @param requestId   RequestId
     *                    Request identifier.
     * @param newPriority ResourcePriority
     *                    New priority
     * @param timestamp   MonotonicTime
     *                    Timestamp.
     */
    @Event("resourceChangedPriority")
    void resourceChangedPriority(String requestId, ResourcePriority newPriority, Long timestamp);

    /**
     * Fired when additional information about a responseReceived event is available from the network stack. Not every responseReceived event
     * will have an additional responseReceivedExtraInfo for it, and responseReceivedExtraInfo may be fired before or after responseReceived.
     *
     * @param requestId                RequestId
     *                                 Request identifier. Used to match this information to another responseReceived event.
     * @param blockedCookies           array[ BlockedSetCookieWithReason ]
     *                                 A list of cookies which were not stored from the response along with the corresponding reasons for blocking. The cookies here may not be valid due to syntax errors, which are represented by the invalid cookie line string instead of a proper cookie.
     * @param headers                  Headers
     *                                 Raw response headers as they were received over the wire.
     * @param resourceIPAddressSpace   IPAddressSpace
     *                                 The IP address space of the resource. The address space can only be determined once the transport established the connection, so we can't send it in requestWillBeSentExtraInfo.
     * @param statusCode               integer
     *                                 The status code of the response. This is useful in cases the request failed and no responseReceived event is triggered, which is the case for, e.g., CORS errors. This is also the correct status code for cached requests, where the status in responseReceived is a 200 and this will be 304.
     * @param headersText              string
     *                                 Raw response header text as it was received over the wire. The raw text may not always be available, such as in the case of HTTP/2 or QUIC.
     * @param cookiePartitionKey       string
     *                                 The cookie partition key that will be used to store partitioned cookies set in this response. Only sent when partitioned cookies are enabled.
     * @param cookiePartitionKeyOpaque boolean
     *                                 True if partitioned cookies are enabled, but the partition key is not serializeable to string.
     */
    @Event("responseReceivedExtraInfo")
    void responseReceivedExtraInfo(String requestId, List<BlockedSetCookieWithReason> blockedCookies, Headers headers, IPAddressSpace resourceIPAddressSpace,
                                   Integer statusCode, String headersText, String cookiePartitionKey, Boolean cookiePartitionKeyOpaque);

    /**
     * Fired when a signed exchange was received over the network
     *
     * @param requestId RequestId
     *                  Request identifier.
     * @param info      SignedExchangeInfo
     *                  Information about the signed exchange response.
     */
    @Event("signedExchangeReceived")
    void signedExchangeReceived(String requestId, SignedExchangeInfo info);

    /**
     * Fired when request for resources within a .wbn file failed.
     *
     * @param innerRequestId  RequestId
     *                        Request identifier of the subresource request
     * @param innerRequestURL string
     *                        URL of the subresource resource.
     * @param errorMessage    string
     *                        Error message
     * @param bundleRequestId RequestId
     *                        Bundle request identifier. Used to match this information to another event. This made be absent in case when the instrumentation was enabled only after webbundle was parsed.
     */
    @Event("subresourceWebBundleInnerResponseError")
    void subresourceWebBundleInnerResponseError(String innerRequestId, String innerRequestURL, String errorMessage, String bundleRequestId);

    /**
     * Fired when handling requests for resources within a .wbn file. Note: this will only be fired for resources that are requested by the webpage.
     *
     * @param innerRequestId  RequestId
     *                        Request identifier of the subresource request
     * @param innerRequestURL string
     *                        URL of the subresource resource.
     * @param bundleRequestId RequestId
     *                        Bundle request identifier. Used to match this information to another event. This made be absent in case when the instrumentation was enabled only after webbundle was parsed.
     */
    @Event("subresourceWebBundleInnerResponseParsed")
    void subresourceWebBundleInnerResponseParsed(String innerRequestId, String innerRequestURL, String bundleRequestId);

    /**
     * Fired once when parsing the .wbn file has failed.
     *
     * @param requestId    RequestId
     *                     Request identifier. Used to match this information to another event.
     * @param errorMessage string
     *                     Error message
     */
    @Event("subresourceWebBundleMetadataError")
    void subresourceWebBundleMetadataError(String requestId, String errorMessage);

    /**
     * Fired once when parsing the .wbn file has succeeded. The event contains the information about the web bundle contents.
     *
     * @param requestId RequestId
     *                  Request identifier. Used to match this information to another event.
     * @param urls      array[ string ]
     *                  A list of URLs of resources in the subresource Web Bundle.
     */
    @Event("subresourceWebBundleMetadataReceived")
    void subresourceWebBundleMetadataReceived(String requestId, List<String> urls);

    /**
     * Fired exactly once for each Trust Token operation. Depending on the type of the operation and whether the operation succeeded
     * or failed, the event is fired before the corresponding request was sent or after the response was received.
     *
     * @param status           string
     *                         Detailed success or error status of the operation. 'AlreadyExists' also signifies a successful operation, as the result of the operation already exists und thus, the operation was abort preemptively (e.g. a cache hit).
     *                         Allowed Values: Ok, InvalidArgument, MissingIssuerKeys, FailedPrecondition, ResourceExhausted, AlreadyExists, Unavailable, Unauthorized, BadResponse, InternalError, UnknownError, FulfilledLocally
     * @param type             TrustTokenOperationType
     * @param requestId        RequestId
     * @param topLevelOrigin   string
     *                         Top level origin. The context in which the operation was attempted.
     * @param issuerOrigin     string
     *                         Origin of the issuer in case of a "Issuance" or "Redemption" operation.
     * @param issuedTokenCount integer
     *                         The number of obtained Trust Tokens on a successful "Issuance" operation.
     */
    @Event("trustTokenOperationDone")
    void trustTokenOperationDone(String status, TrustTokenOperationType type, String requestId, String topLevelOrigin, String issuerOrigin, Integer issuedTokenCount);

    /**
     * Details of an intercepted HTTP request, which must be either allowed, blocked, modified or mocked. Deprecated, use Fetch.requestPaused instead.
     *
     * @param interceptionId      InterceptionId
     *                            Each request the page makes will have a unique id, however if any redirects are encountered while processing that fetch, they will be reported with the same id as the original fetch. Likewise if HTTP authentication is needed then the same fetch id will be used.
     * @param request             Request
     * @param frameId             Page.FrameId
     *                            The id of the frame that initiated the request.
     * @param resourceType        ResourceType
     *                            How the requested resource will be used.
     * @param isNavigationRequest boolean
     *                            Whether this is a navigation request, which can abort the navigation completely.
     * @param isDownload          boolean
     *                            Set if the request is a navigation that will result in a download. Only present after response is received from the server (i.e. HeadersReceived stage).
     * @param redirectUrl         string
     *                            Redirect location, only sent if a redirect was intercepted.
     * @param authChallenge       AuthChallenge
     *                            Details of the Authorization Challenge encountered. If this is set then continueInterceptedRequest must contain an authChallengeResponse.
     * @param responseErrorReason ErrorReason
     *                            Response error if intercepted at response stage or if redirect occurred while intercepting request.
     * @param responseStatusCode  integer
     *                            Response code if intercepted at response stage or if redirect occurred while intercepting request or auth retry occurred.
     * @param responseHeaders     Headers
     *                            Response headers if intercepted at the response stage or if redirect occurred while intercepting request or auth retry occurred.
     * @param requestId           RequestId
     *                            If the intercepted request had a corresponding requestWillBeSent event fired for it, then this requestId will be the same as the requestId present in the requestWillBeSent event.
     */
    @Event("requestIntercepted")
    void requestIntercepted(String interceptionId, Request request, String frameId, ResourceType resourceType, Boolean isNavigationRequest, Boolean isDownload,
                            String redirectUrl, AuthChallenge authChallenge, ErrorReason responseErrorReason, Integer responseStatusCode, Headers responseHeaders, String requestId);
  }

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
    Integer certificateId;
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
