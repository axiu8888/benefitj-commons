package com.benefitj.core;

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

  public long length() {
    return getSource().length();
  }

  public String path() {
    return getSource().getAbsolutePath();
  }

  public File parent() {
    return getSource().getParentFile();
  }

  public BufferedOutputStream getOut() {
    return out;
  }

  public void write(String str) {
    write(str.getBytes());
  }

  public void write(byte[] buf) {
    write(buf, 0, buf.length);
  }

  public void write(byte[] buf, int offset, int len) {
    try {
      synchronized (this) {
        getOut().write(buf, offset, len);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void flush() {
    try {
      synchronized (this) {
        getOut().flush();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void writeAndFlush(String str) {
    writeAndFlush(str.getBytes());
  }

  public void writeAndFlush(byte[] buf) {
    writeAndFlush(buf, 0, buf.length);
  }

  public void writeAndFlush(byte[] buf, int offset, int len) {
    try {
      synchronized (this) {
        getOut().write(buf, offset, len);
        getOut().flush();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() {
    synchronized (this) {
      IOUtils.closeQuietly(getOut());
    }
  }

  @Override
  public Map<String, Object> attributes() {
    return attributes;
  }

}
