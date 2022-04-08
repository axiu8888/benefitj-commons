package com.benefitj.core.file;

import com.benefitj.core.IOUtils;
import com.benefitj.core.CatchUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 文件管理
 */
public class FileManager implements IFileManager {

  public static final File[] EMTPY = new File[0];

  /**
   * 根目录
   */
  private final AtomicReference<LocaleFile> rootRef = new AtomicReference<>();

  public FileManager() {
    // ~
  }

  public FileManager(File root) {
    setRoot(root, false);
  }

  @Override
  public boolean setRoot(File f, boolean force) {
    if (f == null) {
      throw new IllegalArgumentException("根目录不能为 null ！");
    }

    if (!f.exists()) {
      f.mkdirs();
    }

    synchronized (this) {
      // 如果强制替换，直接替换掉，否则只有为 null 时替换
      LocaleFile tmpRoot = this.rootRef.get();
      if (force || tmpRoot == null) {
        this.rootRef.set(new LocaleFile(f.getAbsolutePath()));
      }
    }
    return true;
  }

  @Override
  public File getRoot() {
    LocaleFile file = this.rootRef.get();
    if (file == null) {
      throw new IllegalStateException("还未设置根目录!");
    }
    return file;
  }

  @Override
  public String getRootPath() {
    LocaleFile file = this.rootRef.get();
    if (file == null) {
      throw new IllegalStateException("还未设置根目录!");
    }
    return file.getRootPath();
  }

  @Override
  public String getFilepath(String filename) {
    char c = filename.charAt(0);
    String separator = (c == '/' || c == '\\' ? "" : File.separator);
    return append(getRootPath(), separator, filename);
  }

  @Override
  public boolean createIfNotExist(File file, boolean isDirectory) {
    return createIfNotExist(file, isDirectory, true);
  }

  public boolean createIfNotExist(File file, boolean isDirectory, boolean create) {
    if (create && !file.exists()) {
      if (isDirectory) {
        return file.mkdirs();
      } else {
        try {
          return file.getParentFile().mkdirs() && file.createNewFile();
        } catch (IOException e) {
          throw CatchUtils.throwing(e, IllegalStateException.class);
        }
      }
    }
    return true;
  }

  @Override
  public File[] listFiles(String filename) {
    File dir = getDirectory(filename, false);
    File[] files = dir.listFiles();
    return files != null ? files : EMTPY;
  }

  @Override
  public File getDirectory(String filename) {
    return getDirectory(null, filename, false);
  }

  @Override
  public File getDirectory(String filename, boolean create) {
    return getDirectory(null, filename, create);
  }

  @Override
  public File getDirectory(@Nullable String parentFile, String filename) {
    File parent = obtainParent(parentFile);
    return getDirectory(parent, filename, false);
  }

  @Override
  public File getDirectory(@Nullable File parentFile, String filename) {
    return getDirectory(parentFile, filename, false);
  }

  @Override
  public File getDirectory(@Nullable File parentFile, String filename, boolean create) {
    File dir = parentFile != null
        ? new File(parentFile, filename)
        : new File(getFilepath(filename));
    createIfNotExist(dir, true, create);
    return dir;
  }

  @Override
  public File getFile(String filename) {
    return getFile(null, filename, false);
  }

  @Override
  public File getFile(@Nullable String parentFile, String filename) {
    File parent = obtainParent(parentFile);
    return getFile(parent, filename, false);
  }

  @Override
  public File getFile(@Nullable File parentFile, String filename, boolean create) {
    File file = parentFile != null
        ? new File(parentFile, filename)
        : new File(getFilepath(filename));
    createIfNotExist(file, false, create);
    return file;
  }

  @Override
  public boolean deleteFile(String file) {
    return deleteFile(null, file);
  }

  @Override
  public boolean deleteFile(String directory, String filename) {
    File parent = obtainParent(directory);
    File file = getFile(parent, filename, false);
    return !file.exists() || file.delete();
  }

  @Override
  public int deleteDirectory(String directory) {
    File f = getDirectory(directory, false);
    return f.exists() && f.isDirectory() ? delete(f) : 0;
  }

  @Override
  public int delete(File file) {
    return delete(file, true);
  }

