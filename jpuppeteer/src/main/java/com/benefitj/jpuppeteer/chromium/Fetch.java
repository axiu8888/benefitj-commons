package com.benefitj.jpuppeteer.chromium;


import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * A domain for letting clients substitute browser's network layer with client code.
 */
@ChromiumApi("Fetch")
public interface Fetch {

  /**
   * Continues the request, optionally modifying some of its parameters.
   *
   * @param requestId         RequestId
   *                          An id the client received in requestPaused event.
   * @param url               string
   *                          If set, the request url will be modified in a way that's not observable by page.
   * @param method            string
   *                          If set, the request method is overridden.
   * @param postData          string
   *                          If set, overrides the post data in the request. (Encoded as a base64 string when passed over JSON)
   * @param headers           array[ HeaderEntry ]
   *                          If set, overrides the request headers. Note that the overrides do not extend to subsequent redirect hops, if a redirect happens. Another override may be applied to a different request produced by a redirect.
   * @param interceptResponse boolean
   *                          If set, overrides response interception behavior for this request. EXPERIMENTAL
   */
  void continueRequest(String requestId, String url, String method, String postData, List<HeaderEntry> headers, Boolean interceptResponse);

  /**
   * Continues a request supplying authChallengeResponse following authRequired event.
   *
   * @param requestId             RequestId
   *                              An id the client received in authRequired event.
   * @param authChallengeResponse AuthChallengeResponse
   *                              Response to with an authChallenge.
   */
  void continueWithAuth(String requestId, AuthChallengeResponse authChallengeResponse);

  /**
   * Disables the fetch domain.
   */
  void disable();

  /**
   * Enables issuing of requestPaused events. A request will be paused until client calls one of failRequest, fulfillRequest or continueRequest/continueWithAuth.
   *
   * @param patterns           array[ RequestPattern ]
   *                           If specified, only requests matching any of these patterns will produce fetchRequested event and will be paused until clients response. If not set, all requests will be affected.
   * @param handleAuthRequests boolean
   *                           If true, authRequired events will be issued and requests will be paused expecting a call to continueWithAuth.
   */
  void enable(List<RequestPattern> patterns, Boolean handleAuthRequests);

  /**
   * Causes the request to fail with specified reason.
   *
   * @param requestId   RequestId
   *                    An id the client received in requestPaused event.
   * @param errorReason Network.ErrorReason
   *                    Causes the request to fail with the given reason.
   */
  void failRequest(String requestId, Network.ErrorReason errorReason);

  /**
   * Provides response to the request.
   *
   * @param requestId             RequestId
   *                              An id the client received in requestPaused event.
   * @param responseCode          integer
   *                              An HTTP response code.
   * @param responseHeaders       array[ HeaderEntry ]
   *                              Response headers.
   * @param binaryResponseHeaders string
   *                              Alternative way of specifying response headers as a \0-separated series of name: value pairs. Prefer the above method unless you need to represent some non-UTF8 values that can't be transmitted over the protocol as text. (Encoded as a base64 string when passed over JSON)
   * @param body                  string
   *                              A response body. If absent, original response body will be used if the request is intercepted at the response stage and empty body will be used if the request is intercepted at the request stage. (Encoded as a base64 string when passed over JSON)
   * @param responsePhrase        string
   *                              A textual representation of responseCode. If absent, a standard phrase matching responseCode is used.
   */
  void fulfillRequest(String requestId, Integer responseCode, List<HeaderEntry> responseHeaders,
                      String binaryResponseHeaders, String body, String responsePhrase);

  /**
   * Causes the body of the response to be received from the server and returned as a single string. May only be issued for a request that is paused in the Response stage and is mutually exclusive with takeResponseBodyForInterceptionAsStream. Calling other methods that affect the request or disabling fetch domain before body is received results in an undefined behavior. Note that the response body is not available for redirects. Requests paused in the redirect received state may be differentiated by responseCode and presence of location response header, see comments to requestPaused for details.
   *
   * @param requestId RequestId
   *                  Identifier for the intercepted request to get body for.
   * @return {
   * body: string  Response body.
   * base64Encoded: boolean  True, if content was sent as base64.
   * }
   */
  JSONObject getResponseBody(String requestId);

  /**
   * Returns a handle to the stream representing the response body. The request must be paused in the HeadersReceived stage.
   * Note that after this command the request can't be continued as is -- client either needs to cancel it or to provide the
   * response body. The stream only supports sequential read, IO.read will fail if the position is specified. This method
   * is mutually exclusive with getResponseBody. Calling other methods that affect the request or disabling fetch domain
   * before body is received results in an undefined behavior.
   *
   * @param requestId RequestId
   * @return {
   * stream: IO.StreamHandle
   * }
   */
  JSONObject takeResponseBodyAsStream(String requestId);

