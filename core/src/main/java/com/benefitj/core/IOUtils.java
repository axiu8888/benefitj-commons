package com.benefitj.core;

import com.benefitj.core.functions.IBiConsumer;
import com.benefitj.core.functions.IConsumer;
import com.benefitj.core.functions.IRunnable;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IO工具
 */
public class IOUtils {

  public static final byte[] EMPTY = new byte[0];
  public static final long KB = 1024L;
  public static final long MB = 1024L * KB;
  public static final long GB = 1024L * MB;
  public static final long TB = 1024L * GB;

  /**
   * 计算KB大小
   *
   * @param len 长度
   * @return 返回对应的KB
   */
  public static double ofKB(long len) {
    return (len * 1.0) / KB;
  }

  /**
   * 计算MB大小
   *
   * @param len 长度
   * @return 返回对应的MB
   */
  public static double ofMB(long len) {
    return (len * 1.0) / MB;
  }

  /**
   * 计算GB大小
   *
   * @param len 长度
   * @return 返回对应的GB
   */
  public static double ofGB(long len) {
    return (len * 1.0) / GB;
  }

  /**
   * 计算TB大小
   *
   * @param len 长度
   * @return 返回对应的TB
   */
  public static double ofTB(long len) {
    return (len * 1.0) / TB;
  }

  /**
   * 文件数组是否不为空
   *
   * @param files 文件数组
   * @return 返回判断文件数组的结果
   */
  public static boolean isNotEmpty(File... files) {
    return files != null && files.length > 0;
  }

  /**
   * 计算文件大小
   *
   * @param file 文件
   * @return 返回计算的文件大小
   */
  public static long length(File file) {
    return length(file, null, false);
  }

  /**
   * 计算文件大小
   *
   * @param file 文件
   * @return 返回计算的文件大小
   */
  public static long length(File file, boolean multiLevel) {
    return length(file, null, multiLevel);
  }

  /**
   * 计算文件大小
   *
   * @param file       文件
   * @param multiLevel 是否包含子文件
   * @return 返回计算的文件大小
   */
  public static long length(File file, @Nullable FileFilter filter, boolean multiLevel) {
    if (file == null) return 0L;
    if (file.isDirectory()) {
      long size = 0L;
      if (multiLevel) {
        File[] files = filter != null ? file.listFiles(filter) : file.listFiles();
        if (isNotEmpty(files)) {
          for (File f : files) {
            size += length(f, filter, true);
          }
        }
      }
      return size;
    }
    return file.length();
  }

  /**
   * 列出全部的文件
   *
   * @param f 文件
   * @return 返回文件列表
   */
  public static List<File> listFiles(File f) {
    return listFiles(f, null, true);
  }

  /**
   * 列出全部的文件
   *
   * @param f      文件
   * @param filter 文件过滤器
   * @return 返回文件列表
   */
  public static List<File> listFiles(File f, @Nullable FileFilter filter) {
    return listFiles(f, filter, true);
  }

  /**
   * 列出全部的文件
   *
   * @param f          文件
   * @param filter     文件过滤器
   * @param multiLevel 是否为多层级文件
   * @return 返回文件列表
   */
  public static List<File> listFiles(File f, @Nullable FileFilter filter, boolean multiLevel) {
    if (f != null && f.isDirectory()) {
      File[] files = filter != null ? f.listFiles(filter) : f.listFiles();
      if (isNotEmpty(files)) {
        if (multiLevel) {
          return Arrays.stream(files)
              .flatMap(file -> Stream.concat(Stream.of(file), listFiles(file, filter, true).stream()))
              .collect(Collectors.toList());
        }
        return Arrays.asList(files);
      }
    }
    return Collections.emptyList();
  }

  /**
   * 创建新目录
   *
   * @param filename 文件名
   * @return 返回新创建的目录
   */
  public static File mkDirs(String filename) {
    return createFile(filename, true);
  }

  /**
   * 创建新目录
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @return 返回新创建的目录
   */
  public static File mkDirs(String parentFile, String filename) {
    return mkDirs(new File(parentFile), filename);
  }

  /**
   * 创建新目录
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @return 返回新创建的目录
   */
  public static File mkDirs(File parentFile, String filename) {
    return createFile(parentFile, filename, true);
  }

  /**
   * 创建新文件
   *
   * @param filename 文件名
   * @return 返回新创建的文件
   */
  public static File createFile(String filename) {
    return createFile(filename, false);
  }

