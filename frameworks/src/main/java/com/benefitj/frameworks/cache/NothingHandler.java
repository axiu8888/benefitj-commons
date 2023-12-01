package com.benefitj.frameworks.cache;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class NothingHandler implements Handler {
  @Override
  public Object process(Method method, Object[] args, MethodProxy proxy) {
    return null;
  }
}
