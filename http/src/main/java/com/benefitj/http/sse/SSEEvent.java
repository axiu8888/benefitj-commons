package com.benefitj.http.sse;

import com.benefitj.core.JsonUtils;

public class SSEEvent {

  private String id;
  private String event;
  private String data;
  private boolean keepAlive;
  private String other;
  private Long retry;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public boolean isKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(boolean keepAlive) {
    this.keepAlive = keepAlive;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public Long getRetry() {
    return retry;
  }

  public void setRetry(Long retry) {
    this.retry = retry;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
