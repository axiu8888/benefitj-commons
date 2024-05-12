package com.benefitj.vertx.mqtt.server;


import io.vertx.mqtt.MqttServerOptions;

/**
 * MQTT server 配置
 */
public class MqttServerProperty {

  /**
   * 主机地址，默认 0.0.0.0
   */
  private String host = "0.0.0.0";
  /**
   * 端口，非SSL时为1883，否则为8883
   */
  private int port = 1883;
  /**
   * 是否启用 WebSocket
   */
  private boolean useWebSocket = false;
  /**
   * WebSocket 缓冲区大小
   */
  private int wsMaxFrameSize = MqttServerOptions.DEFAULT_WEB_SOCKET_MAX_FRAME_SIZE;
  /**
   * 最大的消息体长度
   */
  private int maxMessageSize = MqttServerOptions.DEFAULT_MAX_MESSAGE_SIZE;
  /**
   * 是否驱逐旧的Session
   */
  private boolean dislodgeSession = true;
  /**
   * 是否启用SSL
   */
  private boolean ssl = false;
  /**
   * 密钥
   */
  private String keyPath;
  /**
   * 证书
   */
  private String certPath;

  public String getHost() {
    return host;
  }

  public MqttServerProperty setHost(String host) {
    this.host = host;
    return this;
  }

  public int getPort() {
    return port;
  }

  public MqttServerProperty setPort(int port) {
    this.port = port;
    return this;
  }

  public boolean isUseWebSocket() {
    return useWebSocket;
  }

  public MqttServerProperty setUseWebSocket(boolean useWebSocket) {
    this.useWebSocket = useWebSocket;
    return this;
  }

  public int getWsMaxFrameSize() {
    return wsMaxFrameSize;
  }

  public void setWsMaxFrameSize(int wsMaxFrameSize) {
    this.wsMaxFrameSize = wsMaxFrameSize;
  }

  public int getMaxMessageSize() {
    return maxMessageSize;
  }

  public MqttServerProperty setMaxMessageSize(int maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
    return this;
  }

  public boolean isDislodgeSession() {
    return dislodgeSession;
  }

  public MqttServerProperty setDislodgeSession(boolean dislodgeSession) {
    this.dislodgeSession = dislodgeSession;
    return this;
  }

  public boolean isSsl() {
    return ssl;
  }

  public MqttServerProperty setSsl(boolean ssl) {
    this.ssl = ssl;
    return this;
  }

  public String getKeyPath() {
    return keyPath;
  }

  public MqttServerProperty setKeyPath(String keyPath) {
    this.keyPath = keyPath;
    return this;
  }

  public String getCertPath() {
    return certPath;
  }

  public MqttServerProperty setCertPath(String certPath) {
    this.certPath = certPath;
    return this;
  }
}
