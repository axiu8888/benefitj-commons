package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * Defines events for background web platform features. EXPERIMENTAL
 */
@ChromiumApi("BackgroundService")
public interface BackgroundService {

  /**
   * Clears all stored data for the service.
   *
   * @param service ServiceName
   */
  void clearEvents(ServiceName service);

  /**
   * Set the recording state for the service.
   *
   * @param shouldRecord boolean
   * @param service      ServiceName
   */
  void setRecording(Boolean shouldRecord, ServiceName service);

  /**
   * Enables event updates for the service.
   *
   * @param service ServiceName
   */
  void startObserving(ServiceName service);

  /**
   * Disables event updates for the service.
   *
   * @param service ServiceName
   */
  void stopObserving(ServiceName service);

  /**
   * Called with all existing backgroundServiceEvents when enabled, and all new events afterwards if enabled and recording.
   *
   * @param backgroundServiceEvent BackgroundServiceEvent
   */
  @Event("backgroundServiceEventReceived")
  void backgroundServiceEventReceived(BackgroundServiceEvent backgroundServiceEvent);

  /**
   * Called when the recording state for the service has been updated.
   *
   * @param isRecording boolean
   * @param service     ServiceName
   */
  @Event("recordingStateChanged")
  void recordingStateChanged(boolean isRecording, ServiceName service);

  @Data
  public class BackgroundServiceEvent {
    /**
     * Timestamp of the event (in seconds).
     */
    Long timestamp;
    /**
     * The origin this event belongs to.
     */
    String origin;
    /**
     * The Service Worker ID that initiated the event.
     * ServiceWorker.RegistrationID
     */
    String serviceWorkerRegistrationId;
    /**
     * The Background Service this event belongs to.
     */
    ServiceName service;
    /**
     * A description of the event.
     */
    String eventName;
    /**
     * An identifier that groups related events together.
     */
    String instanceId;
    /**
     * A list of event-specific information.
     */
    List<EventMetadata> eventMetadata;
    /**
     * Storage key this event belongs to.
     */
    String storageKey;
  }

  /**
   * A key-value pair for additional event information to pass along.
   */
  @Data
  public class EventMetadata {
    String key;
    String value;
  }

  /**
   * The Background Service that will be associated with the commands/events. Every Background Service operates independently, but they share the same API.
   * Allowed Values: backgroundFetch, backgroundSync, pushMessaging, notifications, paymentHandler, periodicBackgroundSync
   */
  public enum ServiceName {
    backgroundFetch, backgroundSync, pushMessaging, notifications, paymentHandler, periodicBackgroundSync
  }

}
