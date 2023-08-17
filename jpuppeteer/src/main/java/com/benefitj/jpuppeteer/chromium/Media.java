package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * This domain allows detailed inspection of media elements
 */
@ChromiumApi("Media")
public interface Media {

  /**
   * Disables the Media domain.
   */
  void disable();

  /**
   * Enables the Media domain
   */
  void enable();

  @Event("Media")
  public interface Events {
    /**
     * Send a list of any errors that need to be delivered.
     *
     * @param playerId PlayerId
     * @param errors   array[ PlayerError ]
     */
    @Event("playerErrorsRaised")
    void playerErrorsRaised(String playerId, List<PlayerError> errors);

    /**
     * Send events as a list, allowing them to be batched on the browser for less congestion. If batched, events must ALWAYS be in chronological order.
     *
     * @param playerId PlayerId
     * @param events   array[ PlayerEvent ]
     */
    @Event("playerEventsAdded")
    void playerEventsAdded(String playerId, List<PlayerEvent> events);

    /**
     * Send a list of any messages that need to be delivered.
     *
     * @param playerId PlayerId
     * @param messages array[ PlayerMessage ]
     */
    @Event("playerMessagesLogged")
    void playerMessagesLogged(String playerId, List<PlayerMessage> messages);

    /**
     * This can be called multiple times, and can be used to set / override / remove player properties. A null propValue indicates removal.
     *
     * @param playerId   PlayerId
     * @param properties array[ PlayerProperty ]
     */
    @Event("playerPropertiesChanged")
    void playerPropertiesChanged(String playerId, List<PlayerProperty> properties);

    /**
     * Called whenever a player is created, or when a new agent joins and receives a list of active players.
     * If an agent is restored, it will receive the full list of player ids and all events again.
     *
     * @param players array[ PlayerId ]
     */
    @Event("playersCreated")
    void playersCreated(List<String> players);

  }

  /**
   * Corresponds to kMediaError
   */
  @Data
  public class PlayerError {
    /**
     *
     */
    String errorType;
    /**
     * Code is the numeric enum entry for a specific set of error codes, such as PipelineStatusCodes in media/base/pipeline_status.h
     */
    Integer code;
    /**
     * A trace of where this error was caused / where it passed through.
     * array[ PlayerErrorSourceLocation ]
     */
    List<PlayerErrorSourceLocation> stack;
    /**
     * Errors potentially have a root cause error, ie, a DecoderError might be caused by an WindowsError
     * array[ PlayerError ]
     */
    PlayerError cause;
    /**
     * Extra data attached to an error, such as an HRESULT, Video Codec, etc.
     */
    JSONObject data;
  }

  /**
   * Represents logged source line numbers reported in an error. NOTE: file and line are from chromium c++ implementation code, not js.
   */
  @Data
  public class PlayerErrorSourceLocation {
    String file;
    Integer line;
  }

  /**
   * Corresponds to kMediaEventTriggered
   */
  @Data
  public class PlayerEvent {
    Long timestamp;
    String value;
  }

  /**
   * Have one type per entry in MediaLogRecord::Type Corresponds to kMessage
   */
  @Data
  public class PlayerMessage {
    /**
     * Keep in sync with MediaLogMessageLevel We are currently keeping the message level 'error' separate from the PlayerError type because right now they represent different things, this one being a DVLOG(ERROR) style log message that gets printed based on what log level is selected in the UI, and the other is a representation of a media::PipelineStatus object. Soon however we're going to be moving away from using PipelineStatus for errors and introducing a new error type which should hopefully let us integrate the error log level into the PlayerError type.
     * Allowed Values: error, warning, info, debug
     */
    Level level;
    String message;
  }

  public enum Level {
    error, warning, info, debug
  }

  /**
   * Corresponds to kMediaPropertyChange
   */
  @Data
  public class PlayerProperty {
    String name;
    String value;
  }

}
