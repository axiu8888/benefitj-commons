package com.benefitj.core.file.slicer;

import com.benefitj.core.file.IWriter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 文件分割器
 */
public class FileSlicer<T extends SliceFileWriter> implements IWriter {

  /**
   * 文件最大50MB
   */
  public static final long MAX_SIZE = (1024 << 10) * 50;
  /**
   * 默认缓存目录
   */
  private static final File DEFAULT_CACHE_DIR;

  static {
    Properties p = System.getProperties();
    String tmpDir = p.getProperty("java.io.tmpdir");
    DEFAULT_CACHE_DIR = new File(tmpDir, "/slicer/");
  }

  /**
   * 缓存目录
   */
  private File cacheDir = DEFAULT_CACHE_DIR;
  /**
   * 文件最大长度
   */
  private long maxSize = MAX_SIZE;
  /**
   * 创建文件
   */
  private FileFactory<T> fileFactory;
  /**
   * 当前的 Writer
   */
  private volatile T currentWriter;
  /**
   * 监听文件
   */
  private FileListener<T> fileListener;
  /**
   * 上次写入时间
   */
  private long lastWriteTime = -1;
  /**
   * 编码
   */
  private Charset charset = Charset.defaultCharset();

  public FileSlicer() {
  }

  public FileSlicer(File cacheDir, long maxSize) {
    this.cacheDir = cacheDir;
    this.maxSize = maxSize;
  }

  @Override
  public FileSlicer<T> write(String str) {
    return writeAndFlush(str.getBytes(getCharset()));
  }

  @Override
  public FileSlicer<T> write(String... strings) {
    return writeAndFlush(strings);
  }

  @Override
  public FileSlicer<T> write(byte[] buf) {
    return writeAndFlush(buf);
  }

  @Override
  public FileSlicer<T> write(byte[]... array) {
    return writeAndFlush(array);
  }

  @Override
  public FileSlicer<T> write(byte[] buf, int offset, int len) {
    return writeAndFlush(buf, offset, len);
  }

  @Override
  public FileSlicer<T> writeAndFlush(String str) {
    return writeAndFlush(str.getBytes(getCharset()));
  }

  @Override
  public FileSlicer<T> writeAndFlush(String... strings) {
    for (int i = 0; i < strings.length; i++) {
      byte[] buf = strings[i].getBytes(getCharset());
      write0(buf, 0, buf.length, i == strings.length - 1);
    }
    return this;
  }

  @Override
  public FileSlicer<T> writeAndFlush(byte[]... array) {
    for (int i = 0; i < array.length; i++) {
      write0(array[i], 0, array[i].length, i == array.length - 1);
    }
    return this;
  }

  @Override
  public FileSlicer<T> writeAndFlush(byte[] buf) {
    return write0(buf, 0, buf.length);
  }

  @Override
  public FileSlicer<T> writeAndFlush(byte[] buf, int offset, int len) {
    return write0(buf, offset, len);
  }

  /**
   * 写入数据
   *
   * @param buf    字节缓冲
   * @param offset 偏移量
   * @param len    长度
   */
  protected FileSlicer<T> write0(byte[] buf, int offset, int len) {
    return write0(buf, offset, len, true);
  }

  /**
   * 写入数据
   *
   * @param buf       字节缓冲
   * @param offset    偏移量
   * @param len       长度
   * @param checkSize 是否检查文件大小
   */
  protected FileSlicer<T> write0(byte[] buf, int offset, int len, boolean checkSize) {
    final T writer;
    synchronized (this) {
      writer = getWriter(true);
    }
    boolean newFile = false;
    synchronized (writer) {
      writer.writeAndFlush(buf, offset, len);
      this.setLastWriteTime(System.currentTimeMillis());
      // 检查文件
      if (checkSize && checkNewFile(writer)) {
        this.currentWriter = null;
        writer.close();
        newFile = true;
      }
    }
    if (newFile) {
      getFileListener().onHandle(writer, writer.getSource());
    }
    return this;
  }

  @Override
  public void flush() {
    T writer;
    synchronized (this) {
      writer = getWriter(false);
      if (writer != null) {
        this.currentWriter = null;
        writer.close();
      }
    }
    if (writer != null) {
      getFileListener().onHandle(writer, writer.getSource());
    }
  }

  @Override
  public void close() {
    // ignore
  }

  /**
   * 获取最近的写入时间
   */
  public long getLastWriteTime() {
    return lastWriteTime;
  }

  /**
   * 设置最近的写入时间
   *
   * @param lastWriteTime 写入时间
   */
  public void setLastWriteTime(long lastWriteTime) {
    this.lastWriteTime = lastWriteTime;
  }

  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  /**
   * 检查文件是否满足新文件条件
   */
  public boolean checkNewFile() {
    return checkNewFile(getWriter());
  }

  /**
   * 检查文件是否满足新文件条件
   */
  public boolean checkNewFile(T writer) {
    return writer != null && writer.length() >= getMaxSize();
  }

  /**
   * 获取 FileWriter
   *
   * @return 返回 FileWriter
   */
  protected T getWriter() {
    return getWriter(false);
  }

  /**
   * 获取 FileWriter
   *
   * @param autoCreate 是否自动创建
   * @return 返回 FileWriter
   */
  protected T getWriter(boolean autoCreate) {
    T writer = this.currentWriter;
    if (writer == null && autoCreate) {
      synchronized (this) {
        if ((writer = this.currentWriter) == null) {
          // 创建新文件
          writer = getFileFactory().create(getCacheDir());
          this.currentWriter = writer;
        }
      }
    }
    return writer;
  }

  /**
   * 获取最后一个文件长度
   */
  public long length() {
    T writer = this.getWriter();
    return writer != null ? writer.length() : 0L;
  }

  public File getCacheDir() {
    return cacheDir;
  }

  public FileSlicer<T> setCacheDir(File cacheDir) {
    this.cacheDir = cacheDir;
    return this;
  }

  public long getMaxSize() {
    return maxSize;
  }

  public FileSlicer<T> setMaxSize(long maxSize) {
    this.maxSize = maxSize > (1024 << 10) ? maxSize : (1024 << 10);
    return this;
  }

  public FileFactory<T> getFileFactory() {
    return fileFactory;
  }

  public FileSlicer<T> setFileFactory(FileFactory<T> fileFactory) {
    this.fileFactory = fileFactory;
    return this;
  }

  public FileListener<T> getFileListener() {
    return fileListener;
  }

  public FileSlicer<T> setFileListener(FileListener<T> fileListener) {
    this.fileListener = fileListener;
    return this;
  }

}
