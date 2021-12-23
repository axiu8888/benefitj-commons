package com.benefitj.core;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class StackUtils {

  public static StackTraceElement[] getStackTraceElements() {
    return Thread.currentThread().getStackTrace();
  }

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
  public static String getTag(String append) {
    return getTag(get(3), append);
  }

  /**
   * 获取文件名
   */
  public static String getFileName() {
    return get(3).getFileName();
  }

  /**
   * 获取标签
   */
  public static String getTag(StackTraceElement stack, String append) {
    String className = getSimpleClassName(stack);
    return String.format("[(%s) - %s.%s(%d)]%s"
        , Thread.currentThread().getName()
        , className
        , stack.getMethodName()
        , stack.getLineNumber()
        , append
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

  /**
   * 过滤符合的报名
   *
   * @param packageName 报名
   * @param consumer    处理
   */
  public static void filterPackage(String packageName, Consumer<StackTraceElement> consumer) {
    filter(getStackTraceElements(), ste -> ste.getClassName().startsWith(packageName), consumer);
  }

  /**
   * 过滤符合的报名
   *
   * @param elements    栈追踪元素
   * @param packageName 报名
   * @param consumer    处理
   */
  public static void filterPackage(StackTraceElement[] elements,
                                   String packageName,
                                   Consumer<StackTraceElement> consumer) {
    filter(elements, ste -> ste.getClassName().startsWith(packageName), consumer);
  }

  /**
   * 过滤符合的类名
   *
   * @param type     类
   * @param consumer 处理
   */
  public static void filterClass(Class<?> type, Consumer<StackTraceElement> consumer) {
    filterClass(getStackTraceElements(), type, consumer);
  }

  /**
   * 过滤符合的类名
   *
   * @param elements 栈追踪元素
   * @param type     类
   * @param consumer 处理
   */
  public static void filterClass(StackTraceElement[] elements,
                                 Class<?> type,
                                 Consumer<StackTraceElement> consumer) {
    filter(elements, ste -> ste.getClassName().equals(type.getName()), consumer);
  }

  /**
   * 过滤
   *
   * @param elements 栈追踪元素
   * @param matcher  匹配
   * @param consumer 处理
   */
  public static void filter(StackTraceElement[] elements,
                            Predicate<StackTraceElement> matcher,
                            Consumer<StackTraceElement> consumer) {
    for (StackTraceElement element : elements) {
      if (matcher.test(element)) {
        consumer.accept(element);
      }
    }
  }

}
