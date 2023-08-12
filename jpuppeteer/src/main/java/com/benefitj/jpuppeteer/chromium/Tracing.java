package com.benefitj.jpuppeteer.chromium;


import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Tracing Domain
 */
@ChromiumApi("Tracing")
public interface Tracing {

  /**
   * Stop trace events collection.
   */
  void end();

  /**
   * Gets supported tracing categories.
   *
   * @param categories array[ string ]
   *                   A list of supported tracing categories.
   */
  void getCategories(List<String> categories);

  /**
   * Record a clock sync marker in the trace.
   *
   * @param syncId string
   *               The ID of this clock sync marker
   */
  void recordClockSyncMarker(String syncId);

  /**
   * Request a global memory dump.
   *
   * @param syncId        deterministic  boolean  Enables more deterministic results by forcing garbage collection
   * @param levelOfDetail MemoryDumpLevelOfDetail  Specifies level of details in memory dump. Defaults to "detailed".
   * @return {
   * dumpGuid: string  GUID of the resulting global memory dump.
   * success: boolean  True iff the global memory dump succeeded.
   * }
   */
  JSONObject requestMemoryDump(String syncId, MemoryDumpLevelOfDetail levelOfDetail);

  /**
   * Start trace events collection.
   *
   * @param categories                   string
   *                                     Category/tag filter DEPRECATED
   * @param options                      string
   *                                     Tracing options DEPRECATED
   * @param bufferUsageReportingInterval number
   *                                     If set, the agent will issue bufferUsage events at this interval, specified in milliseconds
   * @param transferMode                 string
   *                                     Whether to report trace events as series of dataCollected events or to save trace to a stream (defaults to ReportEvents).
   *                                     Allowed Values: ReportEvents, ReturnAsStream
   * @param streamFormat                 StreamFormat
   *                                     Trace data format to use. This only applies when using ReturnAsStream transfer mode (defaults to json).
   * @param streamCompression            StreamCompression
   *                                     Compression format to use. This only applies when using ReturnAsStream transfer mode (defaults to none)
   * @param traceConfig                  TraceConfig
   * @param perfettoConfig               string
   *                                     Base64-encoded serialized perfetto.protos.TraceConfig protobuf message When specified, the parameters categories, options, traceConfig are ignored. (Encoded as a base64 string when passed over JSON)
   * @param tracingBackend               TracingBackend
   *                                     Backend type (defaults to auto)
   */
  void start(String categories, String options, Long bufferUsageReportingInterval, String transferMode, String streamFormat,
             StreamCompression streamCompression, TraceConfig traceConfig, String perfettoConfig, TracingBackend tracingBackend);

  @Event("Tracing")
  public interface Events {

    /**
     * @param percentFull number
     *                    A number in range [0..1] that indicates the used size of event buffer as a fraction of its total size.
     * @param eventCount  number
     *                    An approximate number of events in the trace log.
     * @param value       number
     *                    A number in range [0..1] that indicates the used size of event buffer as a fraction of its total size.
     */
    @Event("bufferUsage")
    void bufferUsage(Double percentFull, Integer eventCount, Double value);

    /**
     * Contains a bucket of collected trace events. When tracing is stopped collected events will be sent as a sequence
     * of dataCollected events followed by tracingComplete event.
     *
     * @param value array[ object ]
     */
    @Event("dataCollected")
    void dataCollected(List<JSONObject> value);

    /**
     * Signals that tracing is stopped and there is no trace buffers pending flush, all data were delivered via dataCollected events.
     *
     * @param dataLossOccurred  boolean
     *                          Indicates whether some trace data is known to have been lost, e.g. because the trace ring buffer wrapped around.
     * @param stream            IO.StreamHandle
     *                          A handle of the stream that holds resulting trace data.
     * @param traceFormat       StreamFormat
     *                          Trace data format of returned stream.
     * @param streamCompression StreamCompression
     *                          Compression format of returned stream.
     */
    @Event("tracingComplete")
    void tracingComplete(Boolean dataLossOccurred, IO.StreamHandle stream, StreamFormat traceFormat, StreamCompression streamCompression);

  }

  /**
   * Configuration for memory dump. Used only when "memory-infra" category is enabled.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class MemoryDumpConfig extends JSONObject {
  }

  /**
   * Details exposed when memory request explicitly declared. Keep consistent with memory_dump_request_args.h and memory_instrumentation.mojom
   * Allowed Values: background, light, detailed
   */
  public enum MemoryDumpLevelOfDetail {
    background, light, detailed
  }

  /**
   * Compression type to use for traces returned via streams.
   * Allowed Values: none, gzip
   */
  public enum StreamCompression {
    none, gzip
  }

  /**
   * Data format of a trace. Can be either the legacy JSON format or the protocol buffer format. Note that the JSON format will be deprecated soon.
   * Allowed Values: json, proto
   */
  public enum StreamFormat {
    json, proto
  }

  /**
   *
   */
  @Data
  public class TraceConfig {
    /**
     * Controls how the trace buffer stores data.
     * Allowed Values: recordUntilFull, recordContinuously, recordAsMuchAsPossible, echoToConsole
     */
    String recordMode;
    /**
     * Size of the trace buffer in kilobytes. If not specified or zero is passed, a default value of 200 MB would be used.
     */
    Long traceBufferSizeInKb;
    /**
     * Turns on JavaScript stack sampling.
     */
    boolean enableSampling;
    /**
     * Turns on system tracing.
     */
    boolean enableSystrace;
    /**
     * Turns on argument filter.
     */
    boolean enableArgumentFilter;
    /**
     * Included category filters.
     * array[ string ]
     */
    List<String> includedCategories;
    /**
     * array[ string ]
     * Excluded category filters.
     */
    List<String> excludedCategories;
    /**
     * Configuration to synthesize the delays in tracing.
     * array[ string ]
     */
    List<String> syntheticDelays;
    /**
     * Configuration for memory dump triggers. Used only when "memory-infra" category is enabled.
     */
    MemoryDumpConfig memoryDumpConfig;
  }

  /**
   * Backend type to use for tracing. chrome uses the Chrome-integrated tracing service and is supported on all platforms. system is only supported on Chrome OS and uses the Perfetto system tracing service. auto chooses system when the perfettoConfig provided to Tracing.start specifies at least one non-Chrome data source; otherwise uses chrome.
   * Allowed Values: auto, chrome, system
   */
  public enum TracingBackend {
    auto, chrome, system
  }

}
