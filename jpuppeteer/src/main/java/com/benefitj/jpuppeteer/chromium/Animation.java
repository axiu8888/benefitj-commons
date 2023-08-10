package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * Animation Domain
 */
@ChromiumApi("Animation")
public interface Animation {

  /**
   * Disables animation domain notifications.
   */
  void disable();

  /**
   * Enables animation domain notifications.
   */
  void enable();

  /**
   * Returns the current time of the an animation.
   *
   * @param id Id of animation.
   * @return {
   * currentTime: number, Current time of the page.
   * }
   */
  JSONObject getCurrentTime(String id);

  /**
   * Gets the playback rate of the document timeline.
   *
   * @param playbackRate Playback rate for animations on page.
   */
  void getPlaybackRate(Integer playbackRate);

  /**
   * Releases a set of animations to no longer be manipulated.
   *
   * @param animations array[ string ]
   *                   List of animation ids to seek.
   */
  void releaseAnimations(List<String> animations);

  /**
   * Gets the remote object of the Animation.
   *
   * @param animationId Animation id.
   * @return {
   * remoteObject: Runtime.RemoteObject, Corresponding remote object.
   * }
   */
  JSONObject resolveAnimation(String animationId);

  /**
   * Seek a set of animations to a particular time within each animation.
   *
   * @param animations  array[ string ]
   *                    List of animation ids to seek.
   * @param currentTime number
   *                    Set the current time of each animation.
   */
  void seekAnimations(List<String> animations, Long currentTime);

  /**
   * Sets the paused state of a set of animations.
   *
   * @param animations array[ string ]
   *                   Animations to set the pause state of.
   * @param paused     boolean
   *                   Paused state to set to.
   */
  void setPaused(List<String> animations, Boolean paused);

  /**
   * Sets the playback rate of the document timeline.
   *
   * @param playbackRate number
   *                     Playback rate for animations on page
   */
  void setPlaybackRate(Integer playbackRate);

  /**
   * Sets the timing of an animation node.
   *
   * @param animationId string
   *                    Animation id.
   * @param duration    number
   *                    Duration of the animation.
   * @param delay       number
   *                    Delay of the animation.
   */
  void setTiming(String animationId, Long duration, Long delay);

  /**
   * 事件
   */
  @Event("Animation")
  public interface Events {

    /**
     * Event for when an animation has been cancelled.
     *
     * @param id Id of the animation that was cancelled.
     */
    @Event("animationCanceled")
    void animationCanceled(String id);

    /**
     * Event for each animation that has been created.
     *
     * @param id Id of the animation that was created.
     */
    @Event("animationCreated")
    void animationCreated(String id);

    /**
     * Event for animation that has been started.
     *
     * @param animation Animation that was started.
     */
    @Event("animationStarted")
    void animationStarted(Anim animation);

  }

  /**
   * Animation instance.
   */
  @Data
  public class Anim {
    /**
     * Animation's id.
     */
    String id;
    /**
     * Animation's name.
     */
    String name;
    /**
     * Animation's internal paused state.
     */
    boolean pausedState;
    /**
     * Animation's play state.
     */
    String playState;
    /**
     * Animation's playback rate.
     */
    Integer playbackRate;
    /**
     * Animation's start time.
     */
    Long startTime;
    /**
     * Animation's current time.
     */
    Long currentTime;
    /**
     * Animation type of Animation.
     * Allowed Values: CSSTransition, CSSAnimation, WebAnimation
     */
    String type;
    /**
     * Animation's source animation node.
     */
    AnimationEffect source;
    /**
     * A unique ID for Animation representing the sources that triggered this CSS animation/transition.
     */
    String cssId;
  }

  /**
   * AnimationEffect instance
   */
  @Data
  public class AnimationEffect {
    /**
     * AnimationEffect's delay.
     */
    Long delay;
    /**
     * AnimationEffect's end delay.
     */
    Long endDelay;
    /**
     * AnimationEffect's iteration start.
     */
    Long iterationStart;
    /**
     * AnimationEffect's iterations.
     */
    Long iterations;
    /**
     * AnimationEffect's iteration duration.
     */
    Long duration;
    /**
     * AnimationEffect's playback direction.
     */
    String direction;

    /**
     * AnimationEffect's fill mode.
     */
    String fill;
    /**
     * AnimationEffect's target node.
     */
    String backendNodeId;
    /**
     * AnimationEffect's keyframes.
     */
    KeyframesRule keyframesRule;
    /**
     * AnimationEffect's timing function.
     */
    String easing;
  }

  /**
   * Keyframes Rule
   */
  @Data
  public class KeyframesRule {
    /**
     * CSS keyframed animation's name.
     */
    String name;
    /**
     * List of animation keyframes.
     */
    List<KeyframeStyle> keyframes;
  }

  /**
   * Keyframe Style
   */
  @Data
  public class KeyframeStyle {
    /**
     * Keyframe's time offset.
     */
    String offset;
    /**
     * AnimationEffect's timing function.
     */
    String easing;
  }
}
