package com.benefitj.jpuppeteer.chromium;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * LayerTree Domain
 */
@ChromiumApi("LayerTree")
public interface LayerTree {

  /**
   * Provides the reasons why the given layer was composited.
   *
   * @param layerId LayerId
   *                The id of the layer for which we want to get the reasons it was composited.
   * @return {
   * compositingReasons: array[ string ]  A list of strings specifying reasons for the given layer to become composited.
   * compositingReasonIds: array[ string ]  A list of strings specifying reason IDs for the given layer to become composited.
   * }
   */
  JSONObject compositingReasons(String layerId);

  /**
   * Disables compositing tree inspection.
   */
  void disable();

  /**
   * Enables compositing tree inspection.
   */
  void enable();

  /**
   * Returns the snapshot identifier.
   *
   * @param tiles array[ PictureTile ]
   *              An array of tiles composing the snapshot.
   * @return {
   * snapshotId: SnapshotId  The id of the snapshot.
   * }
   */
  JSONObject loadSnapshot(List<PictureTile> tiles);

  /**
   * Returns the layer snapshot identifier.
   *
   * @param layerId LayerId
   *                The id of the layer.
   * @return {
   * snapshotId: SnapshotId  The id of the layer snapshot.
   * }
   */
  JSONObject makeSnapshot(String layerId);

  /**
   * @param snapshotId     SnapshotId
   *                       The id of the layer snapshot.
   * @param minRepeatCount integer
   *                       The maximum number of times to replay the snapshot (1, if not specified).
   * @param minDuration    number
   *                       The minimum duration (in seconds) to replay the snapshot.
   * @param clipRect       DOM.Rect
   *                       The clip rectangle to apply when replaying the snapshot.
   * @return {
   * timings: array[ PaintProfile ]  The array of paint profiles, one per run.
   * }
   */
  JSONObject profileSnapshot(String snapshotId, Integer minRepeatCount, Long minDuration, DOM.Rect clipRect);

  /**
   * Releases layer snapshot captured by the back-end.
   *
   * @param snapshotId SnapshotId
   *                   The id of the layer snapshot.
   */
  void releaseSnapshot(String snapshotId);

  /**
   * @param snapshotId SnapshotId
   *                   The id of the layer snapshot.
   * @param fromStep   integer
   *                   The first step to replay from (replay from the very start if not specified).
   * @param toStep     integer
   *                   The last step to replay to (replay till the end if not specified).
   * @param scale      number
   *                   The scale to apply while replaying (defaults to 1).
   * @return {
   * dataURL: string  A data: URL for resulting image.
   * }
   */
  JSONObject replaySnapshot(String snapshotId, Integer fromStep, Integer toStep, Double scale);

  /**
   * Replays the layer snapshot and returns canvas log.
   *
   * @param snapshotId SnapshotId
   *                   The id of the layer snapshot.
   * @return {
   * commandLog: array[ object ]  The array of canvas function calls.
   * }
   */
  JSONObject snapshotCommandLog(String snapshotId);


  @Event("LayerTree")
  public interface Events {

    /**
     * @param layerId LayerId
     *                The id of the painted layer.
     * @param clip    DOM.Rect
     *                Clip rectangle.
     */
    @Event("layerPainted")
    void layerPainted(String layerId, DOM.Rect clip);

    /**
     * @param layers array[ Layer ]
     *               Layer tree, absent if not in the comspositing mode.
     */
    @Event("layerTreeDidChange")
    void layerTreeDidChange(List<Layer> layers);

  }

  /**
   * Information about a compositing layer.
   */
  @Data
  public class Layer {
    /**
     * The unique id for this layer.
     * LayerId
     */
    String layerId;
    /**
     * The id of parent (not present for root).
     * LayerId
     */
    String parentLayerId;
    /**
     * The backend id for the node associated with this layer.
     * DOM.BackendNodeId
     */
    String backendNodeId;
    /**
     * Offset from parent layer, X coordinate.
     */
    Integer offsetX;
    /**
     * Offset from parent layer, Y coordinate.
     */
    Integer offsetY;
    /**
     * Layer width.
     */
    Integer width;
    /**
     * Layer height.
     */
    Integer height;
    /**
     * Transformation matrix for layer, default is identity matrix
     * array[ number ]
     */
    List<Number> transform;
    /**
     * Transform anchor point X, absent if no transform specified
     * number
     */
    Integer anchorX;
    /**
     * Transform anchor point Y, absent if no transform specified
     * number
     */
    Integer anchorY;
    /**
     * Transform anchor point Z, absent if no transform specified
     * number
     */
    Integer anchorZ;
    /**
     * Indicates how many time this layer has painted.
     */
    Integer paintCount;
    /**
     * Indicates whether this layer hosts any content, rather than being used for transform/scrolling purposes only.
     */
    boolean drawsContent;
    /**
     * Set if layer is not visible.
     */
    boolean invisible;
    /**
     * Rectangles scrolling on main thread only.
     * array[ ScrollRect ]
     */
    List<ScrollRect> scrollRects;
    /**
     * Sticky position constraint information
     */
    StickyPositionConstraint stickyPositionConstraint;
  }

  /**
   * Array of timings, one per paint step.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class PaintProfile extends JSONArray {
  }

  /**
   * Serialized fragment of layer picture along with its offset within the layer.
   */
  @Data
  public class PictureTile {
    /**
     * Offset from owning layer left boundary
     */
    Integer x;
    /**
     * Offset from owning layer top boundary
     */
    Integer y;
    /**
     * Base64-encoded snapshot data. (Encoded as a base64 string when passed over JSON)
     */
    String picture;
  }

  /**
   * Rectangle where scrolling happens on the main thread.
   */
  @Data
  public class ScrollRect {
    /**
     * Rectangle itself.
     */
    DOM.Rect rect;
    /**
     * Reason for rectangle to force scrolling on the main thread
     * Allowed Values: RepaintsOnScroll, TouchEventHandler, WheelEventHandler
     */
    String type;
  }

  /**
   * Sticky position constraints.
   */
  @Data
  public class StickyPositionConstraint {
    /**
     * Layout rectangle of the sticky element before being shifted
     */
    DOM.Rect stickyBoxRect;
    /**
     * Layout rectangle of the containing block of the sticky element
     */
    DOM.Rect containingBlockRect;
    /**
     * The nearest sticky layer that shifts the sticky box
     * LayerId
     */
    String nearestLayerShiftingStickyBox;
    /**
     * The nearest sticky layer that shifts the containing block
     * LayerId
     */
    String nearestLayerShiftingContainingBlock;
  }

}
