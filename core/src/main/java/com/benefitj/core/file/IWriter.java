package com.benefitj.core.file;

import com.benefitj.core.ByteArrayCopy;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface IWriter<T extends IWriter<T>> extends AutoCloseable, Appendable, Flushable {

  default Charset getCharset() {
    return StandardCharsets.UTF_8;
  }

  /**
   * 写入数据
   *
   * @param str 字符串
   */
  default T write(String str) {
    return write(str.getBytes(getCharset()));
  }

  /**
   * 写入数据
   *
   * @param array 字符串
   */
  default T write(String... array) {
    return write(String.join("", array));
  }

  /**
   * 写入并刷新
   *
   * @param str 字符串
   */
  default T writeAndFlush(String str) {
    return writeAndFlush(str.getBytes(getCharset()));
  }

  /**
   * 写入并刷新
   *
   * @param array 字符串数组
   */
  default T writeAndFlush(String... array) {
    return writeAndFlush(String.join("", array));
  }

  /**
   * 写入数据
   *
   * @param buf 字节缓冲
   */
  default T write(byte[] buf) {
    return write(buf, 0, buf.length);
  }

  /**
   * 写入数据
   *
   * @param array 字节缓冲
   */
  default T write(byte[]... array) {
    return write(ByteArrayCopy.concat(array));
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  default T write(byte[] buf, int offset, int len) {
    return write(buf, offset, len, false);
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   * @param flush  是否写入
   */
  T write(byte[] buf, int offset, int len, boolean flush);

  /**
   * 写入并刷新
   *
   * @param buf 字节缓冲
   */
  default T writeAndFlush(byte[] buf) {
    return writeAndFlush(buf, 0, buf.length);
  }

  /**
   * 写入并刷新
   *
   * @param array 字节缓冲
   */
  default T writeAndFlush(byte[]... array) {
    return writeAndFlush(ByteArrayCopy.concat(array));
  }

  /**
   * 写入并刷新
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  default T writeAndFlush(byte[] buf, int offset, int len) {
    return write(buf, offset, len, true);
  }

  @Override
  default T append(CharSequence csq) {
    return append(csq, 0, csq.length());
  }

  @Override
  default T append(CharSequence csq, int start, int end) {
    if (csq.length() == (end - start)) {
      return write(csq.toString());
    }
    StringBuilder sb = new StringBuilder();
    for (int i = start; i < end; i++) {
      sb.append(csq.charAt(i));
    }
    return write(sb.toString());
  }

  @Override
  default T append(char c) {
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

  default T flushAndClose() {
    flush();
    close();
    return (T) this;
  }

  /**
   * 创建文件写入器
   *
   * @param file   文件
   * @param append 是否在文件后拼接
   * @return 返回文件写入器
   */
  static FileWriterImpl create(String file, boolean append) {
    return create(new File(file), append);
  }

  /**
   * 创建文件写入器
   *
   * @param file   文件
   * @param append 是否在文件后拼接
   * @return 返回文件写入器
   */
  static FileWriterImpl create(File file, boolean append) {
    IOUtils.createFile(file.getAbsolutePath());
    return new FileWriterImpl(file, append);
  }

  static BufferedOutputStream wrapOut(File src, boolean append) {
    try {
      return new BufferedOutputStream2(new FileOutputStream(src, append));
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  static BufferedWriter wrapWriter(File src, boolean append) {
    return wrapWriter(src, StandardCharsets.UTF_8, append);
  }

  static BufferedWriter wrapWriter(File src, Charset charset, boolean append) {
    try {
      return new BufferedWriter(new FileWriter(src, charset, append));
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }


  class BufferedOutputStream2 extends BufferedOutputStream {

    protected volatile boolean closed;

    public BufferedOutputStream2(OutputStream out) {
      super(out);
    }

    public BufferedOutputStream2(OutputStream out, int size) {
      super(out, size);
    }

    @Override
    public void close() throws IOException {
      synchronized (this) {
        this.closed = true;
        super.close();
      }
    }

    public boolean isClosed() {
      return closed;
    }

  }

}
