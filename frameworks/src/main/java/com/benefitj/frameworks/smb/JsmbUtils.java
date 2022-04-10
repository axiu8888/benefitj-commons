package com.benefitj.frameworks.smb;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import jcifs.smb.SmbFileFilter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Arrays;

public class JsmbUtils {

  public static final String separator = "/";

  public static String refine(String path) {
    return refine(path, true, true);
  }

  public static String refine(String path, boolean prefix, boolean suffix) {
    if (StringUtils.isNotBlank(path)) {
      if (prefix) {
        path = path.startsWith("/") ? path.substring(1) : path;
      }
      if (suffix) {
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
      }
      return path;
    }
    return "";
  }

  public static String joint(String prefix, String suffix) {
    if (StringUtils.isAllBlank(prefix, suffix)) {
      return "";
    }
    if (StringUtils.isAnyBlank(prefix, suffix)) {
      if (StringUtils.isBlank(prefix)) {
        return suffix.startsWith("/") ? suffix : separator + suffix;
      }
      return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }
    return refine(prefix, false, true) + separator + refine(suffix, true, false);
  }


  /**
   * 从远程拷贝文件
   *
   * @param src  源文件
   * @param dest 目标文件
   */
  public static void transfer(JsmbFile src, JsmbFile dest) {
    transfer(src, dest, true);
  }

  /**
   * 从远程拷贝文件
   *
   * @param src        源文件
   * @param dest       目标文件
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  public static void transfer(JsmbFile src, JsmbFile dest, boolean multiLevel) {
    transfer(src, dest, null, multiLevel);
  }

  /**
   * 从远程拷贝文件
   *
   * @param src        源文件
   * @param dest       目标文件
   * @param filter     文件过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  public static void transfer(JsmbFile src, JsmbFile dest, SmbFileFilter filter, boolean multiLevel) {
    if (!src.exists()) {
      throw new IllegalStateException("文件不存在：" + src.getPath());
    }
    dest.deleteOnExist();
    dest.createIfNotExist(src.isDirectory());
    if (src.isDirectory()) {
      dest.mkdirs();
      if (multiLevel) {
        for (JsmbFile subFile : src.listFiles(filter)) {
          try {
            transfer(subFile, dest.createSub(subFile.getName()), filter, multiLevel);
          } finally {
            IOUtils.closeQuietly(subFile);
          }
        }
      }
    } else {
      CatchUtils.tryThrow(() -> {
        IOUtils.write(src.openInputStream(), dest.openOutputStream());
        src.setAttributes(dest.getAttributes());
        src.setFileTimes(dest.createTime(), dest.lastModified(), dest.lastAccess());
      }, e -> {
        System.err.println("exists: " + src.exists());
        System.err.println("getPath: " + src.getPath());
        System.err.println("getRelativePath: " + src.getRelativePath());
        System.err.println("getSecurity: " + CatchUtils.ignore(() -> Arrays.toString(src.getSecurity())));
        throw new IllegalStateException(e);
      });
    }
  }

  /**
   * 拷贝文件到远程
   *
   * @param src  本地原文件
   * @param dest 目标文件
   */
  public static void transferFrom(File src, JsmbFile dest) {
    transferFrom(src, dest, true);
  }

  /**
   * 拷贝文件到远程
   *
   * @param src        本地原文件
   * @param dest       目标文件
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  public static void transferFrom(File src, JsmbFile dest, boolean multiLevel) {
    transferFrom(src, dest, null, multiLevel);
  }

  /**
   * 拷贝文件到远程
   *
   * @param src        本地原文件
   * @param dest       目标文件
   * @param filter     文件过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  public static void transferFrom(File src, JsmbFile dest, @Nullable FileFilter filter, boolean multiLevel) {
    dest.createIfNotExist(src.isDirectory());
    if (src.isDirectory()) {
      if (multiLevel) {
        for (File file : src.listFiles(filter)) {
          try (JsmbFile jf = dest.createSub(file.getName());) {
            jf.transferFrom(file, filter, true);
          }
        }
      }
    } else {
      if (src.length() > 0) {
        IOUtils.write(src, dest.openOutputStream());
        CatchUtils.ignore(() -> {
          // 修改属性
          Path srcPath = src.toPath();
          try {
            DosFileAttributes srcAttrs = Files.readAttributes(srcPath, DosFileAttributes.class);
            dest.getSource().setFileTimes(srcAttrs.creationTime().toMillis()
                , srcAttrs.lastModifiedTime().toMillis(), srcAttrs.lastAccessTime().toMillis());
            if (srcAttrs.isReadOnly()) {
              dest.getSource().setReadOnly();
            }
          } catch (UnsupportedOperationException e) {
            BasicFileAttributes srcAttrs = Files.readAttributes(srcPath, BasicFileAttributes.class);
            dest.setFileTimes(srcAttrs.lastModifiedTime().toMillis(),
                srcAttrs.lastAccessTime().toMillis(), srcAttrs.creationTime().toMillis());
          }
        });
      }
    }
  }

  /**
   * 从远程拷贝文件
   *
   * @param src  源文件
   * @param dest 目标文件
   */
  public static void transferTo(JsmbFile src, File dest) {
    transferTo(src, dest, true);
  }

  /**
   * 从远程拷贝文件
   *
   * @param src        源文件
   * @param dest       目标文件
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  public static void transferTo(JsmbFile src, File dest, boolean multiLevel) {
    transferTo(src, dest, null, multiLevel);
  }

  /**
   * 从远程拷贝文件
   *
   * @param src        源文件
   * @param dest       目标文件
   * @param filter     文件过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  public static void transferTo(JsmbFile src, File dest, SmbFileFilter filter, boolean multiLevel) {
    checkFileExist(src);
    if (src.isDirectory()) {
      dest.mkdirs();
      if (multiLevel) {
        for (JsmbFile subFile : src.listFiles(filter)) {
          transferTo(subFile.connect(), new File(dest, subFile.getName()), filter, multiLevel);
        }
      }
    } else {
      IOUtils.createFile(dest.getAbsolutePath());
      System.err.println(dest.getAbsolutePath() + " ==========>: " + src.getPath() + ", " + src.length());
      IOUtils.write(src.openInputStream(), dest, true);
      CatchUtils.ignore(() -> {
        // 修改属性
        dest.setReadable(src.canRead());
        dest.setWritable(src.canWrite());
        dest.setLastModified(src.lastModified());
      });
    }
  }

  public static void checkFileExist(JsmbFile jf) {
    if (!jf.exists()) {
      throw new IllegalStateException("文件不存在: " + jf.getPath());
    }
  }
}
