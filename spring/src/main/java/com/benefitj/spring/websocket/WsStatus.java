package com.benefitj.spring.websocket;

/**
 * WebSocket的状态
 */
public enum WsStatus {
  /**
   * 打开
   */
  OPEN,
  /**
   * 关闭
   */
  CLOSE;

  /**
   * 是否处于打开状态
   *
   * @param status 判断的状态
   * @return 如果处于打开状态，返回true，否则返回false
   */
  public static boolean isOpen(WsStatus status) {
    return status == OPEN;
  }

  /**
   * 是否处于关闭状态
   *
   * @param status 判断的状态
   * @return 如果处于关闭状态，返回true，否则返回false
   */
  public static boolean isClose(WsStatus status) {
    return status == CLOSE;
  }

  /**
   * 获取枚举值
   *
   * @param values 某枚举类型的全部值
   * @param name   枚举值对应的名称
   * @param <E>    枚举类型
   * @return 返回查找到的枚举值
   */
  public static <E extends Enum> E get(E[] values, String name) {
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
    return get(Packet.values(), name);
  }

  /**
   * 是否处于打开状态
   *
   * @return 如果处于打开状态，返回true，否则返回false
   */
  public boolean isOpen() {
    return isOpen(this);
  }

  /**
   * 是否处于关闭状态
   *
   * @return 如果处于关闭状态，返回true，否则返回false
   */
  public boolean isClose() {
    return isClose(this);
  }

  /**
   * 数据包的类型
   */
  public enum Packet {
    /**
     * 打开
     */
    OPEN,
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
