package com.benefitj.core.file.slicer;

import com.benefitj.core.file.IWriter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 文件分割器
 */
public class FileSlicer<T extends SliceFileWriter> implements IWriter<FileSlicer<T>> {

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
  public FileSlicer<T> write(byte[] buf, int offset, int len, boolean flush) {
    final T writer = getWriter(true);
    boolean newFile = false;
    synchronized (writer) {
      writer.writeAndFlush(buf, offset, len);
      this.setLastWriteTime(System.currentTimeMillis());
      // 检查文件
      if (checkNewFile(writer)) {
        this.currentWriter = null;
        writer.close();
        newFile = true;
      }
    }
    if (newFile) {
      getFileListener().onHandle(writer, writer.source());
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
      getFileListener().onHandle(writer, writer.source());
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
          this.currentWriter = (writer = getFileFactory().create(getCacheDir(), getCharset()));
        }
      }
    }
    return writer;
  }

  /**
   * 获取最后一个文件长度
   */
  public long length() {
    T w = this.getWriter();
    return w != null ? w.length() : 0L;
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
