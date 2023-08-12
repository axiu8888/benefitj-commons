package com.benefitj.jpuppeteer.chromium;


import cn.hutool.json.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * HeapProfiler Domain
 */
@ChromiumApi("HeapProfiler")
public interface HeapProfiler {

  /**
   * Enables console to refer to the node with given id via $x (see Command Line API for more details $x functions).
   *
   * @param heapObjectId HeapSnapshotObjectId
   *                     Heap snapshot object id to be accessible by means of $x command line API.
   */
  void addInspectedHeapObject(String heapObjectId);

  void collectGarbage();

  void disable();

  void enable();

  /**
   * @param objectId Runtime.RemoteObjectId
   *                 Identifier of the object to get heap object id for.
   * @return {
   * heapSnapshotObjectId: HeapSnapshotObjectId Id of the heap snapshot object corresponding to the passed remote object id.
   * }
   */
  JSONObject getHeapObjectId(String objectId);

  /**
   * @param objectId    HeapSnapshotObjectId
   * @param objectGroup string
   *                    Symbolic group name that can be used to release multiple objects.
   * @return {
   * result: Runtime.RemoteObject  Evaluation result.
   * }
   */
  JSONObject getObjectByHeapObjectId(String objectId, String objectGroup);

  /**
   * @return {
   * profile: SamplingHeapProfile  Return the sampling profile being collected.
   * }
   */
  JSONObject getSamplingProfile();

  /**
   * @param samplingInterval                 number
   *                                         Average sample interval in bytes. Poisson distribution is used for the intervals.
   *                                         The default value is 32768 bytes.
   * @param includeObjectsCollectedByMajorGC boolean
   *                                         By default, the sampling heap profiler reports only objects which are still
   *                                         alive when the profile is returned via getSamplingProfile or stopSampling,
   *                                         which is useful for determining what functions contribute the most to steady-state
   *                                         memory usage. This flag instructs the sampling heap profiler to also include
   *                                         information about objects discarded by major GC, which will show which functions
   *                                         cause large temporary memory usage or long GC pauses.
   * @param includeObjectsCollectedByMinorGC boolean
   *                                         By default, the sampling heap profiler reports only objects which are still
   *                                         alive when the profile is returned via getSamplingProfile or stopSampling,
   *                                         which is useful for determining what functions contribute the most to steady-state
   *                                         memory usage. This flag instructs the sampling heap profiler to also include
   *                                         information about objects discarded by minor GC, which is useful when tuning
   *                                         a latency-sensitive application for minimal GC activity.
   */
  void startSampling(Long samplingInterval, Boolean includeObjectsCollectedByMajorGC, Boolean includeObjectsCollectedByMinorGC);

  /**
   * @param trackAllocations boolean
   */
  void startTrackingHeapObjects(Boolean trackAllocations);

  /**
   * @param profile SamplingHeapProfile
   *                Recorded sampling heap profile.
   */
  void stopSampling(SamplingHeapProfile profile);

  /**
   * @param reportProgress            boolean
   *                                  If true 'reportHeapSnapshotProgress' events will be generated while snapshot is being taken when the tracking is stopped.
   * @param treatGlobalObjectsAsRoots boolean
   *                                  Deprecated in favor of exposeInternals. DEPRECATED
   * @param captureNumericValue       boolean
   *                                  If true, numerical values are included in the snapshot
   * @param exposeInternals           boolean
   *                                  If true, exposes internals of the snapshot. EXPERIMENTAL
   */
  void stopTrackingHeapObjects(Boolean reportProgress, Boolean treatGlobalObjectsAsRoots, Boolean captureNumericValue, Boolean exposeInternals);

  /**
   * @param reportProgress            boolean
   *                                  If true 'reportHeapSnapshotProgress' events will be generated while snapshot is being taken.
   * @param treatGlobalObjectsAsRoots boolean
   *                                  If true, a raw snapshot without artificial roots will be generated. Deprecated in favor of exposeInternals. DEPRECATED
   * @param captureNumericValue       boolean
   *                                  If true, numerical values are included in the snapshot
   * @param exposeInternals           boolean
   *                                  If true, exposes internals of the snapshot. EXPERIMENTAL
   */
  void takeHeapSnapshot(Boolean reportProgress, Boolean treatGlobalObjectsAsRoots, Boolean captureNumericValue, Boolean exposeInternals);


  /**
   * 事件
   */
  @Event("HeapProfiler")
  public interface Events {

    /**
     * @param chunk string
     */
    @Event("addHeapSnapshotChunk")
    void addHeapSnapshotChunk(String chunk);

    /**
     * If heap objects tracking has been started then backend may send update for one or more fragments
     *
     * @param statsUpdate array[ integer ]
     *                    An array of triplets. Each triplet describes a fragment. The first integer is the fragment index, the second integer is a total count of objects for the fragment, the third integer is a total size of the objects for the fragment.
     */
    @Event("heapStatsUpdate")
    void heapStatsUpdate(List<Integer> statsUpdate);

    /**
     * If heap objects tracking has been started then backend regularly sends a current value for last seen object id and
     * corresponding timestamp. If the were changes in the heap since last event then one or more heapStatsUpdate events
     * will be sent before a new lastSeenObjectId event.
     *
     * @param lastSeenObjectId integer
     * @param timestamp        number
     */
    @Event("lastSeenObjectId")
    void lastSeenObjectId(Integer lastSeenObjectId, Long timestamp);

    /**
     * @param done     integer
     * @param total    integer
     * @param finished boolean
     */
    @Event("reportHeapSnapshotProgress")
    void reportHeapSnapshotProgress(Integer done, Integer total, Boolean finished);

    @Event("resetProfiles")
    void resetProfiles();

  }

  /**
   * Sampling profile.
   */
  @Data
  public class SamplingHeapProfile {
    SamplingHeapProfileNode head;
    List<SamplingHeapProfileSample> samples;
  }

  /**
   * Sampling Heap Profile node. Holds callsite information, allocation statistics and child nodes.
   */
  @Data
  public class SamplingHeapProfileNode {
    /**
     * Function location.
     */
    Runtime.CallFrame callFrame;
    /**
     * Allocations size in bytes for the node excluding children.
     */
    Long selfSize;
    /**
     * Node id. Ids are unique across all profiles collected between startSampling and stopSampling.
     */
    Integer id;
    /**
     * Child nodes.
     * array[ SamplingHeapProfileNode ]
     */
    List<SamplingHeapProfileNode> children;
  }

  /**
   * A single sample from a sampling profile.
   */
  @Data
  public class SamplingHeapProfileSample {
    /**
     * Allocation size in bytes attributed to the sample.
     */
    Long size;
    /**
     * Id of the corresponding profile tree node.
     */
    Integer nodeId;
    /**
     * Time-ordered sample ordinal number. It is unique across all profiles retrieved between startSampling and stopSampling.
     */
    Integer ordinal;
  }

}
