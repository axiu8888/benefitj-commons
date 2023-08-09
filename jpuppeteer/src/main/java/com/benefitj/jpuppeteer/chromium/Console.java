package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.ChromiumApi;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

/**
 * This domain is deprecated - use Runtime or Log instead. DEPRECATED
 */
public interface Console extends ChromiumApi {

  /**
   * Does nothing.
   */
  void clearMessages();

  /**
   * Disables console domain, prevents further console messages from being reported to the client.
   */
  void disable();

  /**
   * Enables console domain, sends the messages collected so far to the client by means of the messageAdded notification.
   */
  void enable();

  /**
   * Issued when new console message is added.
   *
   * @param message ConsoleMessage
   *                Console message that has been added.
   */
  @Event("messageAdded")
  void messageAdded(ConsoleMessage message);

  /**
   * Console message.
   */
  @Data
  public class ConsoleMessage {
    /**
     * Message source.
     * Allowed Values: xml, javascript, network, console-api, storage, appcache, rendering, security, other, deprecation, worker
     */
    String source;
    /**
     * Message severity.
     * Allowed Values: log, warning, error, debug, info
     */
    String level;
    /**
     * Message text.
     */
    String text;
    /**
     * URL of the message origin.
     */
    String url;
    /**
     * Line number in the resource that generated this message (1-based).
     */
    Integer line;

    /**
     * Column number in the resource that generated this message (1-based).
     */
    Integer column;
  }

}
