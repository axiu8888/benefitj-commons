package com.benefitj.core;

import com.benefitj.core.file.FileCopy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nullable;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * classpath 工具
 */
@Deprecated
public class ClasspathUtils {

  /**
   * 是否为spring环境
   */
  private static final boolean SPRING_CLASSPATH;
  // Jar包的协议
  private static final String JAR_SCHEME = "jar:";
  private static final String FILE_SCHEME = "file:";
  private static final String JAR_FILE_SCHEME = "jar:file:";
  // Jar的后缀
  private static final String JAR_SUFFIX = ".jar";

  static {
    boolean springboot;
    try {
      Class.forName("org.springframework.core.io.ClassPathResource");
      springboot = true;
    } catch (ClassNotFoundException ignore) {
      springboot = false;
    }
    SPRING_CLASSPATH = springboot;
  }

  private static ClassLoader getClassLoader(Callable<ClassLoader> callable) {
    /*if (System.getSecurityManager() == null) {
      try {
        return callable.call();
      } catch (Exception e) {
        throw CatchUtils.throwing(e, IllegalStateException.class);
      }
    } else {
      return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
        try {
          return callable.call();
        } catch (Exception e) {
          throw CatchUtils.throwing(e, IllegalStateException.class);
        }
      });
    }*/
    try {
      return callable.call();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  public static List<String> getClasspath() {
    return Arrays.asList(System.getProperty("java.class.path").split(";"));
  }

  public static List<File> getClasspathDirs() {
    return getClasspath()
        .stream()
        .map(File::new)
        .filter(File::isDirectory)
        .collect(Collectors.toList());
  }

  /**
   * 获取默认上下文的ClassLoader
   */
  public static ClassLoader getDefaultClassLoader() {
    return getDefaultClassLoader(null);
  }

  /**
   * 获取默认上下文的ClassLoader
   */
  public static ClassLoader getDefaultClassLoader(@Nullable Class<?> defaultClass) {
    return getClassLoader(() -> {
      if (defaultClass != null) {
        return defaultClass.getClassLoader();
      }
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl == null) {
        cl = ClassLoader.getSystemClassLoader();
        if (cl == null) {
          cl = ClasspathUtils.class.getClassLoader();
        }
      }
      return cl;
    });
  }

  /**
   * 是否为springboot环境
   */
  public static boolean isSpringEnv() {
    return SPRING_CLASSPATH;
  }

  /**
   * 默认的Classpath目录
   */
  public static String getDefaultClasspathDir() {
    return getURL(".").getPath();
  }

  /**
   * 获取文件对象
   *
   * @param filename 文件名
   * @return 返回文件对象
   */
  public static File getFile(String filename) {
    return new File(getURL(filename).getFile());
  }

  /**
   * 获取资源的URL
   *
   * @param filename 文件名
   * @return 返回URL
   */
  public static URL getURL(String filename) {
    return getURL(filename, null);
  }

  /**
   * 获取资源的URL
   *
   * @param filename 文件名
   * @param loader   ClassLoader
   * @return 返回URL
   */
  public static URL getURL(String filename, ClassLoader loader) {
    try {
      if (isSpringEnv()) {
        ClassPathResource resource = new ClassPathResource(filename, loader);
        return resource.getURL();
      } else {
        loader = loader != null ? loader : getDefaultClassLoader(null);
        return loader.getResource(filename);
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 判断路径是否为Jar包，如果路径以{@link ClasspathUtils#JAR_SCHEME}开头，
   * 或以{@link #JAR_SUFFIX}结尾，
   * 或者以{@link ClasspathUtils#FILE_SCHEME}开头 与 包含{@link ClasspathUtils#JAR_SUFFIX}的路径，表示是Jar包
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
   * 拷贝文件或文件夹
   *
   * @param src  源文件
   * @param dest 目标目录
   */
  public static void copy(String src, String dest) {
    copy(src, dest, null);
  }

  /**
   * 拷贝文件或文件夹
   *
   * @param src    源文件
   * @param dest   目标目录
   * @param loader 类加载器
   */
  public static void copy(String src, String dest, @Nullable ClassLoader loader) {
    URL url = getURL(src, loader);
    if (isJar(url.getPath())) {
      copyFromJarTo(url, src, dest);
    } else {
      File rawFile = new File(url.getFile());
      FileCopy.copy(rawFile, new File(dest, rawFile.getName()));
    }
  }

  /**
   * 从jar包中拷贝
   *
   * @param url  JAR包中资源的路径
   * @param src  源文件
   * @param dest 目标文件
   */
  public static void copyFromJarTo(URL url, String src, String dest) {
    try {
      // 拷贝jar中的文件
      URLConnection conn = url.openConnection();
      if (conn instanceof JarURLConnection) {
        JarURLConnection jarConn = (JarURLConnection) url.openConnection();
        try (JarFile jarFile = jarConn.getJarFile();) {
          copyFromJarTo(jarFile, src, dest);
        }
      } else {
        try (JarFile jarFile = new JarFile(url.getPath());) {
          copyFromJarTo(jarFile, src, dest);
        }
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 从jar包中拷贝
   *
   * @param jarFile JAR包
   * @param src     源文件
   * @param dest    目标文件
   */
  public static void copyFromJarTo(JarFile jarFile, String src, String dest) {
    try {
      Enumeration<JarEntry> entries = jarFile.entries();
      String stripSrc = trim(src, true, false);
      String stripDest = trim(dest, false, true);
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.getName().startsWith(stripSrc)) {
          String name;
          if (entry.isDirectory()) {
            name = entry.getName().substring(stripSrc.length());
          } else {
            if (entry.getName().endsWith(stripSrc)) {
              name = trim(entry.getName(), false, true);
              name = name.substring(Math.max(name.lastIndexOf("/") + 1, 0));
            } else {
              name = entry.getName();
            }
          }
          File destFile = new File(stripDest, name);
          if (entry.isDirectory()) {
            destFile.mkdirs();
          } else {
            // 拷贝文件
            IOUtils.write(jarFile.getInputStream(entry), destFile);
          }
        }
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  private static String trim(String path, boolean first, boolean last) {
    String out = StringUtils.getIfBlank(path, () -> "").replace("\\", "/").replace("//", "/");
    while (first && out.startsWith("/")) out = out.substring(1);
    while (last && out.endsWith("/")) out = out.substring(0, out.length() - 1);
    return out;
  }


  /**
   * 查找所有的Class
   *
   * @param standard 标准类所在包
   * @return 返回搜索到的所有Class
   */
  public static List<String> findClasses(Class<?> standard) {
    return findClasses(standard.getPackage().getName(), true);
  }

  /**
   * 查找所有的Class
   *
   * @param packageName 包名
   * @return 返回搜索到的所有Class
   */
  public static List<String> findClasses(String packageName) {
    return findClasses(packageName, true);
  }

  /**
   * 查找所有的Class
   *
   * @param packageName     包名
   * @param subPackageNames 是否迭代查询所有子目录
   * @return 返回搜索到的所有Class
   */
  public static List<String> findClasses(String packageName, boolean subPackageNames) {
    return findClasses(packageName, subPackageNames, s -> true);
  }

  /**
   * 查找所有的Class
   *
   * @param packageName     包名
   * @param subPackageNames 是否迭代查询所有子目录
   * @param filter          过滤
   * @return 返回搜索到的所有Class
   */
  public static List<String> findClasses(String packageName, boolean subPackageNames, Predicate<String> filter) {
    try {
      List<String> classes = new ArrayList<>();
      // 获取包的名字 并进行替换
      String packageDir = packageName.replace('.', '/');
      Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDir);
      // 循环迭代下去
      while (dirs.hasMoreElements()) {
        // 获取下一个元素
        URL url = dirs.nextElement();
        //得到协议的名称
        String protocol = url.getProtocol();
        // 如果是以文件的形式保存在服务器上
        if ("file".equals(protocol)) {
          // 获取包的物理路径
          String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
          // 以文件的方式扫描整个包下的文件 并添加到集合中
          List<String> subClasses = findClasses(packageName, new File(filePath), subPackageNames, filter);
          classes.addAll(subClasses);
        } else if ("jar".equals(protocol)) {
          JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
          Enumeration<JarEntry> entries = jar.entries();
          while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果是以/开头的
            if (name.charAt(0) == '/') {
              // 获取后面的字符串
              name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDir)) {
              int idx = name.lastIndexOf('/');
              // 如果以"/"结尾 是一个包
              if (idx != -1) {
                // 获取包名 把"/"替换成"."
                packageName = name.substring(0, idx).replace('/', '.');
              }
              // 如果可以迭代下去 并且是一个包
              if ((idx != -1) || subPackageNames) {
                if (name.endsWith(".class") && !entry.isDirectory()) {
                  String classname = getClassname(packageName, name);
                  if (filter.test(classname)) {
                    classes.add(classname);
                  }
                }
              }
            }
          }
        }
      }
      return classes;
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 以文件的形式来获取包下的所有Class
   *
   * @param packageName 包名
   * @param packagePath 包路径
   * @param recursive   是否递归
   */
  public static List<String> findClasses(String packageName, File packagePath, boolean recursive, Predicate<String> filter) {
    //如果不存在或者 也不是目录就直接返回
    if (!packagePath.exists() || !packagePath.isDirectory()) {
      return Collections.emptyList();
    }
    List<String> classes = new ArrayList<>();
    File[] subFiles = packagePath.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
    if (subFiles != null && subFiles.length > 0) {
      for (File subFile : subFiles) {
        if (subFile.isDirectory()) {
          List<String> subClasses = findClasses(packageName + "." + subFile.getName(), subFile, recursive, filter);
          classes.addAll(subClasses);
        } else {
          String classname = getClassname(packageName, subFile.getName());
          if (filter.test(classname)) {
            classes.add(classname);
          }
        }
      }
    }
    return classes;
  }

  private static String getClassname(String packageName, String filename) {
    if (filename.indexOf("/") > 0) {
      String tmpName = filename.replace("/", ".");
      return tmpName.substring(0, filename.length() - 6);
    }
    return packageName + '.' + filename.substring(0, filename.length() - 6);
  }

}
