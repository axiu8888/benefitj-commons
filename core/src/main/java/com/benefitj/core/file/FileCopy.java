package com.benefitj.core.file;

import com.benefitj.core.TryCatchUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;

/**
 * 文件拷贝或剪切
 */
public class FileCopy {

  /**
   * 拷贝文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @return 是否成功
   */
  public static boolean copy(File srcFile, File destFile) {
    return copy(srcFile, destFile, null);
  }

  /**
   * 拷贝文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @param filter   过滤器
   * @return 是否成功
   */
  public static boolean copy(File srcFile, File destFile, FileFilter filter) {
    return copy(srcFile, destFile, filter, true);
  }

  /**
   * 拷贝文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @param filter   过滤器
   * @param cover    是否覆盖已存在的文件
   * @return 是否成功
   */
  public static boolean copy(File srcFile, File destFile, FileFilter filter, boolean cover) {
    return operate(srcFile, destFile, filter, Type.COPY, cover);
  }

  /**
   * 剪切文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @return 是否成功
   */
  public static boolean cut(File srcFile, File destFile) {
    return cut(srcFile, destFile, null, true);
  }

  /**
   * 剪切文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @param filter   过滤器
   * @return 是否成功
   */
  public static boolean cut(File srcFile, File destFile, FileFilter filter) {
    return cut(srcFile, destFile, filter, true);
  }

  /**
   * 剪切文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @param filter   过滤器
   * @param cover    是否覆盖已存在的文件
   * @return 是否成功
   */
  public static boolean cut(File srcFile, File destFile, FileFilter filter, boolean cover) {
    return operate(srcFile, destFile, filter, Type.CUT, cover);
  }

  /**
   * 剪切文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @param filter   过滤器
   * @param type     操作类型
   * @param cover    是否覆盖已存在的文件
   * @return 是否成功
   */
  private static boolean operate(File srcFile, File destFile, FileFilter filter, Type type, boolean cover) {
    if (!exists(srcFile) || destFile == null) {
      return false;
    }

    if (!cover && isDir(destFile)) {
      // 不允许覆盖的情况下，如果目标文件已经存在，就不进行拷贝
      return false;
    }

    if (isFile(srcFile)) {
      // 文件直接拷贝
      return type != Type.CUT
          ? copyFile(srcFile, destFile)
          : copyFile(srcFile, destFile) && srcFile.delete();
    }

    // 创建目录
    boolean ignore = (exists(destFile)
        && destFile.isFile())
        && (destFile.delete() && destFile.mkdirs());

    String oldDirName = srcFile.getAbsolutePath();
    String newDirName = destFile.getAbsolutePath();
    StringBuilder sb = new StringBuilder();
    File[] files = srcFile.listFiles(filter);
    if (files != null && files.length > 0) {
      for (File tmpSrcFile : files) {
        sb.setLength(0);
        sb.append(tmpSrcFile.getAbsolutePath());
        sb.replace(0, oldDirName.length(), newDirName);
        operate(tmpSrcFile, new File(sb.toString()), filter, type, true);
      }
    }
    return type != Type.CUT || srcFile.delete();
  }

  /**
   * 拷贝文件或目录
   *
   * @param srcFile  原文件
   * @param destFile 目标文件
   * @return 是否成功
   */
  private static boolean copyFile(File srcFile, File destFile) {
    if (isFile(srcFile) && destFile != null) {
      destFile.delete();
      createIfNotExist(destFile);
      try (final FileInputStream fis = new FileInputStream(srcFile);
           final FileOutputStream fos = new FileOutputStream(destFile);) {
        FileChannel srcChannel = fis.getChannel();
        FileChannel destChannel = fos.getChannel();
        destChannel.transferFrom(srcChannel, 0, srcChannel.size());
        closeQuietly(srcChannel, destChannel);
        Path srcPath = srcFile.toPath();
        Path destPath = destFile.toPath();
        // 修改属性
        try {
          DosFileAttributes srcDosAttrs = Files.readAttributes(srcPath, DosFileAttributes.class);
          DosFileAttributeView destDosAttrView = Files.getFileAttributeView(destPath, DosFileAttributeView.class);
          destDosAttrView.setTimes(srcDosAttrs.lastModifiedTime(),
              srcDosAttrs.lastAccessTime(), srcDosAttrs.creationTime());
          destDosAttrView.setHidden(srcDosAttrs.isHidden());
          destDosAttrView.setSystem(srcDosAttrs.isSystem());
          destDosAttrView.setArchive(srcDosAttrs.isArchive());
          destDosAttrView.setReadOnly(srcDosAttrs.isReadOnly());
        } catch (UnsupportedOperationException e) {
          BasicFileAttributes srcAttrs = Files.readAttributes(srcPath, BasicFileAttributes.class);
          BasicFileAttributeView destAttrView = Files.getFileAttributeView(destPath, BasicFileAttributeView.class);
          destAttrView.setTimes(srcAttrs.lastModifiedTime(), srcAttrs.lastAccessTime(), srcAttrs.creationTime());
        }

        // 修改权限
        try {
          PosixFileAttributes srcPosixFileAttributes = Files.readAttributes(srcPath, PosixFileAttributes.class);
          PosixFileAttributeView destPosixFileAttributeView = Files.getFileAttributeView(srcPath, PosixFileAttributeView.class);
          destPosixFileAttributeView.setPermissions(srcPosixFileAttributes.permissions());
          destPosixFileAttributeView.setGroup(srcPosixFileAttributes.group());
          destPosixFileAttributeView.setOwner(Files.getOwner(srcPath));
        } catch (UnsupportedOperationException e) {
          destFile.setReadable(srcFile.canRead());
          destFile.setWritable(srcFile.canWrite());
          destFile.setExecutable(srcFile.canExecute());
        }
        return true;
      } catch (IOException e) {
        throw TryCatchUtils.throwing(e, IllegalStateException.class);
      }
    }
    return false;
  }

  private static void createIfNotExist(File f) {
    if (!exists(f.getParentFile())) {
      f.getParentFile().mkdirs();
    }
    if (!exists(f)) {
      try {
        f.createNewFile();
      } catch (IOException e) {
        throw TryCatchUtils.throwing(e, IllegalStateException.class);
      }
    }
  }

  /**
   * 是否为文件
   *
   * @param f 文件
   * @return 返回是否为文件
   */
  public static boolean isFile(File f) {
    return exists(f) && f.isFile();
  }

  /**
   * 是否为目录
   *
   * @param f 文件
   * @return 返回是否为目录
   */
  public static boolean isDir(File f) {
    return exists(f) && f.isDirectory();
  }

  /**
   * 判断文件是否存在
   *
   * @param f 文件
   * @return 返回是否存在
   */
  private static boolean exists(File f) {
    return f != null && f.exists();
  }

  /**
   * 关闭全部
   *
   * @param closes AutoCloseable实现(InputStream、OutputStream)
   */
  private static void closeQuietly(AutoCloseable... closes) {
    if (closes != null && closes.length > 0) {
      for (AutoCloseable c : closes) {
        try {
          c.close();
        } catch (Exception e) {/* ignore */}
      }
    }
  }


  /**
   * 文件操作的类型，拷贝或剪切
   */
  enum Type {
    /**
     * 拷贝
     */
    COPY,
    /**
     * 剪切
     */
    CUT;
  }

}
