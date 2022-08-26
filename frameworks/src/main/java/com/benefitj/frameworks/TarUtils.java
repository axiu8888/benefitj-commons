package com.benefitj.frameworks;

import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.SystemProperty;
import com.benefitj.core.file.CompressUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TarUtils {


  /**
   * TAR & gZip
   *
   * @param src 源文件
   * @return 返回 TAR & gZip 后的文件
   */
  public static File tarGzip(File src) {
    return tarGzip(src, CompressUtils.getFilename(src, ".tar.gz"));
  }

  /**
   * TAR & gZip
   *
   * @param src  源文件
   * @param dest 目标文件
   * @return 返回 TAR & gZip 后的文件
   */
  public static File tarGzip(File src, File dest) {
    final File tar = tar(src, CompressUtils.getFilename(src, ".tar"));
    try {
      return CompressUtils.gzip(tar, dest);
    } finally {
      tar.delete();
    }
  }


  /**
   * TAR
   *
   * @param src 源文件
   * @return 返回 TAR 后的文件
   */
  public static File tar(File src) {
    return tar(src, CompressUtils.getFilename(src, ".tar"));
  }

  /**
   * TAR
   *
   * @param src  源文件
   * @param dest 目标文件
   * @return 返回 TAR 后的文件
   */
  public static File tar(File src, File dest) {
    if (!src.exists()) {
      throw new IllegalArgumentException("源文件不存在!");
    }
    final File tar = CompressUtils.getFilename(dest, ".tar");
    IOUtils.createFile(tar.getAbsolutePath());
    try (final TarArchiveOutputStream out = new TarArchiveOutputStream(new FileOutputStream(tar));) {
      out.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
      compress(out, src, src.getName());
      return tar;
    } catch (IOException e) {
      tar.delete();
      throw new IllegalStateException(e);
    }
  }

  private static void compress(TarArchiveOutputStream out, File src, String base) throws IOException {
    if (src.isDirectory()) {
      File[] files = src.listFiles();
      if (files == null || files.length == 0) {
        TarArchiveEntry entry = new TarArchiveEntry(src);
        entry.setName(base);
        out.putArchiveEntry(entry);
        out.closeArchiveEntry();
      } else {
        for (File f : files) {
          compress(out, f, base + File.separator + f.getName());
        }
      }
    } else {
      TarArchiveEntry entry = new TarArchiveEntry(src);
      entry.setName(base);
      out.putArchiveEntry(entry);
      IOUtils.write(src, out, false);
      out.closeArchiveEntry();
    }
  }

  /**
   * 解压缩
   *
   * @param src 源文件
   * @return 返回解压后的目录
   */
  public static File untarGzip(File src) {
    return untarGzip(src, new File(src, CompressUtils.trimRight(src.getName(), ".tar.gz", "")));
  }

  /**
   * 解压缩
   *
   * @param src  源文件
   * @param dest 目标目录
   * @return 返回解压后的目录
   */
  public static File untarGzip(File src, File dest) {
    String filename = CompressUtils.trimRight(src.getName(), ".tar.gz", ".tar");
    File tar = CompressUtils.ungzip(src, new File(SystemProperty.getJavaIOTmpDir() + IdUtils.nextId(10), filename));
    try {
      return untar(tar, dest);
    } finally {
      IOUtils.deleteFile(tar);
    }
  }

  /**
   * 解压缩
   *
   * @param src 源文件
   * @return 返回解压后的目录
   */
  public static File untar(File src) {
    return untar(src, new File(src, CompressUtils.trimRight(src.getName(), ".tar", "")));
  }

  /**
   * 解压缩
   *
   * @param src 源文件
   * @return 返回解压后的目录
   */
  public static File untar(File src, File dest) {
    try (final TarArchiveInputStream in = new TarArchiveInputStream(new FileInputStream(src));) {
      ArchiveEntry entry;
      while ((entry = in.getNextEntry()) != null) {
        if (entry.isDirectory()) {
          IOUtils.mkDirs(dest, entry.getName());
        } else {
          IOUtils.write(in, IOUtils.createFile(dest, entry.getName()), false);
        }
      }
      return dest;
    } catch (IOException e) {
      IOUtils.deleteFile(dest);
      throw new IllegalStateException(e);
    }
  }

}
