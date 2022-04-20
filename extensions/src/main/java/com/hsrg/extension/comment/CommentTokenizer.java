package com.hsrg.extension.comment;

import com.benefitj.core.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 注释解析器
 */
public class CommentTokenizer {

  /**
   * 获取Java代码
   *
   * @param chars 字符数组
   * @return 返回移除注释后的代码
   */
  public String getJavaCode(char[] chars) {
    return getCode(chars, parseJavaComments(chars));
  }

  /**
   * 获取JavaScript代码
   *
   * @param chars 字符数组
   * @return 返回移除注释后的代码
   */
  public String getJsCode(char[] chars) {
    return getCode(chars, parseJsComments(chars));
  }

  /**
   * 获取代码
   *
   * @param chars    字符数组
   * @param comments 注释
   * @return 返回移除注释后的代码
   */
  public String getCode(char[] chars, List<Comment> comments) {
    StringBuilder sb = new StringBuilder();
    int index = 0;
    for (Comment comment : comments) {
      sb.append(chars, index, comment.start - index);
      index = comment.end;
    }
    sb.append(chars, index, chars.length - index);
    return sb.toString();
  }

  /**
   * 解析Java注释
   *
   * @param chars 字符数组
   * @return 返回解析后的注释
   */
  public List<Comment> parseJavaComments(char[] chars) {
    return parse(chars, Quote.DOUBLE);
  }

  /**
   * 解析JavaScript注释
   *
   * @param chars 字符数组
   * @return 返回解析后的注释
   */
  public List<Comment> parseJsComments(char[] chars) {
    return parse(chars, Quote.values());
  }

  /**
   * 解析注释
   *
   * @param chars  字符
   * @param quotes 引号（单引号、双引号、）
   * @return 返回解析的注释片段
   */
  public List<Comment> parse(char[] chars, Quote... quotes) {
    final List<Comment> comments = new LinkedList<>();
    int index = 0;
    while (chars.length > index) {
      if (isQuote(chars[index], quotes)) {
        // 是字符串，查找字符串的结束标志
        index = findStringEndMark(chars, index + 1, Quote.get(chars[index]));
        continue;
      }
      if (chars[index] == '/') {
        // 往前查找，同一行中是否存在一个
        if (chars.length > index + 1) { // 说明【/】后面还有字符
          if (chars[index + 1] == '*') {
            // 【/*】 或 【/**】开头
            Comment comment = new Comment();
            comment.start = index;
            comment.type = matchChar(chars, '*', index + 2) ? CommentType.SLASH_STAR2 : CommentType.SLASH_STAR;
            comment.end = findStarEndMark(chars, index + 1); // 查找最近的一个注释结束符
            comment.content = splitComment(comment, chars);
            comments.add(comment);
            index = comment.end;
            continue;
          } else if (chars[index + 1] == '/') {
            // 以【//】开头，一整行都是注释范围
            Comment comment = new Comment();
            comment.start = index;
            comment.type = CommentType.DOUBLE_SLASH;
            comment.end = findNewLineIndex(chars, index + 2); // 查找换行符
            comment.content = splitComment(comment, chars);
            comments.add(comment);
            index = comment.end;
            continue;
          }
        }
      }
      index++;
    }
    return comments;
  }

  /**
   * 判断是否为引号
   *
   * @param ch     字符
   * @param quotes 引号
   * @return 返回判断结果
   */
  protected boolean isQuote(char ch, Quote... quotes) {
    for (Quote quote : quotes) {
      if (quote.symbol == ch) {
        return true;
      }
    }
    return false;
  }

  /**
   * 匹配字符
   *
   * @param chars 字符数组
   * @param ch    需要匹配的字符
   * @param index 匹配的位置
   * @return 返回是否相等
   */
  protected boolean matchChar(char[] chars, char ch, int index) {
    return index >= 0 && chars.length > index && chars[index] == ch;
  }

  /**
   * 查找字符串结束的标识符
   *
   * @param chars 字符数组
   * @param start 开始的位置
   * @param quote 引号的类型
   * @return 返回查找到的位置
   */
  protected int findStringEndMark(char[] chars, int start, Quote quote) {
    for (int i = start; i < chars.length; i++) {
      if (chars[i] == quote.symbol && !matchChar(chars, '\\', i - 1)) {
        return i + 1;
      }
    }
    return chars.length;
  }

  /**
   * 查找跨行注释的结束符号
   *
   * @param chars 字符数组
   * @param start 开始的位置
   * @return 返回查找到的位置
   */
  protected int findStarEndMark(char[] chars, int start) {
    for (int i = start; i < chars.length; i++) {
      if (chars[i] == '*' && matchChar(chars, '/', i + 1)) {
        return i + 2;
      }
    }
    return chars.length;
  }

  /**
   * 查找换行符的位置，返回的是结束符的最后一个字符的下一个符号位置
   *
   * @param chars 字符数组
   * @param start 开始的位置
   * @return 返回查找到的位置，如果未查找到，返回字符数组长度
   */
  protected int findNewLineIndex(char[] chars, int start) {
    for (int i = start; i < chars.length; i++) {
      if (chars[i] == '\r') {
        if (matchChar(chars, '\n', i + 1)) {
          return i + 2;
        }
        return i + 1;
      }
      if (chars[i] == '\n') {
        return i + 1;
      }
    }
    return chars.length;
  }

  /**
   * 分割出注释的内容
   *
   * @param comment 注释
   * @param chars   全部字符
   * @return 返回注释内容
   */
  protected String splitComment(Comment comment, char[] chars) {
    StringBuilder sb = new StringBuilder(comment.end - comment.start);
    for (int i = comment.start; i < comment.end; i++) {
      sb.append(chars[i]);
    }
    return sb.toString();
  }

  /**
   * 分割为行
   *
   * @param data 数据
   * @return 返回分割后的行
   */
  public static List<String> splitToLines(String data) {
    return splitToLines(data.getBytes());
  }

  /**
   * 分割为行
   *
   * @param data 数据
   * @return 返回分割后的行
   */
  public static List<String> splitToLines(byte[] data) {
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    InputStreamReader reader = new InputStreamReader(bais);
    return IOUtils.readLines(reader);
  }

  public static String trimCode(List<String> lines) {
    return lines.stream()
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining(" "));
  }

}
