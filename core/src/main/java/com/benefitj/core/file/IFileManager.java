package com.benefitj.core.file;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * 文件管理
 */
public interface IFileManager {

  /**
   * 设置目录
   *
   * @param rootFile 根目录文件夹
   * @param force    当已经存在一个根目录的File对象时是否强制替换
   * @return 是否设置成功
   */
  boolean setRoot(File rootFile, boolean force);

  /**
   * 获取缓存的根目录
   */
  File getRoot();

  /**
   * 获取根目录路径
   *
   * @return 返回根目录的路径
   */
  String getRootPath();

  /**
   * 获取文件路径
   *
   * @param filename 文件名
   * @return 返回文件的绝对路径
   */
  String getFilepath(String filename);

  /**
   * 创建文件如果不存在
   *
   * @param file        文件
   * @param isDirectory 是否为目录
   * @return 返回是否存在或创建成功
   */
  boolean createIfNotExist(File file, boolean isDirectory);

  /**
   * 列出目录下的全部文件
   *
   * @param filename 目录
   * @return 返回 File 数组
   */
  File[] listFiles(String filename);

  /**
   * 创建目录，如果存在直接返回 File 对象
   *
   * @param filename 目录名
   * @return 返回创建的目录文件
   */
  File getDirectory(String filename);

  /**
   * 创建目录，如果存在直接返回 File 对象
   *
   * @param filename 目录名
   * @param create   如果不存在是否创建
   * @return 返回创建的目录文件
   */
  File getDirectory(String filename, boolean create);

  /**
   * 创建目录，如果存在直接返回 File 对象
   *
   * @param parentFile 父目录
   * @param filename   目录名
   * @return 返回创建的目录文件
   */
  File getDirectory(@Nullable String parentFile, String filename);

  /**
   * 创建目录，如果存在直接返回 File 对象
   *
   * @param parentFile 父目录
   * @param filename   目录名
   * @return 返回创建的目录文件
   */
  File getDirectory(@Nullable File parentFile, String filename);

  /**
   * 创建目录，如果存在直接返回 File 对象
   *
   * @param parentFile 父目录
   * @param filename   目录名
   * @return 返回创建的目录文件
   */
  File getDirectory(@Nullable File parentFile, String filename, boolean create);

  /**
   * 创建文件，如果存在直接返回 File 对象
   *
   * @param filename 文件名
   * @return 返回创建的文件
   */
  File getFile(String filename);

  /**
   * 创建文件，如果存在直接返回 File 对象
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @return 返回创建的文件
   */
  File getFile(@Nullable String parentFile, String filename);

  /**
   * 创建文件，如果存在直接返回 File 对象
   *
   * @param parentFile 目录
   * @param filename   文件名
   * @param create     是否创建
   * @return 返回创建的文件
   */
  File getFile(@Nullable File parentFile, String filename, boolean create);


  /**
   * 删除文件
   *
   * @param file 文件名
   * @return 是否删除
   */
  boolean deleteFile(String file);

  /**
   * 删除文件
   *
   * @param directory 目录
   * @param filename  文件名
   * @return 是否删除
   */
  boolean deleteFile(@Nullable String directory, String filename);

  /**
   * 删除目录
   *
   * @param directory 目录名
   * @return 删除的文件数量
   */
  int deleteDirectory(String directory);

  /**
   * 删除文件或目录
   *
   * @param path 路径
   * @return 返回是否删除成功
   */
  boolean delete(String path);

  /**
   * 删除文件或目录
   *
   * @param file 文件
   * @return 删除的文件数量
   */
  int delete(File file);

  /**
   * 删除文件或目录
   *
   * @param file  文件
   * @param force 是否强制
   * @return 删除的文件数量
   */
  int delete(File file, boolean force);

  /**
   * 删除全部文件和目录
   *
   * @return 删除的文件数量
   */
  int deleteAll();

  /**
   * 查看文件或目录的大小
   *
   * @param file 文件或目录
   * @return 返回文件或目录的大小
   */
  long length(String file);

  /**
   * 查看文件或目录的大小
   *
   * @param file     文件或目录
   * @param subFiles 是否统计子文件
   * @return 返回文件或目录的大小
   */
  long length(File file, boolean subFiles);

  /**
   * 统计某个目录的数量，如果传入的是文件，则返回 1 ，否则返回统计的数量
   *
   * @param path 目录
   * @return 返回统计的数量
   */
  int count(String path);

  /**
   * 统计某个目录的数量，如果传入的是文件，则返回 1 ，否则返回统计的数量
   *
   * @param path 目录
   * @return 返回统计的数量
   */
  int count(File path);

  /**
   * 是否为文件
   *
   * @param path 文件名
   * @return 如果是文件返回 true，否则返回 false
   */
  boolean isFile(String path);

  /**
   * 是否为文件
   *
   * @param parentFile 目录名
   * @param filename   文件名
   * @return 如果是文件返回 true，否则返回 false
   */
  boolean isFile(String parentFile, String filename);

  /**
   * 是否为目录
   *
   * @param filename 目录名
   * @return 如果是目录返回 true，否则返回 false
   */
  boolean isDirectory(String filename);

  /**
   * 文件或目录是否存在
   *
   * @param filename 文件名
   * @return 如果存在返回 true ，否则返回 false
   */
  boolean exist(String filename);

  /**
   * 传输
   *
   * @param in  输入流
   * @param out 输出流
   */
  void transferTo(InputStream in, File out);

  /**
   * 传输
   *
   * @param out      输出流
   * @param consumer 输入流处理程序
   */
  void transferTo(File out, Consumer<OutputStream> consumer);

  /**
   * 传输
   *
   * @param in  输入流
   * @param out 输出流
   */
  void transferTo(InputStream in, OutputStream out);

  /**
   * 传输
   *
   * @param in        输入流
   * @param out       输出流
   * @param autoClose 是否自动关闭流
   */
  void transferTo(InputStream in, OutputStream out, boolean autoClose);

  /**
   * 创建目录
   *
   * @param directory 目录
   * @return 返回是否存在或创建成功
   */
  default boolean createDirectoryIfNotExist(File directory) {
    return createIfNotExist(directory, true);
  }

  /**
   * 创建文件
   *
   * @param file 文件
   * @return 返回是否存在或创建成功
   */
  default boolean createFileIfNotExist(File file) {
    return createIfNotExist(file, false);
  }

  /**
   * 关闭全部
   *
   * @param closes AutoCloseable实现(InputStream、OutputStream)
   */
  default void closeQuietly(AutoCloseable... closes) {
    if (closes != null && closes.length > 0) {
      for (AutoCloseable c : closes) {
        if (c != null) {
          try {
            c.close();
          } catch (Exception e) {/* ignore */}
        }
      }
    }
  }
}
