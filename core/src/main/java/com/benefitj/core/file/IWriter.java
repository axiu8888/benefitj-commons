package com.benefitj.core.file;

import java.io.File;

public interface IWriter extends AutoCloseable {

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
   * 写入并刷新
   *
   * @param array 字节缓冲
   */
  IWriter writeAndFlush(byte[]... array);

  /**
   * 写入并刷新
   *
   * @param buf 字节缓冲
   */
  IWriter writeAndFlush(byte[] buf);

  /**
   * 写入并刷新
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  IWriter writeAndFlush(byte[] buf, int offset, int len);

  /**
   * 刷新
   */
  IWriter flush();

  @Override
  void close();


  /**
   * 创建文件写入器
   *
   * @param file 文件
   * @return 返回文件写入器
   */
  static IWriter newFileWriter(String file) {
    return newFileWriter(new File(file));
  }

  /**
   * 创建文件写入器
   *
   * @param file 文件
   * @return 返回文件写入器
   */
  static IWriter newFileWriter(File file) {
    return new FileWriterImpl(file);
  }

}
