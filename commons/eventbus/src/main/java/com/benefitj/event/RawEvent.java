package com.benefitj.event;

/**
 * 把消息当事件发送出去
 */
public final class RawEvent<T> implements Event {

  public static <T> RawEvent<T> of(T msg) {
    return new RawEvent<>(msg);
  }

  private T payload;

  public RawEvent() {
  }

  public RawEvent(T payload) {
    this.payload = payload;
  }

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }
}