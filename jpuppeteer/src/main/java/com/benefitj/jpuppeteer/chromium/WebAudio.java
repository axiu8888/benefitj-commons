package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import lombok.Data;

/**
 * This domain allows inspection of Web Audio API. https://webaudio.github.io/web-audio-api/ EXPERIMENTAL
 */
@ChromiumApi("WebAudio")
public interface WebAudio {

  /**
   * Disables the WebAudio domain.
   */
  void disable();

  /**
   * Enables the WebAudio domain and starts sending context lifetime events.
   */
  void enable();

  /**
   * Fetch the realtime data from the registered contexts.
   *
   * @param contextId GraphObjectId
   * @return {
   * realtimeData:  ContextRealtimeData
   * }
   */
  JSONObject getRealtimeData(String contextId);

  /**
   * 事件
   */
  @Event("WebAudio")
  public interface Events {

    /**
     * Notifies that the construction of an AudioListener has finished.
     *
     * @param listener AudioListener
     */
    @Event("audioListenerCreated")
    void audioListenerCreated(AudioListener listener);

    /**
     * Notifies that a new AudioListener has been created.
     *
     * @param contextId  GraphObjectId
     * @param listenerId GraphObjectId
     */
    @Event("audioListenerWillBeDestroyed")
    void audioListenerWillBeDestroyed(String contextId, String listenerId);

    /**
     * Notifies that a new AudioNode has been created.
     *
     * @param node AudioNode
     */
    @Event("audioNodeCreated")
    void audioNodeCreated(AudioNode node);

    /**
     * Notifies that an existing AudioNode has been destroyed.
     *
     * @param contextId GraphObjectId
     * @param nodeId    GraphObjectId
     */
    @Event("audioNodeWillBeDestroyed")
    void audioNodeWillBeDestroyed(String contextId, String nodeId);

    /**
     * Notifies that a new AudioParam has been created.
     *
     * @param param AudioParam
     */
    @Event("audioParamCreated")
    void audioParamCreated(AudioParam param);

    /**
     * Notifies that an existing AudioParam has been destroyed.
     *
     * @param contextId GraphObjectId
     * @param nodeId    GraphObjectId
     * @param paramId   GraphObjectId
     */
    @Event("audioParamWillBeDestroyed")
    void audioParamWillBeDestroyed(String contextId, String nodeId, String paramId);

    /**
     * Notifies that existing BaseAudioContext has changed some properties (id stays the same)..
     *
     * @param context BaseAudioContext
     */
    @Event("contextChanged")
    void contextChanged(BaseAudioContext context);

    /**
     * Notifies that a new BaseAudioContext has been created.
     *
     * @param context BaseAudioContext
     */
    @Event("contextCreated")
    void contextCreated(BaseAudioContext context);

    /**
     * Notifies that an existing BaseAudioContext will be destroyed.
     *
     * @param contextId GraphObjectId
     */
    @Event("contextWillBeDestroyed")
    void contextWillBeDestroyed(String contextId);

    /**
     * Notifies that an AudioNode is connected to an AudioParam.
     *
     * @param contextId         GraphObjectId
     * @param sourceId          GraphObjectId
     * @param destinationId     GraphObjectId
     * @param sourceOutputIndex number
     */
    @Event("nodeParamConnected")
    void nodeParamConnected(String contextId, String sourceId, String destinationId, Integer sourceOutputIndex);

    /**
     * Notifies that an AudioNode is disconnected to an AudioParam.
     *
     * @param contextId         GraphObjectId
     * @param sourceId          GraphObjectId
     * @param destinationId     GraphObjectId
     * @param sourceOutputIndex number
     */
    @Event("nodeParamDisconnected")
    void nodeParamDisconnected(String contextId, String sourceId, String destinationId, Integer sourceOutputIndex);

