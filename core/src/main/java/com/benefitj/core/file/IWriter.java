package com.benefitj.core.file;

import com.benefitj.core.IOUtils;

import java.io.File;
import java.io.Flushable;

public interface IWriter extends AutoCloseable, Appendable, Flushable {

  /**
   * 写入数据
   *
   * @param str 字符串
   */
  IWriter write(String str);

  /**
   * 写入数据
   *
   * @param strings 字符串
   */
  IWriter write(String... strings);

  /**
   * 写入并刷新
   *
   * @param str 字符串
   */
  IWriter writeAndFlush(String str);

  /**
   * 写入并刷新
   *
   * @param strings 字符串
   */
  IWriter writeAndFlush(String... strings);

  /**
   * 写入数据
   *
   * @param buf 字节缓冲
   */
  IWriter write(byte[] buf);

  /**
   * 写入数据
   *
   * @param array 字节缓冲
   */
  IWriter write(byte[]... array);

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  IWriter write(byte[] buf, int offset, int len);

  /**
   * 写入并刷新
   *
   * @param buf 字节缓冲
   */
  IWriter writeAndFlush(byte[] buf);

  /**
   * 写入并刷新
   *
   * @param array 字节缓冲
   */
  IWriter writeAndFlush(byte[]... array);

  /**
   * 写入并刷新
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  IWriter writeAndFlush(byte[] buf, int offset, int len);

  @Override
  default IWriter append(CharSequence csq) {
    return append(csq, 0, csq.length());
  }

  @Override
  default IWriter append(CharSequence csq, int start, int end) {
    StringBuilder sb = new StringBuilder();
    for (int i = start; i < end; i++) {
      sb.append(csq.charAt(i));
    }
    return write(sb.toString());
  }

  @Override
  default IWriter append(char c) {
    return write(String.valueOf(c));
  }


  /**
   * 刷新
   */
  @Override
  void flush();

  /**
   * 关闭
   */
  @Override
  void close();

  default IWriter flushAndClose() {
    flush();
    close();
    return this;
  }

  /**
   * 创建文件写入器
   *
   * @param file   文件
   * @param append 是否在文件后拼接
   * @return 返回文件写入器
   */
  static IWriter createWriter(String file, boolean append) {
    return createWriter(new File(file), append);
  }

  /**
   * 创建文件写入器
   *
   * @param file   文件
   * @param append 是否在文件后拼接
   * @return 返回文件写入器
   */
  static IWriter createWriter(File file, boolean append) {
    IOUtils.createFile(file.getAbsolutePath());
    return new FileWriterImpl(file, append);
  }

}
