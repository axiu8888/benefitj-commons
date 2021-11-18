package com.benefitj.core.executable;

import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 参数查找器
 */
public class ArgsFinder {

  protected static final Function<Class<?>, ArgMatcher> CREATOR = ArgMatcher::new;
  protected static final Object[] EMPTY_ARGS = {};

  private final Executable executable;

  private Map<Class<?>, ArgMatcher> matchers = new HashMap<>();

  public ArgsFinder(Executable executable) {
    this.executable = executable;
  }

  /**
   * 查找匹配的参数值
   *
   * @param provideArgs 可选的参数值
   * @return 返回查找到的参数值
   */
  public Object[] find(Object[] provideArgs) {
    if (executable.getParameterCount() == 0) {
      return EMPTY_ARGS;
    }
    Class<?>[] types = executable.getParameterTypes();
    Object[] args = new Object[types.length];
    boolean anyone = false;
    synchronized (executable) {
      for (int i = 0; i < types.length; i++) {
        final Class<?> type = types[i];
        ArgMatcher matcher = getMatchers().computeIfAbsent(type, CREATOR);
        args[i] = findArg(provideArgs, matcher.getIndex(), matcher);
        anyone |= args[i] != null;
      }
      getMatchers().forEach((type, matcher) -> matcher.setIndex(0));
    }
    return anyone ? args : null;
  }

  /**
   * 查找单个参数的值
   *
   * @param provideArgs 可选参数
   * @param start       开始的下标
   * @param matcher     匹配器
   * @return 返回查找的参数
   */
  public Object findArg(Object[] provideArgs, int start, ArgMatcher matcher) {
    if (start <= provideArgs.length) {
      for (int i = start; i < provideArgs.length; i++) {
        if (matcher.match(provideArgs[i], i)) {
          return provideArgs[i];
        }
      }
    }
    return null;
  }

  public Executable getExecutable() {
    return executable;
  }

  public Map<Class<?>, ArgMatcher> getMatchers() {
    return matchers;
  }

  public static class ArgMatcher {
    /**
     * 类型
     */
    private Class<?> type;
    /**
     * 索引
     */
    private int index = 0;

    public ArgMatcher(Class<?> type) {
      this.type = type;
    }

    public boolean match(Object value, int position) {
      if (getType().isInstance(value) && getIndex() <= position) {
        setIndex(position + 1);
        return true;
      }
      return false;
    }

    public Class<?> getType() {
      return type;
    }

    public void setType(Class<?> type) {
      this.type = type;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }
  }
}
