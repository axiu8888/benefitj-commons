package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * Memory Domain
 */
@ChromiumApi("Memory")
public interface Memory {

  /**
   * Simulate OomIntervention by purging V8 memory.
   */
  void forciblyPurgeJavaScriptMemory();

  /**
   * Retrieve native memory allocations profile collected since renderer process startup.
   *
   * @return {
   * profile: SamplingProfile
   * }
   */
  JSONObject getAllTimeSamplingProfile();

  /**
   * Retrieve native memory allocations profile collected since browser process startup.
   *
   * @return {
   * profile: SamplingProfile
   * }
   */
  JSONObject getBrowserSamplingProfile();

  /**
   * @return {
   * documents: integer
   * nodes: integer
   * jsEventListeners: integer
   * }
   */
  JSONObject getDOMCounters();

  /**
   * Retrieve native memory allocations profile collected since last startSampling call.
   *
   * @return {
   * profile: SamplingProfile
   * }
   */
  JSONObject getSamplingProfile();

  /**
   */
  void prepareForLeakDetection();

  /**
   * Enable/disable suppressing memory pressure notifications in all processes.
   *
   * @param suppressed boolean  If true, memory pressure notifications will be suppressed.
   */
  void setPressureNotificationsSuppressed(Boolean suppressed);

  /**
   * Simulate a memory pressure notification in all processes.
   *
   * @param level PressureLevel
   *              Memory pressure level of the notification.
   */
  void simulatePressureNotification(PressureLevel level);

  /**
   * Start collecting native memory profile.
   *
   * @param samplingInterval   integer
   *                           Average number of bytes between samples.
   * @param suppressRandomness boolean
   *                           Do not randomize intervals between samples.
   */
  void startSampling(Long samplingInterval, Boolean suppressRandomness);

  /**
   * Stop collecting native memory profile.
   */
  void stopSampling();

  /**
   * Executable module information
   */
  @Data
  public class Module {
    /**
     * Name of the module.
     */
    String name;
    /**
     * UUID of the module.
     */
    String uuid;
    /**
     * Base address where the module is loaded into memory. Encoded as a decimal or hexadecimal (0x prefixed) string.
     */
    String baseAddress;
    /**
     * Size of the module in bytes.
     */
    Long size;
  }

  /**
   * Memory pressure level.
   * Allowed Values: moderate, critical
   */
  public enum PressureLevel {
    moderate, critical
  }

  /**
   * Array of heap profile samples.
   */
  @Data
  public class SamplingProfile {
    /**
     * array[ SamplingProfileNode ]
     */
    List<SamplingProfileNode> samples;
    /**
     * array[ Module ]
     */
    List<Module> modules;
  }

  /**
   * Heap profile sample.
   */
  @Data
  public class SamplingProfileNode {
    /**
     * Size of the sampled allocation.
     */
    Long size;
    /**
     * Total bytes attributed to this sample.
     */
    Long total;
    /**
     * Execution stack at the point of allocation.
     * array[ string ]
     */
    List<String> stack;
  }

}
