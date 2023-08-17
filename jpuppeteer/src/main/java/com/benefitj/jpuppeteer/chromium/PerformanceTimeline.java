package com.benefitj.jpuppeteer.chromium;


import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * Reporting of performance timeline events, as specified in https://w3c.github.io/performance-timeline/#dom-performanceobserver.
 */
@ChromiumApi("PerformanceTimeline")
public interface PerformanceTimeline {

  /**
   * Previously buffered events would be reported before method returns. See also: timelineEventAdded
   *
   * @param eventTypes array[ string ]
   *                   The types of event to report, as specified in https://w3c.github.io/performance-timeline/#dom-performanceentry-entrytype
   *                   The specified filter overrides any previous filters, passing empty filter disables recording. Note that not all types
   *                   exposed to the web platform are currently supported.
   */
  void enable(String eventTypes);

  @Event("PerformanceTimeline")
  public interface Events {

    /**
     * Sent when a performance timeline event is added. See reportPerformanceTimeline method.
     *
     * @param event TimelineEvent
     */
    @Event("timelineEventAdded")
    void timelineEventAdded(TimelineEvent event);
  }

  /**
   * See https://github.com/WICG/LargestContentfulPaint and largest_contentful_paint.idl
   */
  @Data
  public class LargestContentfulPaint {
    /**
     * Network.TimeSinceEpoch
     */
    Long renderTime;
    /**
     * Network.TimeSinceEpoch
     */
    Long loadTime;
    /**
     * The number of pixels being painted.
     */
    Integer size;
    /**
     * The id attribute of the element, if available.
     */
    String elementId;
    /**
     * The URL of the image (may be trimmed).
     */
    String url;
    /**
     * DOM.BackendNodeId
     */
    String nodeId;
  }

  /**
   * See https://wicg.github.io/layout-instability/#sec-layout-shift and layout_shift.idl
   */
  @Data
  public class LayoutShift {
    /**
     * Score increment produced by this event.
     */
    Number value;
    /**
     *
     */
    boolean hadRecentInput;
    /**
     * Network.TimeSinceEpoch
     */
    Long lastInputTime;
    /**
     * array[ LayoutShiftAttribution ]
     */
    List<LayoutShiftAttribution> sources;
  }

  /**
   *
   */
  @Data
  public class LayoutShiftAttribution {
    DOM.Rect previousRect;
    DOM.Rect currentRect;
    /**
     * DOM.BackendNodeId
     */
    String nodeId;
  }

  /**
   *
   */
  @Data
  public class TimelineEvent {
    /**
     * Identifies the frame that this event is related to. Empty for non-frame targets.
     * Page.FrameId
     */
    String frameId;
    /**
     * The event type, as specified in https://w3c.github.io/performance-timeline/#dom-performanceentry-entrytype This determines which of the optional "details" fiedls is present.
     */
    String type;
    /**
     * Name may be empty depending on the type.
     */
    String name;
    /**
     * Time in seconds since Epoch, monotonically increasing within document lifetime.
     * Network.TimeSinceEpoch
     */
    Long time;
    /**
     * Event duration, if applicable.
     */
    Long duration;
    LargestContentfulPaint lcpDetails;
    LayoutShift layoutShiftDetails;
  }

}
