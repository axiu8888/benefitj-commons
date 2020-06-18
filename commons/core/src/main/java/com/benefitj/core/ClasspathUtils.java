package com.benefitj.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classpath工具
 */
public class ClasspathUtils {

  private static final String PATH_SYMBOL = "/";
  // Jar包的协议
  private static final String JAR_SCHEME = "jar:";
  private static final String FILE_SCHEME = "file:";
  private static final String JAR_FILE_SCHEME = "jar:file:";
  // Jar的后缀
  private static final String JAR_SUFFIX = ".jar";
  // SpringBoot
  private static final String SPRINGBOOT_JAR = ".jar!";
  private static final String SPRINGBOOT_SUFFIX = ".jar!/";

  private static final boolean WINDOWS;

  static {
    WINDOWS = System.getProperty("os.name").contains("Windows");
  }

  private static Class<ClasspathUtils> defaultClass() {
    return ClasspathUtils.class;
  }

  /**
   * 获取默认上下文的ClassLoader
   */
  public static ClassLoader getDefaultClassLoader() {
    return getClassLoader(() -> {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl == null) {
        cl = defaultClass().getClassLoader();
        if (cl == null) {
          cl = ClassLoader.getSystemClassLoader();
        }
      }
      return cl;
    });
  }

  /**
   * 获取默认的Classpath
   */
  @Nonnull
  public static String defaultClasspath() {
    URL url = defaultClasspathURL();
    return url != null ? url.toExternalForm() : getClasspath(defaultClass());
  }

  /**
   * 获取Classpath
   */
  public static String getClasspath(ClassLoader loader) {
    URL url = loader.getResource("");
    return url != null ? url.toExternalForm() : null;
  }

  /**
   * 获取的Classpath
   */
  public static String getClasspath(Class<?> klass) {
    return getClasspathURL(klass).toExternalForm();
  }

  /**
   * 获取默认的Classpath的URL
   */
  public static URL defaultClasspathURL() {
    ClassLoader loader = getDefaultClassLoader();
    URL url = loader.getResource("");
    return url != null ? url : getClasspathURL(defaultClass());
  }

  /**
   * 获取Class所在上下文的的URL
   *
   * @param klass 类
   * @return 返回类的上下文路径
   */
  public static URL getClasspathURL(Class<?> klass) {
    return klass.getProtectionDomain().getCodeSource().getLocation();
  }

  /**
   * 判断路径是否为Jar包，如果路径以{@link #JAR_SCHEME}开头，或以{@link #JAR_SUFFIX}结尾，
   * 或者以{@link #FILE_SCHEME}开头且包含{@link #JAR_SUFFIX}的路径，表示是Jar包
   *
   * @param path 路径
   * @return 返回判断的结果
   */
  public static boolean isJar(String path) {
    if (path == null) {
      return false;
    }
    if (path.startsWith(JAR_SCHEME)) {
      return true;
    }
    // 可能是springboot的jar包
    return path.startsWith(FILE_SCHEME) && path.contains(JAR_SUFFIX);
  }

  /**
   * 判断是否以{@link #JAR_FILE_SCHEME}开头的jar文件路径
   *
   * @param path 路径
   * @return 返回判断的结果
   */
  public static boolean isJarFile(String path) {
    return path != null && path.startsWith(JAR_FILE_SCHEME);
  }

  /**
   * 判断是否为SpringBoot的jar包，如果以{@link #JAR_SCHEME}开头，
   * 且包含{@link #SPRINGBOOT_JAR}，表示是SpringBoot的jar包
   *
   * @param classpathUrl 上下文路径
   * @return 返回是否为SpringBoot的jar包
   */
  public static boolean isSpringBootJar(URL classpathUrl) {
    return classpathUrl != null && isSpringBootJar(classpathUrl.toString());
  }

  /**
   * 判断是否为SpringBoot的jar包，如果以{@link #JAR_SCHEME}开头，
   * 且包含{@link #SPRINGBOOT_JAR}，表示是SpringBoot的jar包
   *
   * @param classpath 上下文路径
   * @return 返回是否为SpringBoot的jar包
   */
  public static boolean isSpringBootJar(String classpath) {
    return classpath != null && (classpath.contains(SPRINGBOOT_JAR) && isJar(classpath));
  }

  /**
   * 获取jar的绝对路径，对于如springboot打包后的jar，可能还嵌套着jar，只取第一层的jar路径
   *
   * @param path jar包的路径
   * @return 返回jar的绝对路径
   */
  public static String getJarAbsolutePath(String path) {
    if (!isJar(path)) {
      throw new IllegalArgumentException("不是jar包的路径!");
    }
    String jarPath;
    if (isJarFile(path)) {
      jarPath = path.replaceFirst(JAR_FILE_SCHEME + "/", "");
    } else {
      jarPath = path.replaceFirst(JAR_SCHEME, "");
      jarPath = jarPath.replaceFirst(FILE_SCHEME + "/", "");
    }
    if (jarPath.endsWith(SPRINGBOOT_SUFFIX)) {
      // springboot的classpath路径
      jarPath = jarPath.substring(0, jarPath.indexOf(SPRINGBOOT_SUFFIX) + JAR_SUFFIX.length());
    } else {
      jarPath = jarPath.substring(0, jarPath.lastIndexOf(JAR_SUFFIX) + JAR_SUFFIX.length());
    }
    return jarPath;
  }

  /**
   * 获取jar所在目录的绝对路径
   *
   * @param path jar包的路径
   * @return 返回jar所在目录的绝对路径
   */
  public static String getJarAbsolutePathDir(String path) {
    String jarPath = getJarAbsolutePath(path);
    // 截取.jar
    String pathPrefix = jarPath.substring(0, jarPath.lastIndexOf(JAR_SUFFIX));
    return pathPrefix.substring(0, pathPrefix.lastIndexOf(PATH_SYMBOL) + 1);
  }

  /**
   * 获取Classpath的绝对路径，如果是jar包返回jar的绝对路径
   *
   * @param classpath 上下文路径
   * @return 返回classpath的绝对路径
   */
  public static String getAbsolutePath(String classpath) {
    if (isJar(classpath)) {
      return getJarAbsolutePath(classpath);
    }
    String out = classpath.startsWith(FILE_SCHEME) ? classpath.substring(FILE_SCHEME.length()) : classpath;
    return trimPath(out, true, true);
  }

  /**
   * 拷贝文件或文件夹到指定目录，支持从jar包中拷贝
   *
   * @param klass 类
   * @param src   原文件
   * @param dest  目标文件
   * @throws IOException
   */
  public static void copyFilesTo(Class<?> klass, String src, String dest) throws IOException {
    copyFilesTo(klass.getClassLoader(), klass, src, dest);
  }

  /**
   * 拷贝文件或文件夹到指定目录，支持从jar包中拷贝
   *
   * @param classLoader 类加载对象
   * @param src         原文件
   * @param dest        目标文件
   * @throws IOException
   */
  public static void copyFilesTo(ClassLoader classLoader, String src, String dest) throws IOException {
    copyFilesTo(classLoader, null, src, dest);
  }

  /**
   * 拷贝文件或文件夹到指定目录，支持从jar包中拷贝
   *
   * @param classLoader 类加载对象
   * @param klass       类
   * @param src         原文件
   * @param dest        目标文件
   * @throws IOException
   */
  public static void copyFilesTo(ClassLoader classLoader, @Nullable Class<?> klass, String src, String dest) throws IOException {
    String classpath = getClasspath(classLoader);
    if (classpath == null || classpath.isEmpty()) {
      throw new IllegalArgumentException("classpath is empty");
    }
    if (isJar(classpath)) {
      URL url = classLoader.getResource(src);
      if (url == null) {
        throw new FileNotFoundException("无法获取classpath下的\"" + src + "\"资源");
      }
      copyFilesFromJarTo(url, src, dest);
    } else {
      // 拷贝
      URL srcURL = getResourceURL(classLoader, klass, src);
      if (srcURL == null) {
        if (klass != null && isJar(getClasspathURL(klass).toExternalForm())) {
          // 在jar包中
          URL jarURL = getClasspathURL(klass);
          copyFilesFromJarTo(jarURL, src, dest);
          return;
        }
        throw new FileNotFoundException("无法获取classpath下的\"" + src + "\"资源");
      }
      File srcFile = new File(srcURL.getPath());
      if (!srcFile.exists()) {
        throw new FileNotFoundException("classpath下不存在此文件: " + srcFile.getAbsolutePath());
      }
      copyFiles(srcFile, srcFile.getAbsolutePath(), dest);
    }
  }

  /**
   * 拷贝文件
   *
   * @param file 文件
   * @param src  原文件地址
   * @param dest 目标地址
   * @throws IOException 拷贝错误，抛出异常
   */
  private static void copyFiles(File file, String src, String dest) throws IOException {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        for (File f : files) {
          copyFiles(f, src, dest);
        }
      }
    } else {
      String pathname = dest + file.getAbsolutePath().substring(src.length());
      File destFile = new File(pathname);
      if (file.isDirectory()) {
        destFile.mkdirs();
        copyFiles(file, src, dest);
      } else {
        // 拷贝
        destFile.getParentFile().mkdirs();
        destFile.createNewFile();
        transferTo(new FileInputStream(file), destFile, true);
      }
    }
  }

  /**
   * 从jar包中拷贝
   *
   * @param url  JAR包中资源的路径
   * @param src  源文件
   * @param dest 目标文件
   * @throws IOException
   */
  public static void copyFilesFromJarTo(URL url, String src, String dest) throws IOException {
    // 拷贝jar中的文件
    URLConnection conn = url.openConnection();
    if (conn instanceof JarURLConnection) {
      JarURLConnection jarConn = (JarURLConnection) url.openConnection();
      try (JarFile jarFile = jarConn.getJarFile();) {
        copyFilesFromJarTo(jarFile, src, dest);
      }
    } else {
      try (JarFile jarFile = new JarFile(url.getPath());) {
        copyFilesFromJarTo(jarFile, src, dest);
      }
    }
  }

  /**
   * 从jar包中拷贝
   *
   * @param jarFile JAR包
   * @param src     源文件
   * @param dest    目标文件
   * @throws IOException
   */
  public static void copyFilesFromJarTo(JarFile jarFile, String src, String dest) throws IOException {
    Enumeration<JarEntry> entries = jarFile.entries();
    String stripSrc = trimPath(src, true, false);
    String stripDest = trimPath(dest, false, true);
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String entryName = entry.getName();
      if (entryName.startsWith(stripSrc)) {
        File destFile = new File(stripDest, entryName.substring(stripSrc.length()));
        if (entry.isDirectory()) {
          destFile.mkdirs();
        } else {
          // 拷贝文件
          transferTo(jarFile.getInputStream(entry), destFile, true);
        }
      }
    }
  }

  /**
   * 获取资源路径
   *
   * @param classLoader 类加载对象
   * @param resource    资源
   * @return 返回资源
   */
  public static URL getResourceURL(ClassLoader classLoader, String resource) {
    return getResourceURL(classLoader, null, resource);
  }

  /**
   * 获取资源路径
   *
   * @param klass    类
   * @param resource 资源
   * @return 返回资源
   */
  public static URL getResourceURL(Class<?> klass, String resource) {
    return getResourceURL(klass.getClassLoader(), klass, resource);
  }

  /**
   * 获取资源路径
   *
   * @param classLoader 类加载对象
   * @param klass       类
   * @param resource    资源
   * @return 返回资源
   */
  public static URL getResourceURL(ClassLoader classLoader, @Nullable Class<?> klass, String resource) {
    URL resourceUrl = classLoader.getResource(resource);
    if (resourceUrl != null) {
      return resourceUrl;
    }
    String classpath = getClasspath(classLoader);
    resourceUrl = findResourceURL(classpath, resource);
    if (resourceUrl == null && klass != null) {
      classpath = getClasspath(klass);
      if (isJar(classpath)) {
        return null;
      }
      resourceUrl = findResourceURL(getAbsolutePath(classpath), resource);
    }
    return resourceUrl;
  }

  private static URL findResourceURL(String classpath, String resource) {
    classpath = classpath != null ? classpath : defaultClasspath();

    String absolutePath = getAbsolutePath(classpath);
    File classpathFile = new File(trimPath(absolutePath.substring(0, absolutePath.lastIndexOf("classes")), false, true));
    File parentFile = classpathFile.getParentFile();
    File file = parentFile.getName().equals("build") ? classpathFile : parentFile;
    String resourceName = "/" + (trimPath(resource, true, true));
    List<File> files = filter(file, f -> filePath(f).endsWith("/classes") || filePath(f).endsWith("/resources"))
        .stream()
        .flatMap(f -> filter(f, sf -> filePath(sf).endsWith(resourceName)).stream())
        .collect(Collectors.toList());
    if (!files.isEmpty()) {
      try {
        // 如果是test上下文，优先从test目录获取
        if (classpath.contains("/test/")) {
          return files.stream()
              .filter(f -> (f.getAbsolutePath().replace("\\", "/")).contains("test"))
              .findFirst()
              .orElse(files.get(0)).toURL();
        }
        return files.get(0).toURL();
      } catch (MalformedURLException ignore) {/* ignore */}
    }
    return null;
  }

  private static List<File> filter(File file, Predicate<File> test) {
    if (test.test(file)) {
      return Collections.singletonList(file);
    }
    if (file.isDirectory()) {
      if (test.test(file)) {
        return Collections.singletonList(file);
      }
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        return Stream.of(files)
            .filter(Objects::nonNull)
            .flatMap(f -> filter(f, test).stream())
            .collect(Collectors.toList());
      }
    }
    return Collections.emptyList();
  }


  private static String trimPath(String path, boolean first, boolean last) {
    String out = path;
    while (first && out.startsWith(PATH_SYMBOL)) {
      out = out.substring(1);
    }
    while (last && out.endsWith(PATH_SYMBOL)) {
      out = out.substring(0, out.length() - 1);
    }
    return out;
  }

  /**
   * 拷贝到文件中
   *
   * @param in    输入流
   * @param dest  目标文件
   * @param close 是否自动关闭
   * @return 返回读取的长度
   * @throws IOException 抛出的IO异常
   */
  private static long transferTo(InputStream in, File dest, boolean close) throws IOException {
    if (!dest.exists()) {
      dest.getParentFile().mkdirs();
      dest.createNewFile();
    }
    try (FileOutputStream out = new FileOutputStream(dest);) {
      return transferTo(in, out, close);
    }
  }

  /**
   * 拷贝到某处
   *
   * @param in    输入流
   * @param out   输出流
   * @param close 是否关闭输入输出
   * @return 返回读取的长度
   * @throws IOException 抛出的IO异常
   */
  public static long transferTo(InputStream in, OutputStream out, boolean close) throws IOException {
    try {
      long size = 0;
      int len;
      byte[] buff = new byte[1024 << 4];
      while ((len = in.read(buff)) > 0) {
        out.write(buff, 0, len);
        size += len;
      }
      out.flush();
      return size;
    } finally {
      if (close) {
        IOUtils.closeQuietly(out, in);
      }
    }
  }

  private static ClassLoader getClassLoader(Callable<ClassLoader> callable) {
    if (System.getSecurityManager() == null) {
      try {
        return callable.call();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    } else {
      return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
        try {
          return callable.call();
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
      });
    }
  }


  private static String filePath(File f) {
    return WINDOWS ? f.getAbsolutePath().replace("\\", "/") : f.getAbsolutePath();
  }

}
