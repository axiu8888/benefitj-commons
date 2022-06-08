package com.benefitj.core.file;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;

import java.io.File;
import java.io.RandomAccessFile;

public class RaFile implements OutputWriter<RaFile> {

  public static final String[] MODES = {"r","rw","rws","rwd"};

  private final File source;
  private final RandomAccessFile raf;

  public RaFile(String filename) {
    this(filename, "rw");
  }

  public RaFile(String filename, String mode) {
    this(new File(filename), mode);
  }

  public RaFile(File source) {
    this(source, "rw");
  }

  /**
   * 构造函数
   *
   * @param source 文件
   * @param mode   "r", "rw", "rws", or "rwd"
   */
  public RaFile(File source, String mode) {
    this.source = source;
    try {
      this.raf = new RandomAccessFile(source, mode);
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  public File getSource() {
    return source;
  }

  public RandomAccessFile getRaf() {
    return raf;
  }

  public long length() {
    return CatchUtils.tryThrow(() -> getRaf().length());
  }

  public RaFile skipBytes(long n) {
    synchronized (this) {
      CatchUtils.tryThrow(() -> {
        for (long i = n; i > 0; i -= 4096) {
          getRaf().skipBytes((int) (i >= 4096 ? 4096 : i));
        }
      });
    }
    return this;
  }

  public RaFile seek(long position) {
    synchronized (this) {
      CatchUtils.tryThrow(() -> getRaf().seek(position));
    }
    return this;
  }

  public RaFile seekFirst() {
    return seek(0);
  }

  public RaFile seekLast() {
    return seek(length());
  }

  public RaFile setLength(long newLength) {
    synchronized (this) {
      CatchUtils.tryThrow(() -> getRaf().setLength(newLength));
    }
    return this;
  }

  /**
   * 重置文件的大小，去除后面多余的数据
   *
   * @param size 重置的大小，不能比文件长度大
   */
  public RaFile resizeStart(long size) {
    return resize(0, size);
  }

  /**
   * 重置文件的大小，去除前面多余的数据
   *
   * @param size 重置的大小，不能比文件长度大
   */
  public RaFile resizeEnd(long size) {
    return resize(Math.max(length() - size, 0), size);
  }

  /**
   * 重置文件的大小
   *
   * @param start 开始的位置
   * @param size  重置的大小，不能比文件长度大
   */
  public RaFile resize(long start, long size) {
    if (length() <= (size - start)) {
      // 不做修改
      return this;
    }
    if (start == 0) {
      setLength(size);
      return this;
    }
    synchronized (this) {
      CatchUtils.tryThrow(() -> {
        // 平移数据
        RandomAccessFile raf = this.getRaf();
        raf.seek(0);
        long wpos = raf.getFilePointer();
        skipBytes(start);
        long rpos = raf.getFilePointer();
        byte[] buf = new byte[4096];
        int n;
        while (-1 != (n = raf.read(buf))) {
          raf.seek(wpos);
          raf.write(buf, 0, n);
          rpos += n;
          wpos += n;
          raf.seek(rpos);
        }
        raf.setLength(wpos);
      });
    }
    return this;
  }


  @Override
  public RaFile write(byte[] buf, int offset, int len, boolean flush) {
    CatchUtils.tryThrow(() -> getRaf().write(buf, offset, len));
    return this;
  }

  @Override
  public RaFile flush() {
    // ignore
    return this;
  }

  @Override
  public void close() {
    IOUtils.closeQuietly(getRaf());
  }
}
