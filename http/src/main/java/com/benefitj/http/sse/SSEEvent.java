package com.benefitj.http.sse;


import com.alibaba.fastjson2.JSON;

public class SSEEvent {
  private String event;
  private String data;
  private String id;
  private long retry;

  // getters and setters
  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getRetry() {
    return retry;
  }

  public void setRetry(long retry) {
    this.retry = retry;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
