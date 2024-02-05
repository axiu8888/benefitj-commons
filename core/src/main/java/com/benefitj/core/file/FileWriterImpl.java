package com.benefitj.core.file;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件写入功能
 */
public class FileWriterImpl implements IWriter, AttributeMap {

  private final Map<String, Object> attributes = new ConcurrentHashMap<>();
  private final File source;
  private OutputStream out;
  /**
   * 编码
   */
  private Charset charset = Charset.defaultCharset();

  public FileWriterImpl(File source, boolean append) {
    this.source = source;
    this.out = IWriter.wrapOutput(source, append);
  }

  /**
   * 附加属性的集合
   */
  @Override
  public Map<String, Object> attributes() {
    return attributes;
  }

  public File source() {
    return source;
  }

  /**
   * 长度
   */
  public long length() {
    return source().length();
  }

  /**
   * 路径
   */
  public String path() {
    return source().getAbsolutePath();
  }

  /**
   * 父目录
   */
  public File parent() {
    return source().getParentFile();
  }

  /**
   * 输出流
   */
  public OutputStream getOut() {
    return out;
  }

  public void setOut(OutputStream out) {
    this.out = out;
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
      getOut().write(buf, offset, len);
      if (flush) {
        getOut().flush();
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
        getOut().write(buf[offset + i]);
      }
      if (flush) {
        getOut().flush();
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
      getOut().flush();
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

  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  /**
   * 写入数据
   *
   * @param str 字符串
   */
  @Override
  public FileWriterImpl write(String str) {
    return write(str.getBytes(getCharset()));
  }

  /**
   * 写入数据
   *
   * @param strings 字符串
   */
  @Override
  public FileWriterImpl write(String... strings) {
    for (String str : strings) {
      write(str.getBytes(getCharset()));
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
    return writeAndFlush(str.getBytes(getCharset()));
  }

  @Override
  public FileWriterImpl writeAndFlush(String... strings) {
    synchronized (this) {
      for (int i = 0; i < strings.length; i++) {
        byte[] buf = strings[i].getBytes(getCharset());
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
      IOUtils.closeQuietly(getOut());
    }
  }

}
