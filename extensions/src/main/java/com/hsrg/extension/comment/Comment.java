package com.hsrg.extension.comment;

/**
 * 注释
 */
public class Comment {
  /**
   * 开始的位置
   */
  int start;
  /**
   * 结束的位置
   */
  int end;
  /**
   * 注释类型
   */
  CommentType type;
  /**
   * 注释的内容
   */
  String content;

  public Comment() {
  }

  public Comment(int start, int end, CommentType type, String content) {
    this.start = start;
    this.end = end;
    this.type = type;
    this.content = content;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
