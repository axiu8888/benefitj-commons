package com.benefitj.core;

import org.apache.commons.lang3.StringUtils;

/**
 * 占位符替换
 */
public class PlaceHolder {

  static final SingletonSupplier<PlaceHolder> single = SingletonSupplier.of(PlaceHolder::new);

  public static PlaceHolder get() {
    return single.get();
  }

  /**
   * 格式化
   *
   * @param template 模板
   * @param args     参数
   * @return 返回格式化的消息
   */
  public String format(String template, Object... args) {
    return format('{', '}', false, template, args);
  }

  /**
   * 格式化
   *
   * @param allowDirty 允许脏值
   * @param template   模板
   * @param args       参数
   * @return 返回格式化的消息
   */
  public String format(boolean allowDirty, String template, Object... args) {
    return format('{', '}', allowDirty, template, args);
  }

  /**
   * 格式化
   *
   * @param prefix     前缀
   * @param suffix     后缀
   * @param allowDirty 允许脏值
   * @param template   模板
   * @param args       参数
   * @return 返回格式化的消息
   */
  public String format(char prefix, char suffix, boolean allowDirty, String template, Object... args) {
    if (args == null || args.length == 0) return template;
    StringBuilder sb = new StringBuilder();
    int argIndex = 0;
    for (int i = 0; i < template.length(); i++) {
      char c = template.charAt(i);
      if (c == prefix) {
        int endIndex = template.indexOf(suffix, i);
        if (endIndex != -1) {
          String placeholder = template.substring(i + 1, endIndex);
          if (StringUtils.isNotBlank(placeholder) && !allowDirty) {
            // 存在脏值
            sb.append(prefix);
          } else {
            sb.append(args[argIndex]);
            argIndex++;
            i = endIndex;
          }
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
