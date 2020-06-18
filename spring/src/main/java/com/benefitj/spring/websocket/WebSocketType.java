package com.benefitj.spring.websocket;

/**
 * WebSocket的类型
 */
public enum WebSocketType {

  /* ignore */;

  /**
   * 获取枚举值
   *
   * @param values 某枚举类型的全部值
   * @param name   枚举值对应的名称
   * @param <E>    枚举类型
   * @return 返回查找到的枚举值
   */
  public static <E extends Enum> E valueOf(E[] values, String name) {
    for (E value : values) {
      if (value.name().equalsIgnoreCase(name)) {
        return value;
      }
    }
    return null;
  }

  /**
   * 获取对应的 Packet 类型
   */
  public static Packet ofPacket(String name) {
    return valueOf(Packet.values(), name);
  }

  /**
   * 数据包的类型
   */
  public enum Packet {
    /**
     * 注册
     */
    REGISTER,
    /**
     * 心跳
     */
    HEARTBEAT,
    /**
     * 数据
     */
    DATA,
    /**
     * 关闭
     */
    CLOSE;
  }
}
