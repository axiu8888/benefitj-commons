package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * A domain for interacting with Cast, Presentation API, and Remote Playback API functionalities.
 */
@ChromiumApi("Cast")
public interface Cast {

  /**
   * Stops observing for sinks and issues.
   */
  void disable();

  /**
   * Starts observing for sinks that can be used for tab mirroring, and if set, sinks compatible with |presentationUrl| as well.
   * When sinks are found, a |sinksUpdated| event is fired. Also starts observing for issue messages. When an issue is added
   * or removed, an |issueUpdated| event is fired.
   *
   * @param presentationUrl string
   */
  void enable(String presentationUrl);

  /**
   * Sets a sink to be used when the web page requests the browser to choose a sink via Presentation API, Remote Playback API, or Cast SDK.
   *
   * @param sinkName string
   */
  void setSinkToUse(String sinkName);

  /**
   * Starts mirroring the desktop to the sink.
   *
   * @param sinkName string
   */
  void startDesktopMirroring(String sinkName);

  /**
   * Starts mirroring the tab to the sink.
   *
   * @param sinkName string
   */
  void startTabMirroring(String sinkName);

  /**
   * Stops the active Cast session on the sink.
   *
   * @param sinkName string
   */
  void stopCasting(String sinkName);

  @Event("Cast")
  public interface Events {

    /**
     * This is fired whenever the outstanding issue/error message changes. |issueMessage| is empty if there is no issue.
     *
     * @param issueMessage string
     */
    @Event("issueUpdated")
    void issueUpdated(String issueMessage);

    /**
     * This is fired whenever the list of available sinks changes. A sink is a device or a software surface that you can cast to.
     *
     * @param sinks array[ Sink ]
     */
    @Event("sinksUpdated")
    void sinksUpdated(List<Sink> sinks);

  }

  /**
   *
   */
  @Data
  public class Sink {
    String name;
    String id;
    /**
     * Text describing the current session. Present only if there is an active session on the sink.
     */
    String session;
  }

}