    /**
     * Notifies that two AudioNodes are connected.
     *
     * @param contextId             GraphObjectId
     * @param sourceId              GraphObjectId
     * @param destinationId         GraphObjectId
     * @param sourceOutputIndex     number
     * @param destinationInputIndex number
     */
    @Event("nodesConnected")
    void nodesConnected(String contextId, String sourceId, String destinationId, Integer sourceOutputIndex, Integer destinationInputIndex);

    /**
     * Notifies that AudioNodes are disconnected. The destination can be null, and it means all the outgoing connections from the source are disconnected.
     *
     * @param contextId             GraphObjectId
     * @param sourceId              GraphObjectId
     * @param destinationId         GraphObjectId
     * @param sourceOutputIndex     number
     * @param destinationInputIndex number
     */
    @Event("nodesDisconnected")
    void nodesDisconnected(String contextId, String sourceId, String destinationId, Integer sourceOutputIndex, Integer destinationInputIndex);

  }

  /**
   * Protocol object for AudioListener
   */
  @Data
  public class AudioListener {
    /**
     * GraphObjectId
     */
    String listenerId;
    /**
     * GraphObjectId
     */
    String contextId;
  }

  /**
   * Protocol object for AudioNode
   */
  @Data
  public class AudioNode {
    /**
     * GraphObjectId
     */
    String nodeId;
    /**
     * GraphObjectId
     */
    String contextId;
    NodeType nodeType;
    Integer numberOfInputs;
    Integer numberOfOutputs;
    Integer channelCount;
    ChannelCountMode channelCountMode;
    ChannelInterpretation channelInterpretation;
  }

  /**
   * Protocol object for AudioParam
   */
  @Data
  public class AudioParam {
    /**
     * GraphObjectId
     */
    String paramId;
    /**
     * GraphObjectId
     */
    String nodeId;
    /**
     * GraphObjectId
     */
    String contextId;
    String paramType;
    AutomationRate rate;
    Integer defaultValue;
    Integer minValue;
    Integer maxValue;
  }

  /**
   * Enum of AudioParam::AutomationRate from the spec
   * Allowed Values: a-rate, k-rate
   */
  public enum AutomationRate {
    a_rate, k_rate
  }

  /**
   * Protocol object for BaseAudioContext
   */
  @Data
  public class BaseAudioContext {
    /**
     * GraphObjectId
     */
    String contextId;
    ContextType contextType;
    ContextState contextState;
    ContextRealtimeData realtimeData;
    /**
     * Platform-dependent callback buffer size.
     */
    Integer callbackBufferSize;
    /**
     * Number of output channels supported by audio hardware in use.
     */
    Integer maxOutputChannelCount;
    /**
     * Context sample rate.
     */
    Integer sampleRate;
  }

  /**
   * Enum of AudioNode::ChannelCountMode from the spec
   * Allowed Values: clamped-max, explicit, max
   */
  public enum ChannelCountMode {
    clamped_max, explicit, max
  }

  /**
   * Enum of AudioNode::ChannelInterpretation from the spec
   * Allowed Values: discrete, speakers
   */
  public enum ChannelInterpretation {
    discrete, speakers
  }

  /**
   * Fields in AudioContext that change in real-time.
   */
  @Data
  public class ContextRealtimeData {
    /**
     * The current context time in second in BaseAudioContext.
     */
    Long currentTime;
    /**
     * The time spent on rendering graph divided by render quantum duration, and multiplied by 100. 100 means the audio renderer reached the full capacity and glitch may occur.
     */
    Integer renderCapacity;
    /**
     * A running mean of callback interval.
     */
    Long callbackIntervalMean;
    /**
     * A running variance of callback interval.
     */
    Long callbackIntervalVariance;
  }

  /**
   * Enum of AudioContextState from the spec
   * Allowed Values: suspended, running, closed
   */
  public enum ContextState {
    suspended, running, closed
  }

  /**
   * Enum of BaseAudioContext types
   * Allowed Values: realtime, offline
   */
  public enum ContextType {
    realtime, offline
  }

}
