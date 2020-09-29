package com.benefitj.mqtt.packet;

/**
 * 控制报文类型
 */
public enum MqttMessageType {

  /*
   * Reserved 0 禁止 保留
   */
  // ~

  /**
   * CONNECT 1 客户端到服务端 客户端请求连接服务端
   */
  CONNECT(1, Direction.CLIENT, "Client request to connect to Server"),

  /**
   * CONNACK 2 服务端到客户端 连接报文确认
   */
  CONNACK(2, Direction.CLIENT, "Connect acknowledgment"),

  /**
   * PUBLISH 3 两个方向都允许 发布消息
   */
  PUBLISH(3, Direction.BOTH, "Publish message"),

  /**
   * PUBACK 4 两个方向都允许 QoS 1消息发布收到确认
   */
  PUBACK(4, Direction.BOTH, "Publish acknowledgment"),

  /**
   * PUBREC 5 两个方向都允许 发布收到（ 保证交付第一步）
   */
  PUBREC(5, Direction.BOTH, "Publish release (assured delivery part 1)"),

  /**
   * PUBREL 6 两个方向都允许 发布释放（ 保证交付第二步）
   */
  PUBREL(6, Direction.BOTH, "Publish release (assured delivery part 2)"),

  /**
   * PUBCOMP 7 两个方向都允许 QoS 2消息发布完成（ 保证交互第三步）
   */
  PUBCOMP(7, Direction.BOTH, "Publish complete (assured delivery part 3)"),

  /**
   * SUBSCRIBE 8 客户端到服务端 客户端订阅请求
   */
  SUBSCRIBE(8, Direction.CLIENT, "Client subscribe request"),

  /**
   * SUBACK 9 服务端到客户端 订阅请求报文确认
   */
  SUBACK(9, Direction.SERVER, "Subscribe acknowledgment"),

  /**
   * UNSUBSCRIBE 10 客户端到服务端 客户端取消订阅请求
   */
  UNSUBSCRIBE(10, Direction.CLIENT, "Unsubscribe request"),

  /**
   * UNSUBACK 11 服务端到客户端 取消订阅报文确认
   */
  UNSUBACK(11, Direction.SERVER, "Unsubscribe acknowledgment"),

  /**
   * PINGREQ 12 客户端到服务端 心跳请求
   */
  PINGREQ(12, Direction.CLIENT, "PING request"),

  /**
   * PINGRESP 13 服务端到客户端 心跳响应
   */
  PINGRESP(13, Direction.SERVER, "PING response"),

  /**
   * DISCONNECT 14 客户端到服务端 客户端断开连接
   */
  DISCONNECT(14, Direction.CLIENT, "Client is disconnecting"),

  /*
   * Reserved 15 禁止 保留
   */
  RESERVED(15, Direction.RESERVED, "Forbidden")
  ;

  private final int value;
  private final Direction direction;
  private final String description;


  MqttMessageType(int value, Direction direction, String description) {
    this.value = value;
    this.direction = direction;
    this.description = description;
  }

  public int getValue() {
    return value;
  }

  public Direction getDirection() {
    return direction;
  }

  public String getDescription() {
    return description;
  }

}
