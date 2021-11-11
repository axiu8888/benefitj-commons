package com.benefitj.core;

public class StackUtils {


  public static StackTraceElement get(int index) {
    return Thread.currentThread().getStackTrace()[index];
  }

  public static StackTraceElement currentMethodStack() {
    return Thread.currentThread().getStackTrace()[2];
  }

  public static String getTag() {
    StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
    String className;
    try {
      className = Class.forName(stack.getClassName()).getSimpleName();
    } catch (ClassNotFoundException e) {
      className = stack.getClassName();
    }
    return String.format("thread[%s] %s.%s(%d)"
        , Thread.currentThread().getName()
        , className
        , stack.getMethodName()
        , stack.getLineNumber()
    );
  }

}
