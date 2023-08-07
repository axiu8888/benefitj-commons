package com.benefitj.jpuppeteer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * message that send to browser
 */
public class Message {

  public static final AtomicLong last_id = new AtomicLong(1);

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private CountDownLatch latch;

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private JSONObject rawResponse;

  private long id = last_id.getAndIncrement();
  private String method;
  private Map<String, Object> params = new LinkedHashMap<>();
  private String sessionId;
  /**
   * 本次发送消息返回的结果
   */
  private JSONObject result;
  /**
   * 错误信息
   */
  private Error error;

  public Message() {
  }

  public CountDownLatch getLatch() {
    return latch;
  }

  public void setLatch(CountDownLatch latch) {
    this.latch = latch;
  }

  public JSONObject getRawResponse() {
    return rawResponse;
  }

  public void setRawResponse(JSONObject rawResponse) {
    this.rawResponse = rawResponse;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public JSONObject getResult() {
    return result;
  }

  public void setResult(JSONObject result) {
    this.result = result;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

  public static class Error {

    private Integer code;
    private String message;
    private String data;

    public Integer getCode() {
      return code;
    }

    public void setCode(Integer code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }
  }

}
