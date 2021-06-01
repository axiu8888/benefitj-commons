package com.benefitj.core.file;

import com.benefitj.core.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.*;

/**
 * 文件压缩或解压
 */
public class CompressUtils {

  /**
   * 压缩文件或目录
   *
   * @param src 源文件
   * @return 返回压缩文件
   */
  public static File zip(File src) {
    String name = src.getName();
    String filename = name.lastIndexOf(".") > 0
        ? name.substring(0, name.lastIndexOf("."))
        : name;
    return zip(src, new File(src.getParentFile(), filename + ".zip"));
  }

  /**
   * 压缩文件或目录
   *
   * @param src  源文件
   * @param dest 目标文件
   * @return 返回压缩文件
   */
  public static File zip(File src, File dest) {
    //创建缓冲输出流
    try (final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest));
         final BufferedOutputStream bos = new BufferedOutputStream(out);) {
      //调用函数
      compress(out, bos, src, src.getName());
      return dest;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 压缩
   *
   * @param out     zip的输出流
   * @param bos     ~
   * @param zipFile 压缩文件
   * @param base    文件名或目录名
   * @throws IOException 抛出的异常
   */
  private static void compress(ZipOutputStream out, BufferedOutputStream bos, File zipFile, String base) throws IOException {
    if (zipFile.isDirectory()) {
      File[] files = zipFile.listFiles();
      if (files == null || files.length == 0) {
        out.putNextEntry(new ZipEntry(zipFile + File.separator));
      } else {
        for (File file : files) {
          compress(out, bos, file, base + File.separator + file.getName());
        }
      }
    } else {
      // 如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
      out.putNextEntry(new ZipEntry(base));
      try (final FileInputStream fos = new FileInputStream(zipFile);
           final BufferedInputStream bis = new BufferedInputStream(fos);) {
        int len;
        byte[] buf = new byte[1024 << 8];
        while ((len = bis.read(buf)) > 0) {
          bos.write(buf, 0, len);
          bos.flush();
        }
      }
    }
  }

  /**
   * 解压缩
   *
   * @param zip ZIP文件
   * @return 返回解压后的文件夹
   */
  public static File unzip(File zip) {
    return unzip(zip, null);
  }

  /**
   * 解压缩
   *
   * @param zip  ZIP文件
   * @param dest 目标目录
   * @return 返回解压后的文件夹
   */
  public static File unzip(File zip, @Nullable File dest) {
    zipIterator(zip, (input, entry) -> {
      try {
        if (!entry.isDirectory()) {
          File file = new File(dest != null ? dest : zip.getParentFile(), entry.getName());
          file.getParentFile().mkdirs();
          file.createNewFile();
          byte[] buf = new byte[1024 << 8];
          int len;
          try (final FileOutputStream fos = new FileOutputStream(file);) {
            while ((len = input.read(buf)) > 0) {
              fos.write(buf, 0, len);
              fos.flush();
            }
          }
        }
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    });
    return dest != null ? dest : zip.getParentFile();
  }

  /**
   * 获取所有的压缩文件名称
   *
   * @param zip 压缩包
   * @return 返回文件名
   */
  public static Map<String, Boolean> getFileMap(File zip) {
    Map<String, Boolean> files = new LinkedHashMap<>();
    zipIterator(zip, (input, entry) -> files.put(entry.getName(), entry.isDirectory()));
    return files;
  }

  /**
   * 迭代压缩包内的文件
   *
   * @param zip      压缩包
   * @param consumer 消费者
   */
  public static void zipIterator(File zip, BiConsumer<ZipInputStream, ZipEntry> consumer) {
    try (final ZipInputStream input = new ZipInputStream(new FileInputStream(zip));) {
      ZipEntry entry;
      while ((entry = input.getNextEntry()) != null) {
        consumer.accept(input, entry);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * GZIP压缩
   *
   * @param src  GZIP文件
   * @param dest 目标文件
   */
  public static void gzip(File src, File dest) {
    try (final GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(dest));
         final FileInputStream in = new FileInputStream(src);) {
      IOUtils.write(in, out, 1024 << 4);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * GZIP解压
   *
   * @param src  GZIP文件
   * @param dest 目标文件
   */
  public static void ungzip(File src, File dest) {
    try (final GZIPInputStream in = new GZIPInputStream(new FileInputStream(src));
         final FileOutputStream out = new FileOutputStream(dest);) {
      IOUtils.write(in, out, 1024 << 4);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
