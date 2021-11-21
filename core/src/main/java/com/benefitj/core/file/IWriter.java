package com.benefitj.core.file;

public interface IWriter extends AutoCloseable {

  /**
   * 写入数据
   *
   * @param str 字符串
   */
  void write(String str);

  /**
   * 写入数据
   *
   * @param strings 字符串
   */
  void write(String... strings);

  /**
   * 写入数据
   *
   * @param buf 字节缓冲
   */
  void write(byte[] buf);

  /**
   * 写入数据
   *
   * @param array 字节缓冲
   */
  void write(byte[]... array);

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  void write(byte[] buf, int offset, int len);

  /**
   * 写入并刷新
   *
   * @param str 字符串
   */
  void writeAndFlush(String str);

  /**
   * 写入并刷新
   *
   * @param strings 字符串
   */
  void writeAndFlush(String ...strings);

  /**
   * 写入并刷新
   *
   * @param array 字节缓冲
   */
  void writeAndFlush(byte[]... array);

  /**
   * 写入并刷新
   *
   * @param buf 字节缓冲
   */
  void writeAndFlush(byte[] buf);

  /**
   * 写入并刷新
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  void writeAndFlush(byte[] buf, int offset, int len);

  /**
   * 刷新
   */
  void flush();

  @Override
  void close();

}
