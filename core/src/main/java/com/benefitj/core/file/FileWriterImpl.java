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
 * 文件写入功能
 */
public class FileWriterImpl implements IWriter, AttributeMap {

  private final File source;
  private final BufferedOutputStream out;
  private final Map<String, Object> attributes = new ConcurrentHashMap<>();

  public FileWriterImpl(File source, boolean append) {
    this.source = source;
    try {
      this.out = new BufferedOutputStream(new FileOutputStream(source, append));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 附加属性的集合
   */
  @Override
  public Map<String, Object> attributes() {
    return attributes;
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
  protected FileWriterImpl write0(byte[] buf, int offset, int len, boolean flush) {
    try {
      out().write(buf, offset, len);
      if (flush) {
        out().flush();
      }
      return this;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   * @param flush  是否刷新
   */
  protected FileWriterImpl write0(char[] buf, int offset, int len, boolean flush) {
    try {
      for (int i = 0; i < len; i++) {
        out().write(buf[offset + i]);
      }
      if (flush) {
        out().flush();
      }
      return this;
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
  @Override
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
  @Override
  public FileWriterImpl write(String str) {
    return write(str.getBytes());
  }

  /**
   * 写入数据
   *
   * @param strings 字符串
   */
  @Override
  public FileWriterImpl write(String... strings) {
    for (String str : strings) {
      write(str.getBytes());
    }
    return this;
  }

  /**
   * 写入数据
   *
   * @param buf 字节缓冲
   */
  @Override
  public FileWriterImpl write(byte[] buf) {
    return write(buf, 0, buf.length);
  }

  /**
   * 写入数据
   *
   * @param array 字节缓冲
   */
  @Override
  public FileWriterImpl write(byte[]... array) {
    for (byte[] buf : array) {
      write(buf);
    }
    return this;
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  @Override
  public FileWriterImpl write(byte[] buf, int offset, int len) {
    synchronized (this) {
      write0(buf, offset, len, false);
    }
    return this;
  }

  /**
   * 写入并刷新
   *
   * @param str 字符串
   */
  @Override
  public FileWriterImpl writeAndFlush(String str) {
    return writeAndFlush(str.getBytes());
  }

  @Override
  public FileWriterImpl writeAndFlush(String... strings) {
    synchronized (this) {
      for (int i = 0; i < strings.length; i++) {
        byte[] buf = strings[i].getBytes();
        write0(buf, 0, buf.length, i == (strings.length - 1));
      }
    }
    return this;
  }

  /**
   * 写入并刷新
   *
   * @param array 字节缓冲
   */
  @Override
  public FileWriterImpl writeAndFlush(byte[]... array) {
    synchronized (this) {
      for (byte[] buf : array) {
        write0(buf, 0, buf.length, false);
      }
      flush0();
    }
    return this;
  }

  /**
   * 写入并刷新
   *
   * @param buf 字节缓冲
   */
  @Override
  public FileWriterImpl writeAndFlush(byte[] buf) {
    return writeAndFlush(buf, 0, buf.length);
  }

  /**
   * 写入并刷新
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  @Override
  public FileWriterImpl writeAndFlush(byte[] buf, int offset, int len) {
    synchronized (this) {
      write0(buf, offset, len, true);
    }
    return this;
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

}
