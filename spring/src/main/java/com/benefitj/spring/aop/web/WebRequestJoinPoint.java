package com.benefitj.spring.aop.web;

import com.benefitj.spring.aop.AopJoinPoint;
import org.aspectj.lang.JoinPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebRequestJoinPoint extends AopJoinPoint {
  /**
   * 请求
   */
  private HttpServletRequest request;
  /**
   * 响应
   */
  private HttpServletResponse response;

  public WebRequestJoinPoint(JoinPoint original) {
    super(original);
  }

  public WebRequestJoinPoint(JoinPoint original, HttpServletRequest request, HttpServletResponse response) {
    super(original);
    this.request = request;
    this.response = response;
  }


  public HttpServletRequest getRequest() {
    return request;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

}
