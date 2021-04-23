package com.benefitj.core.file;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 输出文件
 */
public class OutputFile implements AutoCloseable, AttributeMap {

  private final File source;
  private final BufferedOutputStream out;
  private final Map<String, Object> attributes = new ConcurrentHashMap<>();

  public OutputFile(File source) {
    this.source = source;
    try {
      this.out = new BufferedOutputStream(new FileOutputStream(source));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public File getSource() {
    return source;
  }

  /**
   * 长度
   */
  public long length() {
    return getSource().length();
  }

  /**
   * 路径
   */
  public String path() {
    return getSource().getAbsolutePath();
  }

  /**
   * 父目录
   */
  public File parent() {
    return getSource().getParentFile();
  }

  /**
   * 输出流
   */
  public BufferedOutputStream out() {
    return out;
  }

  /**
   * 写入数据
   *
   * @param str 字符串
   */
  public void write(String str) {
    write(str.getBytes());
  }

  /**
   * 写入数据
   *
   * @param buf 字节缓冲
   */
  public void write(byte[] buf) {
    write(buf, 0, buf.length);
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    颤抖
   */
  public void write(byte[] buf, int offset, int len) {
    try {
      synchronized (this) {
        out().write(buf, offset, len);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 刷新
   */
  public void flush() {
    try {
      out().flush();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 写入并刷新
   *
   * @param str 字符串
   */
  public void writeAndFlush(String str) {
    writeAndFlush(str.getBytes());
  }

  /**
   * 写入并刷新
   *
   * @param buf 字节缓冲
   */
  public void writeAndFlush(byte[] buf) {
    writeAndFlush(buf, 0, buf.length);
  }

  /**
   * 写入并刷新
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  public void writeAndFlush(byte[] buf, int offset, int len) {
    synchronized (this) {
      write(buf, offset, len);
      flush();
    }
  }

  /**
   * 关闭输出流
   */
  @Override
  public void close() {
    synchronized (this) {
      IOUtils.closeQuietly(out());
    }
  }

  /**
   * 附加属性的集合
   */
  @Override
  public Map<String, Object> attributes() {
    return attributes;
  }

}
