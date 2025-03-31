package com.benefitj.core;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰命名法转换工具
 */
public class CamelCaseUtils {


  /**
   * 驼峰转下划线
   */
  public static String camelToSnake(String str) {
    if (StringUtils.isBlank(str)) return str;
    // 使用正则表达式替换大写字母
    return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
  }

  /**
   * 小驼峰命名法
   */
  public static String snakeToCamel(String str) {
    if (StringUtils.isBlank(str)) return str;
    //return str.toLowerCase().replaceAll("_(\\w)", m -> m.group(1).toUpperCase());
    String[] parts = str.split("_");
    StringBuilder sb = new StringBuilder(parts[0].toLowerCase());
    for (int i = 1; i < parts.length; i++) {
      sb
          .append(parts[i].substring(0, 1).toUpperCase())
          .append(parts[i].substring(1).toLowerCase());
    }
    return sb.toString();
  }

  /**
   * 大驼峰命名法
   */
  public static String pascalCase(String str) {
    if (StringUtils.isBlank(str)) return str;
    String camelCase = snakeToCamel(str);
    return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
  }


  //--------------------------------------------------------------


  private static final char SEPARATOR = '_';
  private static final String PATTERN_CHAR = "[A-Z]+";
  private static final String PATTERN_UNDERLINE = "_+";

  private static final ConcurrentMap<String, Pattern> CACHE_PATTERN = new ConcurrentHashMap<>();

  static {
    // 匹配至少一个大写字母
    CACHE_PATTERN.putIfAbsent(PATTERN_CHAR, Pattern.compile(PATTERN_CHAR));
    // 匹配至少一个下划线
    CACHE_PATTERN.putIfAbsent(PATTERN_UNDERLINE, Pattern.compile(PATTERN_UNDERLINE));
  }

  /**
   * 匹配大写字符
   */
  public static synchronized Matcher matcherChar(String src) {
    return CACHE_PATTERN.get(PATTERN_CHAR).matcher(src);
  }

  /**
   * 匹配下划线
   */
  public static synchronized Matcher matcherUnderLine(String src) {
    return CACHE_PATTERN.get(PATTERN_UNDERLINE).matcher(src);
  }

  /**
   * 驼峰命名转下划线
   *
   * @param src 要转换的字符串
   * @return 驼峰命名转化成下划线
   */
  @Deprecated
  public static String camelToUnderLine(String src) {
    return camelToUnderLine(src, new StringBuilder(src.length()));
  }

  /**
   * 驼峰命名转下划线
   *
   * @param src 要转换的字符串
   * @return 驼峰命名转化成下划线
   */
  @Deprecated
  public static String camelToUnderLine(String src, StringBuilder builder) {
    Matcher matcher = matcherChar(src);
    if (!matcher.find()) {
      return src;
    }

    boolean upperCase = false;
    char c;
    boolean nextUpperCase;
    for (int i = 0; i < src.length(); i++) {
      c = src.charAt(i);
      nextUpperCase = true;
      if (i < (src.length() - 1)) {
        nextUpperCase = Character.isUpperCase(src.charAt(i + 1));
      }

      if (Character.isUpperCase(c)) {
        if ((!upperCase) || (!nextUpperCase)) {
          builder.append(SEPARATOR);
        }
        upperCase = true;
      } else {
        upperCase = false;
      }
      builder.append(Character.toLowerCase(c));
    }
    return builder.toString();
  }

  /**
   * 下划线转驼峰命名
   *
   * @param src     要转换的字符串
   * @param capWord 是否首字母大写
   * @return 将所有的下划线后的小写字母转化成大写，如果没有下划线，直接返回
   */
  @Deprecated
  public static String underLineToCamel(String src, boolean capWord) {
    return underLineToCamel(src, capWord, new StringBuilder(src.length()));
  }

  /**
   * 下划线转驼峰命名
   *
   * @param src     要转换的字符串
   * @param capWord 是否首字母大写
   * @param builder 拼接字符串
   * @return 将所有的下划线后的小写字母转化成大写，如果没有下划线，直接返回
   */
  @Deprecated
  public static String underLineToCamel(String src, boolean capWord, StringBuilder builder) {
    Matcher matcher = matcherUnderLine(src);
    if (!matcher.find()) {
      return src;
    }

    boolean upperCase = false;
    char ch;
    for (int i = 0; i < src.length(); i++) {
      ch = src.charAt(i);
      if (ch == SEPARATOR) {
        upperCase = true;
      } else if (upperCase) {
        builder.append(Character.toUpperCase(ch));
        upperCase = false;
      } else {
        builder.append(ch);
      }
    }

    if (capWord) {
      char capital = Character.toUpperCase(builder.charAt(0));
      return builder.replace(0, 1, String.valueOf(capital)).toString();
    }
    return builder.toString();
  }
}
