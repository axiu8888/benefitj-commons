package com.benefitj.core.file;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件写入功能
 */
public class FileWriterImpl implements IWriter<FileWriterImpl>, AttributeMap {

  private final Map<String, Object> attrs = new ConcurrentHashMap<>();
  private final File source;
  private OutputStream out;
  /**
   * 编码
   */
  private Charset charset;

  public FileWriterImpl(File source) {
    this(source, false);
  }

  public FileWriterImpl(File source, boolean append) {
    this(source, StandardCharsets.UTF_8, append);
  }

  public FileWriterImpl(File source, Charset charset, boolean append) {
    this.source = source;
    this.charset = charset;
    this.out = IWriter.wrapOutput(source, append);
  }

  /**
   * 附加属性的集合
   */
  @Override
  public Map<String, Object> attrs() {
    return attrs;
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

  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public FileWriterImpl write(byte[] buf, int offset, int len, boolean flush) {
    try {
      synchronized (this) {
        getOut().write(buf, offset, len);
        if (flush) {
          getOut().flush();
        }
      }
      return this;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 刷新
   */
  @Override
  public void flush() {
    try {
      synchronized (this) {
        getOut().flush();
      }
    } catch (IOException ignored) {/*^_^*/}
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
