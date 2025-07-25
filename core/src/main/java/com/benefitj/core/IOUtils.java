package com.benefitj.core;

import com.benefitj.core.functions.IBiConsumer;

import javax.annotation.Nullable;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
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
   * 取小数点后几位
   *
   * @param v 数值
   * @return 返回对应的MB
   */
  public static String fmtSize(long v) {
    return fmtSize(v, 3, RoundingMode.HALF_UP);
  }

  /**
   * 取小数点后几位
   *
   * @param v     数值
   * @param scale 保留的小数点位数
   * @param mode  取整模式，默认为四舍五入
   * @return 返回对应的MB
   */
  public static String fmtSize(long v, int scale, RoundingMode mode) {
    if (v >= TB) return ofTB(v, scale, mode) + "TB";
    else if (v >= GB) return ofGB(v, scale, mode) + "GB";
    else if (v >= MB) return ofMB(v, scale, mode) + "MB";
    else if (v >= KB) return ofKB(v, scale, mode) + "KB";
    else return v + "B";
  }

  /**
   * 取小数点后几位
   *
   * @param v     数值
   * @param scale 保留的小数点位数
   * @param mode  取整模式，默认为四舍五入
   * @return 返回对应的MB
   */
  public static double scale(double v, int scale, RoundingMode mode) {
    return new BigDecimal(v).setScale(scale, mode).doubleValue();
  }

  /**
   * 计算KB大小
   *
   * @param len 长度
   * @return 返回对应的KB
   */
  public static double ofKB(long len) {
    return ofKB(len, 3, RoundingMode.HALF_UP);
  }

  /**
   * 计算KB大小
   *
   * @param len   长度
   * @param scale 保留的小数点位数
   * @param mode  取整模式，默认为四舍五入
   * @return 返回对应的KB
   */
  public static double ofKB(long len, int scale, RoundingMode mode) {
    return scale(len * 1.0 / KB, scale, mode);
  }

  /**
   * 计算MB大小
   *
   * @param len 长度
   * @return 返回对应的MB
   */
  public static double ofMB(long len) {
    return ofMB(len, 3, RoundingMode.HALF_UP);
  }

  /**
   * 计算MB大小
   *
   * @param len   长度
   * @param scale 保留的小数点位数
   * @param mode  取整模式，默认为四舍五入
   * @return 返回对应的MB
   */
  public static double ofMB(long len, int scale, RoundingMode mode) {
    return scale(len * 1.0 / MB, scale, mode);
  }

  /**
   * 计算GB大小
   *
   * @param len 长度
   * @return 返回对应的GB
   */
  public static double ofGB(long len) {
    return ofGB(len, 3, RoundingMode.HALF_UP);
  }

  /**
   * 计算GB大小
   *
   * @param len   长度
   * @param scale 保留的小数点位数
   * @param mode  取整模式，默认为四舍五入
   * @return 返回对应的GB
   */
  public static double ofGB(long len, int scale, RoundingMode mode) {
    return scale(len * 1.0 / GB, scale, mode);
  }

  /**
   * 计算TB大小
   *
   * @param len 长度
   * @return 返回对应的TB
   */
  public static double ofTB(long len) {
    return ofTB(len, 3, RoundingMode.HALF_UP);
  }

  /**
   * 计算TB大小
   *
   * @param len   长度
   * @param scale 保留的小数点位数
   * @param mode  取整模式，默认为四舍五入
   * @return 返回对应的TB
   */
  public static double ofTB(long len, int scale, RoundingMode mode) {
    return scale(len * 1.0 / TB, scale, mode);
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
   * 获取文件的行数
   *
   * @param f 文件
   * @return 返回读取的文件行数
   */
  public static int getLineNumber(File f) {
    try (final LineNumberReader lnr = new LineNumberReader(new FileReader(f))) {
      lnr.skip(Long.MAX_VALUE);
      return lnr.getLineNumber() + 1;
    } catch (IOException e) {
      return -1;
    }
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
   * @param files 文件
   * @return 返回计算的文件大小
   */
  public static long length(File[] files) {
    return length(files, null, false);
  }

  /**
   * 计算文件大小
   *
   * @param files      文件
   * @param filter     过滤器
   * @param multiLevel 是否包含子文件
   * @return 返回计算的文件大小
   */
  public static long length(File[] files, FileFilter filter, boolean multiLevel) {
    long length = 0;
    for (File file : files) {
      length += length(file, filter, multiLevel);
    }
    return length;
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
    if (file != null) {
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
    return 0L;
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
   * @param filename 文件名
   * @return 返回新创建的目录
   */
  public static File mkDirs(File filename) {
    return createFile(filename.getAbsolutePath(), true);
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
   * @param file 文件
   * @return 返回新创建的文件
   */
  public static File createFile(File file) {
    return createFile(file.getAbsolutePath());
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
          throw CatchUtils.throwing(e, IllegalStateException.class);
        }
      }
    }
    return newFile;
  }

  /**
   * 创建新的文件输入流
   *
   * @param file 文件
   * @return 返回创建的文件输入流或Null(当文件不存在时)
   */
  public static FileInputStream newFIS(File file) {
    try {
      return new FileInputStream(file);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
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
    try {
      return new FileOutputStream(file, append);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 创建新的文件的InputStreamReader
   *
   * @param file    文件
   * @param charset 字符编码
   * @return 返回创建的InputStreamReader对象，或返回Null
   */
  public static InputStreamReader newISR(File file, Charset charset) {
    return newISR(newFIS(file), charset);
  }

  /**
   * 创建新的文件的InputStreamReader
   *
   * @param in      输入流
   * @param charset 字符编码
   * @return 返回创建的InputStreamReader对象，或返回Null
   */
  public static InputStreamReader newISR(InputStream in, Charset charset) {
    return new InputStreamReader(in, charset != null ? charset : StandardCharsets.UTF_8);
  }

  /**
   * 创建新的BufferedReader对象
   *
   * @param file    文件
   * @param charset 字符编码
   * @return 返回创建的BufferedReader对象或Null
   */
  public static BufferedReader newBufferedReader(File file, Charset charset) {
    return new BufferedReader(newISR(file, charset));
  }

  /**
   * 创建新的BufferedWriter对象
   *
   * @param file 文件
   * @return 返回创建的BufferedWriter对象或Null
   */
  public static BufferedWriter newBufferedWriter(File file) {
    return newBufferedWriter(file, StandardCharsets.UTF_8);
  }

  /**
   * 创建新的BufferedWriter对象
   *
   * @param file 文件
   * @return 返回创建的BufferedWriter对象或Null
   */
  public static BufferedWriter newBufferedWriter(File file, Charset charset) {
    return wrapWriter(newFOS(file), charset);
  }

  /**
   * 包装成BufferedReader
   *
   * @param in      输入流
   * @param charset 编码
   * @return 返回 BufferedReader
   */
  public static BufferedReader wrapReader(InputStream in, Charset charset) {
    return wrapReader(new InputStreamReader(in, charset != null ? charset : Charset.defaultCharset()));
  }

  public static BufferedReader wrapReader(Reader reader) {
    return reader instanceof BufferedReader
        ? (BufferedReader) reader
        : new BufferedReader(reader);
  }

  public static BufferedWriter wrapWriter(OutputStream out, Charset charset) {
    return wrapWriter(new OutputStreamWriter(out, charset));
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
      byte[] buf = new byte[size];
      int len;
      while ((len = is.read(buf)) > 0) {
        consumer.accept(buf, len);
      }
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
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
      throw CatchUtils.throwing(e, IllegalStateException.class);
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
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param in       文件
   * @param consumer 处理回调
   */
  public static void readLines(File in, IBiConsumer<String, Integer> consumer) {
    readLines(in, Charset.defaultCharset(), consumer);
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param in       文件
   * @param charset  编码
   * @param consumer 处理回调
   */
  public static void readLines(File in, Charset charset, IBiConsumer<String, Integer> consumer) {
    try (final Reader reader = new InputStreamReader(new FileInputStream(in), charset);) {
      readLines(reader, false, consumer);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param reader   输入
   * @param consumer 处理回调
   */
  public static void readLines(Reader reader, IBiConsumer<String, Integer> consumer) {
    readLines(reader, true, consumer);
  }

  /**
   * 读取数据，每次读取一行
   *
   * @param reader   输入
   * @param close    是否关闭流
   * @param consumer 处理回调
   */
  public static void readLines(Reader reader, boolean close, IBiConsumer<String, Integer> consumer) {
    final BufferedReader br = wrapReader(reader);
    try {
      String line;
      int index = 0;
      while ((line = br.readLine()) != null) {
        consumer.accept(line, index);
        index++;
      }
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      if (close) {
        closeQuietly(br);
      }
    }
  }

  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param reader 输入
   */
  public static List<String> readLines(Reader reader) {
    return readLines(reader, (line, lineNumber) -> true);
  }


  /**
   * 读取数据，每次读取一行，默认关闭流
   *
   * @param reader 输入
   */
  public static List<String> readLines(Reader reader, BiPredicate<String, Integer> filter) {
    List<String> lines = new LinkedList<>();
    readLines(reader, (line, lineNumber) -> {
      if(filter.test(line, lineNumber))
        lines.add(line);
    });
    return lines;
  }

  /**
   * 读取数据
   *
   * @param is 输入流
   * @return 返回读取的内存数据流
   */
  public static ByteArrayOutputStream readFully(File is) {
    return readFully(newFIS(is));
  }

  /**
   * 读取数据
   *
   * @param is 输入流
   * @return 返回读取的内存数据流
   */
  public static ByteArrayOutputStream readFully(InputStream is) {
    return readFully(is, true);
  }

  /**
   * 读取数据
   *
   * @param is    输入流
   * @param close 是否关闭输入流
   * @return 返回读取的内存数据流
   */
  public static ByteArrayOutputStream readFully(InputStream is, boolean close) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    read(is, 1024 << 4, close, (buf, len) -> baos.write(buf, 0, len));
    return baos;
  }

  /**
   * 读取流
   *
   * @param in 输入流
   * @return 返回读取的字节数组
   */
  public static byte[] readAsBytes(InputStream in) {
    return readAsBytes(in, true);
  }

  /**
   * 读取流
   *
   * @param in    输入流
   * @param close 是否自动关闭
   * @return 返回读取的字节数组
   */
  public static byte[] readAsBytes(InputStream in, boolean close) {
    try {
      return readFully(in).toByteArray();
    } finally {
      if (close) {
        closeQuietly(in);
      }
    }
  }

  /**
   * 读取文件
   *
   * @param in 要读取的文件
   * @return 返回读取的字节数组
   */
  public static byte[] readAsBytes(File in) {
    return in.length() > 0 ? readAsBytes(newFIS(in), true) : EMPTY;
  }

  /**
   * 读取文件
   *
   * @param in      要读取的文件
   * @param charset 编码类型
   * @return 返回读取的字符串
   */
  public static String readAsString(InputStream in, Charset charset) {
    ByteArrayOutputStream baos = readFully(in);
    return new String(baos.toByteArray(), charset);
  }

  /**
   * 读取文件
   *
   * @param in 要读取的文件
   * @return 返回读取的字符串
   */
  public static String readAsString(File in) {
    return readAsString(in, StandardCharsets.UTF_8);
  }

  /**
   * 读取文件
   *
   * @param in      要读取的文件
   * @param charset 字符集
   * @return 返回读取的字符串
   */
  public static String readAsString(File in, Charset charset) {
    if (exists(in)) {
      final FileInputStream fis = newFIS(in);
      try {
        ByteArrayOutputStream baos = readFully(fis);
        return new String(baos.toByteArray(), charset);
      } finally {
        closeQuietly(fis);
      }
    }
    return "";
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param in 文件
   * @return 返回数据行的集合
   */
  public static List<String> readLines(File in) {
    return readLines(in, s -> true, StandardCharsets.UTF_8);
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param in      文件
   * @param charset 字符编码
   * @return 返回数据行的集合
   */
  public static List<String> readLines(File in, Charset charset) {
    return readLines(in, str -> true, charset);
  }

  /**
   * 读取文件中数据的每一行
   *
   * @param in        文件
   * @param predicate 过滤规则
   * @param charset   字符编码
   * @return 返回数据行的集合
   */
  public static List<String> readLines(File in, Predicate<String> predicate, Charset charset) {
    if (isFile(in)) {
      BufferedReader reader = newBufferedReader(in, charset);
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
   * 读取文件
   *
   * @param in       输入
   * @param consumer 处理
   */
  public static void readFileBytes(File in, int size, BiConsumer<byte[], Integer> consumer) {
    readFileBytes(in, size, (buf, len) -> true, consumer, (buf, len) -> false);
  }

  /**
   * 读取文件
   *
   * @param in        输入
   * @param filter    过滤规则
   * @param consumer  处理
   * @param intercept 拦截
   */
  public static void readFileBytes(File in,
                                   int size,
                                   BiPredicate<byte[], Integer> filter,
                                   BiConsumer<byte[], Integer> consumer,
                                   BiPredicate<byte[], Integer> intercept) {
    try (final FileInputStream fis = newFIS(in)) {
      readBytes(fis, size, filter, consumer, intercept);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 读取数据
   *
   * @param in       输入
   * @param consumer 处理
   */
  public static void readBytes(InputStream in, int size, BiConsumer<byte[], Integer> consumer) {
    readBytes(in, size, (buf, len) -> true, consumer, (buf, len) -> false);
  }

  /**
   * 读取数据
   *
   * @param in        输入
   * @param filter    过滤规则
   * @param consumer  处理
   * @param intercept 拦截
   */
  public static void readBytes(InputStream in,
                               int size,
                               BiPredicate<byte[], Integer> filter,
                               BiConsumer<byte[], Integer> consumer,
                               BiPredicate<byte[], Integer> intercept) {
    try {
      byte[] buf = new byte[size];
      int len;
      while ((len = in.read(buf)) > 0) {
        if (filter.test(buf, len)) {
          consumer.accept(buf, len);
        }
        if (intercept.test(buf, len)) {
          return;
        }
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 写入数据
   *
   * @param is  输入流
   * @param out 输出到的文件
   * @return 返回写入的长度
   */
  public static long write(InputStream is, File out) {
    return write(is, out, true);
  }

  /**
   * 写入数据
   *
   * @param is    输入流
   * @param out   输出到的文件
   * @param close 是否关闭输入流
   * @return 返回写入的长度
   */
  public static long write(InputStream is, File out, boolean close) {
    return write(is, out, false, close);
  }

  /**
   * 写入数据
   *
   * @param out 输出流
   * @param in  输出到的文件
   * @return 返回写入的长度
   */
  public static long write(File in, OutputStream out) {
    return write(newFIS(in), out, 1024 << 4);
  }

  /**
   * 写入数据
   *
   * @param in    输入流
   * @param out   输出流
   * @param close 是否关闭流
   * @return 返回写入的长度
   */
  public static long write(File in, OutputStream out, boolean close) {
    try (final FileInputStream fis = new FileInputStream(in)) {
      return write(fis, out, 1024 << 4, close);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 写入数据
   *
   * @param is     输入流
   * @param out    输出的文件
   * @param append 是否为追加
   * @param close  是否关闭流
   * @return 返回写入的长度
   */
  public static long write(InputStream is, File out, boolean append, boolean close) {
    final FileOutputStream fos = newFOS(out, append);
    try {
      return write(is, fos, 1024 << 4, close);
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
   * @param in  字符串数据
   * @param out 输出流
   */
  public static void write(String in, File out) {
    write(in.getBytes(StandardCharsets.UTF_8), out);
  }

  /**
   * 写入数据
   *
   * @param in  字符串数据
   * @param out 输出流
   */
  public static void write(String in, OutputStream out) {
    write(Collections.singletonList(in), out);
  }

  /**
   * 写入数据
   *
   * @param in  字符串数据
   * @param out 输出流
   */
  public static void write(List<String> in, OutputStream out) {
    for (int i = 0; i < in.size(); i++) {
      String line = in.get(i);
      byte[] ba = line.getBytes(StandardCharsets.UTF_8);
      write(ba, 0, ba.length, out, in.size() - 1 == i);
    }
  }

  /**
   * 写入数据
   *
   * @param in  字节缓冲
   * @param out 输出流
   */
  public static void write(byte[] in, OutputStream out) {
    write(in, 0, in.length, out, true);
  }

  /**
   * 写入数据
   *
   * @param in  字节缓冲
   * @param out 输出流
   */
  public static void write(byte[] in, OutputStream out, boolean close) {
    write(in, 0, in.length, out, close);
  }

  /**
   * 写入数据
   *
   * @param in  字节缓冲
   * @param out 输出流
   */
  public static void write(byte[] in, int start, int len, OutputStream out, boolean close) {
    try {
      writeAndFlush(in, start, len, out);
    } finally {
      if (close) {
        closeQuietly(out);
      }
    }
  }

  /**
   * 写入数据
   *
   * @param in  数据
   * @param out 输出的文件
   */
  public static void write(byte[] in, File out) {
    write(in, out, false);
  }

  /**
   * 写入数据
   *
   * @param in     数据
   * @param out    输出的文件
   * @param append 是否为追加
   */
  public static void write(byte[] in, File out, boolean append) {
    write(in, 0, in.length, out, append);
  }

  /**
   * 写入数据
   *
   * @param in     数据
   * @param start  开始的位置
   * @param len    长度
   * @param out    输出的文件
   * @param append 是否追加
   */
  public static void write(byte[] in, int start, int len, File out, boolean append) {
    try (final FileOutputStream fos = newFOS(out, append)) {
      writeAndFlush(in, start, len, fos);
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 写入数据
   *
   * @param is  输入流
   * @param out 输出流
   * @return 返回写入的长度
   */
  public static long write(InputStream is, OutputStream out) {
    return write(is, out, 1024 << 4);
  }

  /**
   * 写入数据
   *
   * @param is   输入流
   * @param out  输出流
   * @param size 缓存大小
   * @return 返回写入的长度
   */
  public static long write(InputStream is, OutputStream out, int size) {
    return write(is, out, size, true);
  }

  /**
   * 写入数据
   *
   * @param is   输入流
   * @param out  输出流
   * @param size 缓存大小
   * @return 返回写入的长度
   */
  public static long write(InputStream is, OutputStream out, int size, boolean close) {
    try {
      long totalLength = 0;
      byte[] buf = new byte[size];
      int len;
      while ((len = is.read(buf)) > 0) {
        writeAndFlush(buf, 0, len, out);
        totalLength += len;
      }
      return totalLength;
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      if (close) {
        closeQuietly(is, out);
      }
    }
  }

  /**
   * 写入数据
   *
   * @param in    字节缓冲
   * @param start 开始的位置
   * @param len   结束的位置
   * @param out   输出流
   */
  public static void writeAndFlush(byte[] in, int start, int len, OutputStream out) {
    try {
      out.write(in, start, len);
      out.flush();
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 写入数据
   *
   * @param in  输入流
   * @param out 输出流
   */
  public static void writeLine(Reader in, Writer out) {
    writeLine(in, out, true);
  }

  /**
   * 写入数据
   *
   * @param in    输入流
   * @param out   输出流
   * @param close 是否要关闭流
   */
  public static void writeLine(Reader in, Writer out, boolean close) {
    final BufferedReader bw = wrapReader(in);
    try {
      String line;
      while ((line = bw.readLine()) != null) {
        out.write(line);
        out.flush();
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      if (close) {
        closeQuietly(in, out);
      }
    }
  }

  /**
   * 将多个文件写入到一个文件
   *
   * @param src    源文件
   * @param dest   目标文件
   * @param delete 是否删除
   * @return 返回目标文件
   */
  public static File aio(File[] src, File dest, boolean delete) {
    return aio(src, dest, delete, NOTHING);
  }

  /**
   * 将多个文件写入到一个文件
   *
   * @param src      源文件
   * @param dest     目标文件
   * @param delete   是否删除
   * @param progress 进度监听
   * @return 返回目标文件
   */
  public static File aio(File[] src, File dest, boolean delete, ProgressConsumer progress) {
    try (final FileOutputStream out = new FileOutputStream(dest, true);) {
      byte[] buf = new byte[1024 << 4];
      int len;
      for (File f : src) {
        try (final FileInputStream fis = new FileInputStream(f)) {
          while ((len = fis.read(buf)) > 0) {
            out.write(buf, 0, len);
            progress.accept(f, buf, len);
          }
          out.flush();
        }
      }
      if (delete) delete(src); // 删除
      return dest;
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 获取文件后缀
   * @param filename 文件名
   * @return 返回后缀
   */
  public static String getFileSuffix(String filename) {
    return filename.lastIndexOf(".") >= 0 ? filename.substring(filename.lastIndexOf(".") + 1) : "";
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
   * 清空缓存区
   */
  public static void flush(Flushable target) {
    try {
      target.flush();
    } catch (IOException e) {/*^_^*/}
  }

  /**
   * 刷新缓存，并关闭
   */
  public static void flushAndClose(Object target) {
    if (target instanceof Flushable) flush((Flushable) target);
    if (target instanceof AutoCloseable) closeQuietly((AutoCloseable) target);
  }

  /**
   * 关闭全部
   *
   * @param closes AutoCloseable实现(InputStream、OutputStream)
   */
  public static void close(AutoCloseable... closes) {
    if (closes != null) {
      for (AutoCloseable c : closes) {
        try {
          c.close();
        } catch (Exception e) {
          throw CatchUtils.throwing(e, IllegalStateException.class);
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
    if (closes != null) {
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
   * @param f     文件
   * @param clear 是否清空数据
   */
  public static void delete(File f, boolean clear) {
    delete(new File[]{f}, clear);
  }

  /**
   * 删除文件
   *
   * @param fs 文件数组
   */
  public static void delete(File... fs) {
    if (fs != null && fs.length > 0) {
      delete(fs, false);
    }
  }

  /**
   * 删除文件
   *
   * @param fs 文件数组
   */
  public static void delete(File[] fs, boolean clear) {
    delete(fs != null ? Arrays.asList(fs) : null, clear);
  }

  /**
   * 删除文件
   *
   * @param fs 文件数组
   */
  public static void delete(Collection<File> fs, boolean clear) {
    if (fs != null && !fs.isEmpty()) {
      for (File f : fs) {
        delete0(f, clear);
      }
    }
  }

  /**
   * 删除文件和目录，如果删除失败，可以选择清空文件
   *
   * @param f     文件
   * @param clear 是否清空文件
   */
  static void delete0(File f, boolean clear) {
    if (f != null) {
      if (f.isDirectory()) {
        delete(f.listFiles(), clear);
        f.delete();
      } else {
        boolean delete = f.delete();
        if (!delete && clear) {
          // 删除失败，将文件清空
          clearFiles(f);
        }
      }
    }
  }

  /**
   * 清空文件，但不删除
   *
   * @param fs 文件数组
   */
  public static void clearFiles(File... fs) {
    clearFiles(fs, true);
  }

  /**
   * 清空文件，但不删除
   *
   * @param fs       文件数组
   * @param subFiles 是否包含子文件
   */
  public static void clearFiles(File[] fs, boolean subFiles) {
    if (fs != null && fs.length > 0) {
      for (File f : fs) {
        if (f != null && f.exists()) {
          if (f.isDirectory()) {
            if (subFiles) {
              clearFiles(f.listFiles(), true);
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
    }
  }

  /**
   * 处理文件
   *
   * @param file     文件数据
   * @param consumer 处理者
   * @param listener 进度监听
   */
  public static void process(File file, ProgressConsumer consumer, ProgressListener listener) {
    process(new File[]{file}, consumer, listener);
  }

  /**
   * 处理文件
   *
   * @param files    文件数据
   * @param consumer 处理者
   * @param listener 进度监听
   */
  public static void process(File[] files, ProgressConsumer consumer, ProgressListener listener) {
    try {
      // 总长度
      long totalLength = length(files);
      // 进度
      long totalProgress = 0;
      for (File f : files) {
        try (final FileInputStream fis = new FileInputStream(f);) {
          // 新文件
          listener.onProgressRefresh(f, totalLength, totalProgress, 0);
          int len;
          byte[] buf = new byte[1024 << 4];
          long progress = 0;
          while ((len = fis.read(buf)) > 0) {
            progress += len;
            totalProgress += len;
            consumer.accept(f, buf, len);
            // 每次写入后刷新
            listener.onProgressRefresh(f, totalLength, totalProgress, progress);
          }
        }
      }
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 进度监听
   */
  public interface ProgressListener {

    /**
     * 刷新
     *
     * @param source        文件
     * @param totalLength   总长度
     * @param totalProgress 总进度
     * @param progress      进度
     */
    void onProgressRefresh(File source, long totalLength, long totalProgress, long progress);

  }

  @FunctionalInterface
  public interface ProgressConsumer {

    /**
     * 接收数据
     *
     * @param source 源文件
     * @param buf    读取的字节缓冲
     * @param len    数据的长度
     * @throws Exception
     */
    void accept(File source, byte[] buf, int len) throws Exception;

  }

  static final ProgressConsumer NOTHING = (source, buf, len) -> {/*^_^*/};

}
