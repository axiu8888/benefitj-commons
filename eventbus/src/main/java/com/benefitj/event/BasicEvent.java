package com.benefitj.event;

/**
 * 事件
 *
 * @param <T>
 */
public class BasicEvent<T> implements Event {

  public static <T> BasicEvent<T> ofBasic(T msg) {
    return new BasicEvent<>(msg);
  }

  /**
   * 载荷
   */
  private T payload;

  public BasicEvent() {
  }

  public BasicEvent(T payload) {
    this.payload = payload;
  }

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }

}
