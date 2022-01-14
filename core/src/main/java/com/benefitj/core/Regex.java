package com.benefitj.core;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 */
public class Regex {

  private final Pattern pattern;

  public Regex(String regex) {
    this(Pattern.compile(regex));
  }

  public Regex(Pattern pattern) {
    this.pattern = pattern;
  }

  public Pattern getPattern() {
    return pattern;
  }

  public List<String> find(String str) {
    final List<String> find = new LinkedList<>();
    synchronized (this) {
      Matcher matcher = getPattern().matcher(str);
      while (matcher.find()) {
        find.add(matcher.group());
      }
    }
    return find;
  }

  /**
   * 差值表达式
   */
  public static final String INTERPOLATION_EXPRESSION = "\\$\\{(.*?)}";
  /**
   * 日期格式：yyyy-MM-dd
   */
  public static final String DATE = "^\\d{4}-\\d{1,2}-\\d{1,2}";
  /**
   * IPv4
   */
  public static final String IPV4 = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
  /**
   * 密码，必须包含大小写字母和数字的组合
   *
   * @param min    最小长度
   * @param max    最大长度
   * @param strong 是否可以使用特殊字符
   * @return 返回正则表达式
   */
  public static String password(int min, int max, boolean strong) {
    return strong
        // 可以使用特殊字符
        ? "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{" + min + "," + max + "}$"
        // 不能使用特殊字符
        : "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{" + min + "," + max + "}$";
  }

}
