package com.benefitj.http;

import okhttp3.Response;
import okio.ByteString;

public interface WebSocketListener {

  /**
   * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
   * messages.
   */
  default void onOpen(WebSocket socket, Response response) {
  }

  /**
   * Invoked when a text (type `0x1`) message has been received.
   */
  default void onMessage(WebSocket socket, String text) {
  }

  /**
   * Invoked when a binary (type `0x2`) message has been received.
   */
  default void onMessage(WebSocket socket, ByteString bytes) {
  }

  /**
   * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
   */
  default void onClosing(WebSocket socket, int code, String reason) {
  }

  /**
   * Invoked when both peers have indicated that no more messages will be transmitted and the
   * connection has been successfully released. No further calls to this listener will be made.
   */
  default void onClosed(WebSocket socket, int code, String reason) {
  }

  /**
   * Invoked when a web socket has been closed due to an error reading from or writing to the
   * network. Both outgoing and incoming messages may have been lost. No further calls to this
   * listener will be made.
   */
  default void onFailure(WebSocket socket, Throwable error, Response response) {
  }

}
