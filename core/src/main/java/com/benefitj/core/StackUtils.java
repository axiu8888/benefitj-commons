package com.benefitj.core;

public class StackUtils {

  /**
   * 获取指定的栈追踪元素
   *
   * @param index 下标
   * @return 返回对应的栈追踪元素对象
   */
  public static StackTraceElement get(int index) {
    return Thread.currentThread().getStackTrace()[index];
  }

  /**
   * 获取当前的栈追踪元素
   */
  public static StackTraceElement currentStack() {
    return get(3);
  }

  /**
   * 获取标签
   */
  public static String getTag() {
    StackTraceElement stack = get(3);
    String className = getSimpleClassName(stack);
    return String.format("[(%s) - %s.%s(%d)]"
        , Thread.currentThread().getName()
        , className
        , stack.getMethodName()
        , stack.getLineNumber()
    );
  }

  /**
   * 获取类名
   *
   * @param stack 栈追踪元素对象
   * @return 返回获取的类名
   */
  public static String getSimpleClassName(StackTraceElement stack) {
    return getSimpleClassName(stack.getClassName());
  }

  /**
   * 获取类名
   *
   * @param className 全类名
   * @return 返回获取的类名
   */
  public static String getSimpleClassName(String className) {
    return className.substring(className.lastIndexOf(".") + 1);
  }

}