  /**
   * 创建新文件
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @return 返回新创建的文件
   */
  public static File createFile(String parentFile, String filename) {
    return createFile(new File(parentFile), filename, false);
  }

  /**
   * 创建新文件
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @return 返回新创建的文件
   */
  public static File createFile(File parentFile, String filename) {
    return createFile(parentFile, filename, false);
  }

  /**
   * 创建新文件
   *
   * @param filename  文件名
   * @param directory 是否为目录
   * @return 返回新创建的文件
   */
  public static File createFile(String filename, boolean directory) {
    return createFile(null, filename, directory);
  }

  /**
   * 创建新文件
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @param directory  是否为目录
   * @return 返回新创建的文件
   */
  public static File createFile(File parentFile, String filename, boolean directory) {
    File newFile = parentFile != null ? new File(parentFile, filename) : new File(filename);
    if (directory) {
      newFile.mkdirs();
    } else {
      if (!newFile.exists()) {
        File pFile = newFile.getParentFile();
        if (!pFile.exists()) {
          pFile.mkdirs();
        }
        try {
          newFile.createNewFile();
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }
    return newFile;
  }


  /**
   * 获取文件大小
   *
   * @param f 文件
   * @return 返回文件长度
   */
  public static long ofFileSize(File f) {
    return f != null ? f.length() : 0L;
  }

  /**
   * 获取缓存数组
   *
   * @param buff 缓存数据的数组
   * @return 返回不为Null的字节数组
   */
  private static byte[] defaultBuffer(byte[] buff) {
    return buff != null ? buff : new byte[1024 << 4];
  }

  /**
   * 创建新的文件输入流
   *
   * @param file 文件
   * @return 返回创建的文件输入流或Null(当文件不存在时)
   */
  public static FileInputStream newFIS(File file) {
    return tryThrow(() -> new FileInputStream(file));
  }

  /**
   * 创建新的文件输出流
   *
   * @param file 文件
   * @return 返回创建的文件输出流或Null(当文件不存在时)
   */
  public static FileOutputStream newFOS(File file) {
    return newFOS(file, false);
  }

  /**
   * 创建新的文件输出流
   *
   * @param file 文件
   * @return 返回创建的文件输出流或Null(当文件不存在时)
   */
  public static FileOutputStream newFOS(File file, boolean append) {
    return tryThrow(() -> new FileOutputStream(file, append));
  }

  /**
   * 创建新的文件的InputStreamReader
   *
   * @param file    文件
   * @param charset 字符编码
   * @return 返回创建的InputStreamReader对象，或返回Null
   */
  public static InputStreamReader newISR(File file, String charset) {
    return newISR(newFIS(file), charset);
  }

  /**
   * 创建新的文件的InputStreamReader
   *
   * @param input   输入流
   * @param charset 字符编码
   * @return 返回创建的InputStreamReader对象，或返回Null
   */
  public static InputStreamReader newISR(InputStream input, String charset) {
    return StringUtils.isNotBlank(charset)
        ? tryThrow(() -> new InputStreamReader(input, charset))
        : new InputStreamReader(input);
  }

  /**
   * 创建新的BufferedReader对象
   *
   * @param file    文件
   * @param charset 字符编码
   * @return 返回创建的BufferedReader对象或Null
   */
  public static BufferedReader newBufferedReader(File file, String charset) {
    InputStreamReader isr = newISR(file, charset);
    return new BufferedReader(isr);
  }

  /**
   * 创建新的BufferedWriter对象
   *
   * @param file 文件
   * @return 返回创建的BufferedWriter对象或Null
   */
  public static BufferedWriter newBufferedWriter(File file) {
    return tryThrow(() -> new BufferedWriter(new FileWriter(file)));
  }

  /**
   * 包装成BufferedReader
   *
   * @param in 输入流
   * @param charset 编码
   * @return 返回 BufferedReader
   */
  public static BufferedReader wrapReader(InputStream in, Charset charset) {
    return wrapReader(new InputStreamReader(in, charset  != null ? charset :  Charset.defaultCharset()));
  }

  public static BufferedReader wrapReader(Reader reader) {
    return reader instanceof BufferedReader
        ? (BufferedReader) reader
        : new BufferedReader(reader);
  }

  public static BufferedWriter wrapWriter(Writer writer) {
    return writer instanceof BufferedWriter
        ? (BufferedWriter) writer
        : new BufferedWriter(writer);
  }

  /**
   * 读取数据，每次读取指定长度的字节，默认闭关流
   *
   * @param file     文件
   * @param consumer 处理回调
   */
  public static void read(File file, IBiConsumer<byte[], Integer> consumer) {
    read(file, 1024 << 4, consumer);
  }

  /**
   * 读取数据，每次读取指定长度的字节，默认闭关流
   *
   * @param is       输入流
   * @param size     缓冲区大小
   * @param consumer 处理回调
   */
  public static void read(InputStream is, int size, IBiConsumer<byte[], Integer> consumer) {
    read(is, size, true, consumer);
  }

  /**
   * 读取数据，每次读取指定长度的字节
   *
   * @param is       输入流
   * @param size     缓冲区大小
   * @param close    是否关闭流
   * @param consumer 处理回调
   */
  public static void read(InputStream is, int size, boolean close, IBiConsumer<byte[], Integer> consumer) {
    try {
      byte[] buff = new byte[size];
      int len;
      while ((len = is.read(buff)) > 0) {
        consumer.accept(buff, len);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      if (close) {
        closeQuietly(is);
      }
    }
  }

  /**
   * 读取数据
   *
   * @param file     文件
   * @param size     读取的长度
   * @param consumer 处理数据
   */
  public static void read(final File file, int size, IBiConsumer<byte[], Integer> consumer) {
    read(file, size, buf -> true, consumer, buf -> false);
  }


  /**
   * 读取数据
   *
   * @param file        文件
   * @param size        读取的长度
   * @param filter      过滤出匹配的数据
   * @param consumer    处理数据
   * @param interceptor 是否停止读取
   */
  public static void read(final File file,
                          int size,
                          Predicate<byte[]> filter,
                          IBiConsumer<byte[], Integer> consumer,
                          Predicate<byte[]> interceptor) {
    try (final RandomAccessFile raf = new RandomAccessFile(file, "r");) {
      read(raf, size, filter, consumer, interceptor);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 读取数据
   *
   * @param raf         输入流
   * @param size        读取的长度
   * @param filter      过滤出匹配的数据
   * @param consumer    处理数据
   * @param interceptor 是否停止读取
   */
  public static void read(final RandomAccessFile raf,
                          int size,
                          Predicate<byte[]> filter,
                          IBiConsumer<byte[], Integer> consumer,
                          Predicate<byte[]> interceptor) {
    try {
      byte[] buf = new byte[size];
      int len;
      while ((len = raf.read(buf)) > 0) {
        if (filter.test(buf)) {
          consumer.accept(buf, len);
        }
        if (interceptor.test(buf)) {
          return;
        }
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param file     文件
   * @param consumer 处理回调
   */
  public static void readLine(File file, IConsumer<String> consumer) {
    try (final FileReader reader = new FileReader(file);) {
      readLine(reader, false, consumer);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param reader   输入
   * @param consumer 处理回调
   */
  public static void readLine(Reader reader, IConsumer<String> consumer) {
    readLine(reader, true, consumer);
  }

  /**
   * 读取数据，每次读取一行
   *
   * @param reader   输入
   * @param close    是否关闭流
   * @param consumer 处理回调
   */
  public static void readLine(Reader reader, boolean close, IConsumer<String> consumer) {
    final BufferedReader br = wrapReader(reader);
    try {
      String line;
      while ((line = br.readLine()) != null) {
        consumer.accept(line);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      if (close) {
        closeQuietly(br);
      }
    }
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param reader   输入
   */
  public static List<String> readLines(Reader reader) {
    List<String> lines = new LinkedList<>();
    readLine(reader, lines::add);
    return lines;
  }

  /**
   * 读取数据
   *
   * @param is 输入流
   * @return 返回读取的内存数据流
   */
  public static ByteArrayOutputStream readFully(InputStream is) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    read(is, 1024 << 4, (buff, len) -> baos.write(buff, 0, len));
    return baos;
  }

  /**
   * 读取文件, 如果文件超过一定大小就抛出异常
   *
   * @param file 要读取的文件
   * @return 返回读取的字节数组
   * @throws IllegalStateException
   */
  public static byte[] readFileFully(File file) {
    return readFileFully(file, 1024 << 4);
  }

  /**
   * 读取文件, 如果文件超过一定大小就抛出异常
   *
   * @param file 要读取的文件
   * @return 返回读取的字节数组
   * @throws IllegalArgumentException
   */
  public static byte[] readFileFully(File file, long maxSize) {
    if (exists(file)) {
      requireNotOutOfSize(file.length(), maxSize);
      final FileInputStream fis = newFIS(file);
      try {
        return readFully(fis).toByteArray();
      } finally {
        closeQuietly(fis);
      }
    }
    return EMPTY;
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param file 文件
   * @return 返回数据行的集合
   */
  public static List<String> readFileLines(File file) {
    return readFileLines(file, s -> true, null);
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param file    文件
   * @param charset 字符编码
   * @return 返回数据行的集合
   */
  public static List<String> readFileLines(File file, String charset) {
    return readFileLines(file, s -> true, charset);
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param file 文件
   * @return 返回数据行的集合
   */
  public static List<String> readFileLines(File file, Predicate<String> predicate, String charset) {
    return readFileLines(file, predicate, charset, 1024 << 4);
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param file      文件
   * @param predicate 过滤规则
   * @param charset   字符编码
   * @return 返回数据行的集合
   */
  public static List<String> readFileLines(File file, Predicate<String> predicate, String charset, long maxSize) {
    if (isFile(file)) {
      requireNotOutOfSize(file.length(), maxSize);
      BufferedReader reader = newBufferedReader(file, charset);
      try {
        return reader.lines()
            .filter(predicate)
            .collect(Collectors.toList());
      } finally {
        closeQuietly(reader);
      }
    }
    return Collections.emptyList();
  }

  /**
   * 写入数据
   *
   * @param is   输入流
   * @param file 文件
   */
  public static void write(InputStream is, File file, boolean close) {
    write(is, file, false, close);
  }

  /**
   * 写入数据
   *
   * @param file 文件
   * @param os   输出流
   */
  public static void write(File file, OutputStream os) {
    write(newFIS(file), os, 1024 << 4);
  }

  /**
   * 写入数据
   *
   * @param in    输入流
   * @param out   输出流
   * @param close 是否关闭流
   */
  public static void write(File in, OutputStream out, boolean close) {
    try (final FileInputStream fis = new FileInputStream(in)) {
      write(fis, out, 1024 << 4, close);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 写入数据
   *
   * @param is     输入流
   * @param file   文件
   * @param append 是否为追加
   * @param close  是否关闭流
   */
  public static void write(InputStream is, File file, boolean append, boolean close) {
    final FileOutputStream fos = newFOS(file, append);
    try {
      write(is, fos, 1024 << 4, close);
    } finally {
      closeQuietly(fos);
      if (close) {
        closeQuietly(is);
      }
    }
  }

  /**
   * 写入数据
   *
   * @param is 输入流
   * @param os 输出流
   */
  public static void write(InputStream is, OutputStream os) {
    write(is, os, 1024 << 4);
  }

  /**
   * 写入数据
   *
   * @param is   输入流
   * @param os   输出流
   * @param size 缓存大小
   */
  public static void write(InputStream is, OutputStream os, int size) {
    write(is, os, size, true);
  }

  /**
   * 写入数据
   *
   * @param is   输入流
   * @param os   输出流
   * @param size 缓存大小
   */
  public static void write(InputStream is, OutputStream os, int size, boolean close) {
    try {
      byte[] buff = new byte[size];
      int len;
      while ((len = is.read(buff)) > 0) {
        os.write(buff, 0, len);
        os.flush();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (close) {
        closeQuietly(is, os);
      }
    }
  }

  /**
   * 写入数据
   *
   * @param os    输出流
   * @param lines 字符串数据
   */
  public static void write(OutputStream os, String... lines) {
    for (String line : lines) {
      byte[] buff = line.getBytes(StandardCharsets.UTF_8);
      write(os, buff, 0, buff.length);
    }
  }

  /**
   * 写入数据
   *
   * @param os    输出流
   * @param array 字节缓冲数组
   */
  public static void write(OutputStream os, byte[]... array) {
    for (byte[] bytes : array) {
      write(os, bytes, 0, array.length);
    }
  }

  /**
   * 写入数据
   *
   * @param os   输出流
   * @param buff 字节缓冲
   */
  public static void write(OutputStream os, byte[] buff) {
    write(os, buff, 0, buff.length);
  }

  /**
   * 写入数据
   *
   * @param os    输出流
   * @param buff  字节缓冲
   * @param start 开始的位置
   * @param len   结束的位置
   */
  public static void write(OutputStream os, byte[] buff, int start, int len) {
    try {
      os.write(buff, start, len);
      os.flush();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 写入数据
   *
   * @param reader 输入流
   * @param writer 输出流
   */
  public static void writeLine(Reader reader, Writer writer) {
    writeLine(reader, writer, true);
  }

  /**
   * 写入数据
   *
   * @param reader 输入流
   * @param writer 输出流
   * @param close  是否要关闭流
   */
  public static void writeLine(Reader reader, Writer writer, boolean close) {
    final BufferedReader bw = wrapReader(reader);
    try {
      String line;
      while ((line = bw.readLine()) != null) {
        writer.write(line);
        writer.flush();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (close) {
        closeQuietly(reader, writer);
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
  public static boolean exists(File f) {
    return f != null && f.exists();
  }

  /**
   * 关闭全部
   *
   * @param closes AutoCloseable实现(InputStream、OutputStream)
   */
  public static void close(AutoCloseable... closes) {
    if (closes != null && closes.length > 0) {
      for (AutoCloseable c : closes) {
        try {
          c.close();
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  /**
   * 关闭全部
   *
   * @param closes AutoCloseable实现(InputStream、OutputStream)
   */
  public static void closeQuietly(AutoCloseable... closes) {
    if (closes != null && closes.length > 0) {
      for (AutoCloseable c : closes) {
        try {
          c.close();
        } catch (Exception e) {/* ignore */}
      }
    }
  }

  /**
   * 删除文件
   *
   * @param fs 文件数组
   */
  public static void deleteFiles(File... fs) {
    if (fs != null && fs.length > 0) {
      deleteFiles(fs, false);
    }
  }

  /**
   * 删除文件
   *
   * @param fs 文件数组
   */
  public static void deleteFiles(File[] fs, boolean clear) {
    deleteFiles(fs != null ? Arrays.asList(fs) : null, clear);
  }

  /**
   * 删除文件
   *
   * @param fs 文件数组
   */
  public static void deleteFiles(Collection<File> fs, boolean clear) {
    if (fs != null && !fs.isEmpty()) {
      for (File f : fs) {
        deleteFile(f, clear);
      }
    }
  }

  /**
   * 删除文件和目录
   *
   * @param f 文件
   */
  public static void deleteFile(File f) {
    deleteFile(f, false);
  }

  /**
   * 删除文件和目录，如果删除失败，可以选择清空文件
   *
   * @param f     文件
   * @param clear 是否清空文件
   */
  public static void deleteFile(File f, boolean clear) {
    if (f != null) {
      if (f.isDirectory()) {
        deleteFiles(f.listFiles(), clear);
        f.delete();
      } else {
        boolean delete = f.delete();
        if (!delete && clear) {
          // 删除失败，将文件清空
          clearFile(f);
        }
      }
    }
  }

  /**
   * 清空文件
   *
   * @param fs 文件数组
   */
  public static void clearFiles(File... fs) {
    if (fs != null && fs.length > 0) {
      for (File f : fs) {
        if (f.isDirectory()) {
          clearFiles(f.listFiles());
        } else {
          clearFile(f);
        }
      }
    }
  }

  /**
   * 清空文件
   *
   * @param f 文件
   */
  public static void clearFile(File f) {
    if (f != null && f.exists()) {
      if (f.isDirectory()) {
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
          for (File file : files) {
            clearFile(file);
          }
        }
      } else {
        if (f.length() > 0) {
          // 重置数据
          try (final FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(new byte[0]);
            fos.flush();
          } catch (IOException ignore) {/*...*/}
        }
      }
    }
  }

  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call) {
    try {
      return call.call();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static void tryThrow(IRunnable r) {
    try {
      r.run();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 要求不超过一定大小
   *
   * @param length  数据的长度
   * @param maxSize 最大的长度
   */
  public static void requireNotOutOfSize(long length, long maxSize) {
    if (length > maxSize) {
      double max;
      double size;
      String unit;
      if (length >= GB) {
        size = ofGB(length);
        max = ofGB(maxSize);
        unit = "GB";
      } else if (length >= MB) {
        size = ofMB(length);
        max = ofMB(maxSize);
        unit = "MB";
      } else {
        size = ofKB(length);
        max = ofKB(maxSize);
        unit = "KB";
      }
      String errMsg = String.format("数据不能超过%.2f%s, 当前数据长度为%.2f%s", max, unit, size, unit);
      throw new IllegalArgumentException(errMsg);
    }
  }

}