  /**
   * Continues loading of the paused response, optionally modifying the response headers. If either responseCode or
   * headers are modified, all of them must be present.
   *
   * @param requestId             RequestId
   *                              An id the client received in requestPaused event.
   * @param responseCode          integer
   *                              An HTTP response code. If absent, original response code will be used.
   * @param responsePhrase        string
   *                              A textual representation of responseCode. If absent, a standard phrase matching responseCode is used.
   * @param responseHeaders       array[ HeaderEntry ]
   *                              Response headers. If absent, original response headers will be used.
   * @param binaryResponseHeaders string
   *                              Alternative way of specifying response headers as a \0-separated series of name: value pairs. Prefer the above method unless you need to represent some non-UTF8 values that can't be transmitted over the protocol as text. (Encoded as a base64 string when passed over JSON)
   */
  void continueResponse(String requestId, Integer responseCode, String responsePhrase, List<HeaderEntry> responseHeaders, String binaryResponseHeaders);

  /**
   * 事件
   */
  @Event("Fetch")
  public interface Events {

    /**
     * Issued when the domain is enabled with handleAuthRequests set to true. The request is paused until client responds with continueWithAuth.
     *
     * @param requestId     RequestId
     *                      Each request the page makes will have a unique id.
     * @param request       Network.Request
     *                      The details of the request.
     * @param frameId       Page.FrameId
     *                      The id of the frame that initiated the request.
     * @param resourceType  Network.ResourceType
     *                      How the requested resource will be used.
     * @param authChallenge AuthChallenge
     *                      Details of the Authorization Challenge encountered. If this is set, client should respond with continueRequest that contains AuthChallengeResponse.
     */
    @Event("authRequired")
    void authRequired(String requestId, Network.Request request, String frameId, Network.ResourceType resourceType, AuthChallenge authChallenge);

    /**
     * Issued when the domain is enabled and the request URL matches the specified filter. The request is paused until the
     * client responds with one of continueRequest, failRequest or fulfillRequest. The stage of the request can be determined
     * by presence of responseErrorReason and responseStatusCode -- the request is at the response stage if either of these
     * fields is present and in the request stage otherwise. Redirect responses and subsequent requests are reported similarly
     * to regular responses and requests. Redirect responses may be distinguished by the value of responseStatusCode (which is
     * one of 301, 302, 303, 307, 308) along with presence of the location header. Requests resulting from a redirect will
     * have redirectedRequestId field set.
     *
     * @param requestId           RequestId
     *                            Each request the page makes will have a unique id.
     * @param request             Network.Request
     *                            The details of the request.
     * @param frameId             Page.FrameId
     *                            The id of the frame that initiated the request.
     * @param resourceType        Network.ResourceType
     *                            How the requested resource will be used.
     * @param responseErrorReason Network.ErrorReason
     *                            Response error if intercepted at response stage.
     * @param responseStatusCode  integer
     *                            Response code if intercepted at response stage.
     * @param responseStatusText  string
     *                            Response status text if intercepted at response stage.
     * @param responseHeaders     array[ HeaderEntry ]
     *                            Response headers if intercepted at the response stage.
     * @param networkId           Network.RequestId
     *                            If the intercepted request had a corresponding Network.requestWillBeSent event fired for it, then this networkId will be the same as the requestId present in the requestWillBeSent event.
     * @param redirectedRequestId RequestId
     *                            If the request is due to a redirect response from the server, the id of the request that has caused the redirect. EXPERIMENTAL
     */
    @Event("requestPaused")
    void requestPaused(String requestId, Network.Request request, String frameId, Network.ResourceType resourceType,
                       Network.ErrorReason responseErrorReason, Integer responseStatusCode, String responseStatusText,
                       List<HeaderEntry> responseHeaders, String networkId, String redirectedRequestId);

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
   * Response HTTP header entry
   */
  @Data
  public class HeaderEntry {
    String name;
    String value;
  }

  /**
   *
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
    Network.ResourceType resourceType;
    /**
     * Stage at which to begin intercepting requests. Default is Request.
     */
    RequestStage requestStage;
  }

  /**
   * Stages of the request to handle. Request will intercept before the request is sent. Response will intercept after the response is received (but before response body is received).
   * Allowed Values: Request, Response
   */
  public enum RequestStage {
    Request, Response
  }

}
