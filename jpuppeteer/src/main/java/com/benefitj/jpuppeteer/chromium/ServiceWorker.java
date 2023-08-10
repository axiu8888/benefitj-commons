package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ServiceWorker Domain
 */
@ChromiumApi("ServiceWorker")
public interface ServiceWorker {


  /**
   * @param origin         string
   * @param registrationId RegistrationID
   * @param data           string
   */
  void deliverPushMessag(String origin, String registrationId, String data);

  void disable();

  /**
   * @param origin         string
   * @param registrationId RegistrationID
   * @param tag            string
   */
  void dispatchPeriodicSyncEvent(String origin, String registrationId, String tag);

  /**
   * @param origin         string
   * @param registrationId RegistrationID
   * @param tag            string
   * @param lastChance     boolean
   */
  void dispatchSyncEvent(String origin, String registrationId, String tag, Boolean lastChance);

  /**
   *
   */
  void enable();

  /**
   * @param versionId string
   */
  void inspectWorker(String versionId);

  /**
   * @param forceUpdateOnPageLoad boolean
   */
  void setForceUpdateOnPageLoad(Boolean forceUpdateOnPageLoad);

  /**
   * @param scopeURL String
   */
  void skipWaiting(String scopeURL);

  /**
   * @param scopeURL String
   */
  void startWorker(String scopeURL);

  /**
   *
   */
  void stopAllWorkers();

  /**
   * @param versionId String
   */
  void stopWorker(String versionId);

  /**
   * @param scopeURL String
   */
  void unregister(String scopeURL);

  /**
   * @param scopeURL String
   */
  void updateRegistration(String scopeURL);

  /**
   * 事件
   */
  @Event("ServiceWorker")
  public interface Events {

    /**
     * @param errorMessage ServiceWorkerErrorMessage
     */
    @Event("workerErrorReported")
    void workerErrorReported(ServiceWorkerErrorMessage errorMessage);

    /**
     * @param registrations array[ ServiceWorkerRegistration ]
     */
    @Event("workerRegistrationUpdated")
    void workerRegistrationUpdated(List<ServiceWorkerRegistration> registrations);

    /**
     * @param versions array[ ServiceWorkerVersion ]
     */
    @Event("workerVersionUpdated")
    void workerVersionUpdated(List<ServiceWorkerVersion> versions);

  }

  /**
   * ServiceWorker error message.
   */
  @Data
  public class ServiceWorkerErrorMessage {
    String errorMessage;
    String registrationId;
    String versionId;
    String sourceURL;
    Integer lineNumber;
    Integer columnNumber;
  }

  /**
   * ServiceWorker registration.
   */
  @Data
  public class ServiceWorkerRegistration {
    String registrationId;
    String scopeURL;
    boolean isDeleted;
  }

  /**
   * ServiceWorker version.
   */
  @Data
  public class ServiceWorkerVersion {
    String versionId;
    String registrationId;
    /**
     * ServiceWorkerVersionRunningStatus runningStatus;
     */
    String scriptURL;
    ServiceWorkerVersionStatus status;
    /**
     * The Last-Modified header value of the main script.
     */
    Integer scriptLastModified;
    /**
     * The time at which the response headers of the main script were received from the server. For cached script it is the last time the cache entry was validated.
     */
    Long scriptResponseTime;
    /**
     * array[ Target.TargetID ]
     */
    List<String> controlledClients;
    /**
     * Target.TargetID
     */
    String targetId;
  }

  /**
   * Allowed Values: stopped, starting, running, stopping
   */
  public enum ServiceWorkerVersionRunningStatus {
    stopped, starting, running, stopping
  }

  /**
   * Allowed Values: new, installing, installed, activating, activated, redundant
   */
  public enum ServiceWorkerVersionStatus {
    _new, installing, installed, activating, activated, redundant;

    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        "new",
        "installing",
        "installed",
        "activating",
        "activated",
        "redundant"
    ));
  }

}
