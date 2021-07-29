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
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   * @param flush  是否刷新
   */
  protected void write0(byte[] buf, int offset, int len, boolean flush) {
    try {
      out().write(buf, offset, len);
      if (flush) {
        out().flush();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 刷新
   */
  protected void flush0() {
    try {
      out().flush();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 刷新
   */
  public void flush() {
    synchronized (this) {
      flush0();
    }
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
   * @param strings 字符串
   */
  public void write(String... strings) {
    for (String str : strings) {
      write(str.getBytes());
    }
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
   * @param array 字节缓冲
   */
  public void write(byte[]... array) {
    for (byte[] buf : array) {
      write(buf);
    }
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  public void write(byte[] buf, int offset, int len) {
    synchronized (this) {
      write0(buf, offset, len, false);
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
   * @param array 字节缓冲
   */
  public void writeAndFlush(byte[]... array) {
    synchronized (this) {
      for (byte[] buf : array) {
        write0(buf, 0, buf.length, false);
      }
      flush0();
    }
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
      write0(buf, offset, len, true);
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
