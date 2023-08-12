package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * Provides access to log entries.
 */
@ChromiumApi("Log")
public interface Log {

  /**
   * Clears the log.
   */
  void clear();

  /**
   * Disables log domain, prevents further log entries from being reported to the client.
   */
  void disable();

  /**
   * Enables log domain, sends the entries collected so far to the client by means of the entryAdded notification.
   */
  void enable();

  /**
   * start violation reporting.
   *
   * @param config array[ ViolationSetting ]
   *               Configuration for violations.
   */
  void startViolationsReport(List<ViolationSetting> config);

  /**
   * Stop violation reporting.
   */
  void stopViolationsReport();

  /**
   * 事件
   */
  @Event("Log")
  public interface Events {
    /**
     * Issued when new message was logged.
     *
     * @param entry LogEntry
     *              The entry.
     */
    @Event("entryAdded")
    void entryAdded(LogEntry entry);

  }

  /**
   * Log entry.
   */
  @Data
  public class LogEntry {
    /**
     * Log entry source.
     * Allowed Values: xml, javascript, network, storage, appcache, rendering, security, deprecation, worker, violation, intervention, recommendation, other
     */
    String source;
    /**
     * Log entry severity.
     * Allowed Values: verbose, info, warning, error
     */
    String level;
    /**
     * Logged text.
     */
    String text;
    /**
     * Allowed Values: cors
     */
    String category;
    /**
     * Timestamp when this entry was added.
     * Runtime.Timestamp
     */
    Long timestamp;
    /**
     * URL of the resource if known.
     */
    String url;
    /**
     * Line number in the resource.
     */
    Integer lineNumber;
    /**
     * JavaScript stack trace.
     */
    Runtime.StackTrace stackTrace;
    /**
     * Identifier of the network request associated with this entry.
     * Network.RequestId
     */
    String networkRequestId;
    /**
     * Identifier of the worker associated with this entry.
     */
    String workerId;
    /**
     * Call arguments.
     * array[ Runtime.RemoteObject ]
     */
    List<Runtime.RemoteObject> args;
  }

  /**
   * Violation configuration setting.
   */
  @Data
  public class ViolationSetting {
    /**
     * Violation type.
     * Allowed Values: longTask, longLayout, blockedEvent, blockedParser, discouragedAPIUse, handler, recurringHandler
     */
    String name;
    /**
     * Time threshold to trigger upon.
     */
    Integer threshold;
  }

}
