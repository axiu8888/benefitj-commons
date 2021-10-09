package com.benefitj.core;

import java.util.LinkedList;
import java.util.List;

/**
 * 分割字符串的分割器
 */
public interface Slicer {

  /**
   * 匹配是否符合
   *
   * @param builder  字符串拼接
   * @param position 字符位置
   * @param ch       当前字符
   * @return 返回是否匹配
   */
  boolean test(StringBuilder builder, int position, char ch);

  /**
   * 换行
   *
   * @param cs     字符串
   * @param slicer 分割器
   * @return 返回分割后的字符串
   */
  static List<String> slice(CharSequence cs, Slicer slicer) {
    StringBuilder builder = new StringBuilder();
    List<String> lines = new LinkedList<>();
    for (int i = 0; i < cs.length(); i++) {
      boolean test = slicer.test(builder, i, cs.charAt(i));
      if (test) {
        lines.add(builder.toString());
        builder.setLength(0);
        continue;
      }
      builder.append(cs.charAt(i));
    }
    if (!builder.isEmpty()) {
      lines.add(builder.toString());
    }
    return lines;
  }

  /**
   * 换行
   *
   * @return 返回自定义分割器
   */
  static Slicer ofNewLine() {
    return (b, p, ch) -> {
      if (ch == '\n') {
        char last = b.charAt(b.length() - 1);
        if (last == '\r') {
          b.deleteCharAt(b.length() - 1);
        }
        return true;
      }
      return false;
    };
  }

  /**
   * 拼接最后一个字符
   *
   * @param slicer 分割器
   * @return 返回自定义分割器
   */
  static CustomizedSlicer ofAppendLast(Slicer slicer) {
    return of((b, p, c) -> b.append(c), slicer);
  }

  /**
   * 删除固定长度
   *
   * @param length 长度
   * @param slicer 分割器
   * @return 返回自定义分割器
   */
  static CustomizedSlicer ofDelete(int length, Slicer slicer) {
    return of((b, p, c) -> b.delete(b.length() - length, b.length()), slicer);
  }

  /**
   * 自定义分割器
   *
   * @param customizer 自定义字符处理器
   * @param slicer     分割匹配
   * @return 返回
   */
  static CustomizedSlicer of(BuilderCustomizer customizer, Slicer slicer) {
    return new CustomizedSlicer(customizer, slicer);
  }

  interface BuilderCustomizer {
    /**
     * 自定义分割处理
     *
     * @param builder  字符串拼接
     * @param position 当前字符的位置
     * @param ch       当前字符
     */
    void accept(StringBuilder builder, int position, char ch);
  }

  class CustomizedSlicer implements Slicer {

    private BuilderCustomizer customizer;
    private Slicer slicer;

    public CustomizedSlicer(BuilderCustomizer customizer, Slicer slicer) {
      this.customizer = customizer;
      this.slicer = slicer;
    }

    @Override
    public boolean test(StringBuilder builder, int position, char ch) {
      if (slicer.test(builder, position, ch)) {
        customizer.accept(builder, position, ch);
        return true;
      }
      return false;
    }
  }

}
