package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Security
 */
@ChromiumApi("Security")
public interface Security {

  /**
   * Disables tracking security state changes.
   */
  void disable();

  /**
   * Enables tracking security state changes.
   */
  void enable();

  /**
   * Handles a certificate error that fired a certificateError event.
   *
   * @param eventId The ID of the event.
   * @param action  The action to take on the certificate error.
   */
  @Deprecated
  void handleCertificateError(Integer eventId, CertificateErrorAction action);

  /**
   * Enable/disable overriding certificate errors. If enabled, all certificate error events need to be handled by the DevTools client and should be answered with handleCertificateError commands.
   *
   * @param override If true, certificate errors will be overridden.
   */
  @Deprecated
  void setOverrideCertificateErrors(boolean override);

  /**
   * Enable/disable whether all certificate errors should be ignored.
   *
   * @param ignore If true, all certificate errors will be ignored.
   */
  void setIgnoreCertificateErrors(boolean ignore);

  /**
   * There is a certificate error. If overriding certificate errors is enabled, then it should be handled with the handleCertificateError command. Note: this event does not fire if the certificate error has been allowed internally. Only one client per target should override certificate errors at the same time.
   *
   * @param eventId    The ID of the event.
   * @param errorType  The type of the error.
   * @param requestURL The url that was requested.
   */
  @Event("certificateError")
  void certificateError(String eventId, String errorType, String requestURL);

  /**
   * The security state of the page changed. No longer being sent.
   *
   * @param securityState         Security state.
   * @param schemeIsCryptographic True if the page was loaded over cryptographic transport such as HTTPS. DEPRECATED
   * @param explanations          Previously a list of explanations for the security state. Now always empty. DEPRECATED
   * @param insecureContentStatus Information about insecure content on the page. DEPRECATED
   * @param summary               Overrides user-visible description of the state. Always omitted. DEPRECATED
   */
  @Event("securityStateChanged")
  void securityStateChanged(SecurityState securityState, boolean schemeIsCryptographic, List<SecurityStateExplanation> explanations, InsecureContentStatus insecureContentStatus, String summary);

  /**
   * The security state of the page changed.
   *
   * @param visibleSecurityState Security state information about the page.
   */
  @Event("visibleSecurityStateChanged")
  void visibleSecurityStateChanged(VisibleSecurityState visibleSecurityState);

  /**
   * The action to take when a certificate error occurs. continue will continue processing the request and cancel will cancel the request.
   * Allowed Values: continue, cancel
   */
  public enum CertificateErrorAction {
  }

  /**
   * A description of mixed content (HTTP resources on HTTPS pages), as defined by https://www.w3.org/TR/mixed-content/#categories
   * Allowed Values: blockable, optionally-blockable, none
   */
  public enum MixedContentType {
    blockable, optionally_blockable, none
  }

  /**
   * The security level of a page or resource.
   * Allowed Values: unknown, neutral, insecure, secure, info, insecure-broken
   */
  public enum SecurityState {
    unknown, neutral, insecure, secure, info, insecure_broken;

    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        ("unknown"),
        ("neutral"),
        ("insecure"),
        ("secure"),
        ("info"),
        ("insecure-broken")
    ));
  }

  /**
   * An explanation of an factor contributing to the security state.
   */
  @Data
  public class SecurityStateExplanation {
    /**
     * Security state representing the severity of the factor being explained.
     */
    SecurityState securityState;
    /**
     * Title describing the type of factor.
     */
    String title;
    /**
     * Short phrase describing the type of factor.
     */
    String summary;
    /**
     * Full text explanation of the factor.
     */
    String description;
    /**
     * The type of mixed content described by the explanation.
     */
    MixedContentType mixedContentType;
    /**
     * Page certificate.
     */
    List<String> certificate;
    /**
     * Recommendations to fix any issues.
     */
    List<String> recommendations;
  }

  /**
   * Information about insecure content on the page.
   */
  @Data
  public class InsecureContentStatus {
    /**
     * Always false.
     */
    boolean ranMixedContent;
    /**
     * Always false.
     */
    boolean displayedMixedContent;
    /**
     * Always false.
     */
    boolean containedMixedForm;
    /**
     * Always false.
     */
    boolean ranContentWithCertErrors;
    /**
     * Always false.
     */
    boolean displayedContentWithCertErrors;
    /**
     * Always set to unknown.
     */
    SecurityState ranInsecureContentStyle;
    /**
     * Always set to unknown.
     */
    SecurityState displayedInsecureContentStyle;
  }

  /**
   * Details about the security state of the page certificate.
   */
  @Data
  public class CertificateSecurityState {
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
     * Page certificate.
     */
    List<String> certificate;
    /**
     * Certificate subject name.
     */
    String subjectName;
    /**
     * Name of the issuing CA.
     */
    String issuer;
    /**
     * Network.TimeSinceEpoch
     * Certificate valid from date.
     */
    Long validFrom;
    /**
     * Network.TimeSinceEpoch
     * Certificate valid to (expiration) date
     */
    Long validTo;
    /**
     * The highest priority network error code, if the certificate has an error.
     */
    String certificateNetworkError;
    /**
     * True if the certificate uses a weak signature aglorithm.
     */
    boolean certificateHasWeakSignature;
    /**
     * True if the certificate has a SHA1 signature in the chain.
     */
    boolean certificateHasSha1Signature;
    /**
     * True if modern SSL
     */
    boolean modernSSL;
    /**
     * True if the connection is using an obsolete SSL protocol.
     */
    boolean obsoleteSslProtocol;
    /**
     * True if the connection is using an obsolete SSL key exchange.
     */
    boolean obsoleteSslKeyExchange;
    /**
     * True if the connection is using an obsolete SSL cipher.
     */
    boolean obsoleteSslCipher;
    /**
     * True if the connection is using an obsolete SSL signature.
     */
    boolean obsoleteSslSignature;
  }

  /**
   *
   */
  @Data
  public class SafetyTipInfo {
    /**
     * Describes whether the page triggers any safety tips or reputation warnings. Default is unknown.
     */
    SafetyTipStatus safetyTipStatus;
    /**
     * The URL the safety tip suggested ("Did you mean?"). Only filled in for lookalike matches.
     */
    String safeUrl;
  }

  /**
   * Allowed Values: badReputation, lookalike
   */
  public enum SafetyTipStatus {
    badReputation, lookalike
  }

  /**
   * Security state information about the page.
   */
  @Data
  public class VisibleSecurityState {
    /**
     * The security level of the page.
     */
    SecurityState securityState;
    /**
     * Security state details about the page certificate.
     */
    CertificateSecurityState certificateSecurityState;
    /**
     * The type of Safety Tip triggered on the page. Note that this field will be set even if the Safety Tip UI was not actually shown.
     */
    SafetyTipInfo safetyTipInfo;
    /**
     * Array of security state issues ids.
     */
    List<String> securityStateIssueIds;
  }

}