  @Override
  public int delete(File file, boolean force) {
    int count = 0;
    if (file != null && file.exists()) {
      if (file.isFile()) {
        if (force) {
          count = file.delete() ? count + 1 : count;
        } else {
          count = delete(file.getPath()) ? count + 1 : count;
        }
      } else if (file.isDirectory()) {
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
          for (File f : files) {
            count += delete(f);
          }
        }
        // 删除目录
        delete(file.getPath());
      }
    }
    return count;
  }

  @Override
  public boolean delete(String path) {
    try {
      return Files.deleteIfExists(Paths.get(path));
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public int deleteAll() {
    String root = getFilepath("/");
    return delete(new File(root));
  }

  @Override
  public long length(String file) {
    File src = getFile(null, file, false);
    return length(src, true);
  }

  @Override
  public long length(File file, boolean subFiles) {
    return IOUtils.length(file, subFiles);
  }

  @Override
  public int count(String path) {
    String filePath = getFilepath(path);
    File f = new File(filePath);
    return count(f);
  }

  @Override
  public int count(File file) {
    if (file == null || !file.exists()) {
      return 0;
    }
    if (file.isDirectory()) {
      int count = 0;
      File[] files = file.listFiles();
      files = files != null ? files : new File[0];
      for (File f : files) {
        if (f.isDirectory()) {
          count += count(f);
        } else {
          count++;
        }
      }
      return count;
    }
    return 1;
  }

  @Override
  public boolean isFile(String path) {
    return isFile(null, path);
  }

  @Override
  public boolean isFile(String parentFile, String filename) {
    File file = getFile(parentFile, filename);
    return file.exists() && file.isFile();
  }

  @Override
  public boolean isDirectory(String filename) {
    File file = getDirectory(null, filename, false);
    return file.exists() && file.isDirectory();
  }

  @Override
  public boolean exist(String filename) {
    return getFile(null, filename, false).exists();
  }

  /**
   * 传输
   *
   * @param in  输入流
   * @param out 输出流
   */
  @Override
  public void transferTo(InputStream in, File out) {
    try (final FileOutputStream fos = new FileOutputStream(out)) {
      transferTo(in, fos, true);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 传输
   *
   * @param out      输出流
   * @param consumer 输入流处理程序
   */
  @Override
  public void transferTo(File out, Consumer<OutputStream> consumer) {
    createFileIfNotExist(out);
    try (final FileOutputStream fos = new FileOutputStream(out);) {
      consumer.accept(fos);
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 传输
   *
   * @param in  输入流
   * @param out 输出流
   */
  @Override
  public void transferTo(InputStream in, OutputStream out) {
    transferTo(in, out, true);
  }

  /**
   * 传输
   *
   * @param in        输入流
   * @param out       输出流
   * @param autoClose 是否自动关闭流
   */
  @Override
  public void transferTo(InputStream in, OutputStream out, boolean autoClose) {
    IOUtils.write(in, out, 1024, autoClose);
  }

  protected String append(Object... os) {
    StringBuilder sb = new StringBuilder();
    for (Object o : os) {
      sb.append(o);
    }
    return sb.toString();
  }

  protected File obtainParent(String directory) {
    return isNotEmpty(directory) ? new File(getFilepath(directory)) : null;
  }

  protected static boolean isNotEmpty(CharSequence cs) {
    if (cs != null) {
      int size = cs.length();
      for (int i = 0; i < size; i++) {
        if (cs.charAt(i) != ' ') {
          return true;
        }
      }
    }
    return false;
  }

  public static class LocaleFile extends File {

    private final String rootPath;

    public LocaleFile(String pathname) {
      super(pathname);
      this.rootPath = getAbsolutePath();
    }

    public LocaleFile(String parent, String child) {
      super(parent, child);
      this.rootPath = getAbsolutePath();
    }

    public LocaleFile(File parent, String child) {
      super(parent, child);
      this.rootPath = getAbsolutePath();
    }

    public LocaleFile(URI uri) {
      super(uri);
      this.rootPath = getAbsolutePath();
    }

    public String getRootPath() {
      return rootPath;
    }
  }

}
