package com.benefitj.core.file;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * jar包资源拷贝
 */
public class JarFileCopy {
  // Jar包的协议
  private static final String JAR_SCHEME = "jar:";
  private static final String FILE_SCHEME = "file:";
  // Jar的后缀
  private static final String JAR_SUFFIX = ".jar";

  /**
   * 判断路径是否为Jar包，如果路径以{@link #JAR_SCHEME}开头，
   * 或以{@link #JAR_SUFFIX}结尾，
   * 或者以{@link #FILE_SCHEME}开头 与 包含{@link #JAR_SUFFIX}的路径，表示是Jar包
   *
   * @param path 路径
   * @return 返回判断的结果
   */
  public static boolean isJar(String path) {
    if (path == null) return false;
    if (path.startsWith(JAR_SCHEME)) return true;
    // 可能是springboot的jar包
    return path.startsWith(FILE_SCHEME) && path.contains(JAR_SUFFIX);
  }

  /**
   * 从jar包中拷贝
   *
   * @param url JAR包中资源的路径, 如: new ClassPathResource("application.properties").getURL()
   * @param src 源文件
   * @return 返回读取到的文件或子文件
   */
  public static List<String> list(URL url, String src) {
    List<String> files = new LinkedList<>();
    readJar(url, src, "", (jar, entry, newDest) -> files.add(newDest));
    return files;
  }

  /**
   * 从jar包中拷贝
   *
   * @param url  JAR包中资源的路径, 如: new ClassPathResource("application.properties").getURL()
   * @param src  源文件
   * @param dest 目标文件
   */
  public static void copy(URL url, String src, String dest) {
    if (isJar(url.getPath())) {
      readJar(url, src, dest, JarFileCopy::transferTo);
    } else {
      if (new File(url.getPath()).exists()) {
        FileCopy.copy(new File(url.getPath()), new File(dest));
      } else {
        throw new IllegalStateException("不支持的URL: " + url);
      }
    }
  }

  /**
   * 从jar包中读取
   *
   * @param url  JAR包中资源的路径, 如: new ClassPathResource("application.properties").getURL()
   * @param src  源文件
   * @param dest 目标文件
   */
  public static void readJar(URL url, String src, String dest, Handler handler) {
    if (!isJar(url.getPath())) {
      throw new IllegalStateException("不支持的URL类型: " + url);
    }
    try {
      // 拷贝jar中的文件
      URLConnection conn = url.openConnection();
      if (conn instanceof JarURLConnection) {
        JarURLConnection jarConn = (JarURLConnection) url.openConnection();
        try (final JarFile jar = jarConn.getJarFile();) {
          readJar(jar, src, dest, handler);
        }
      } else {
        try (final JarFile jar = new JarFile(url.getPath());) {
          readJar(jar, src, dest, handler);
        }
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 从jar包中拷贝
   *
   * @param jarFile JAR包, 如: new ClassPathResource("application.properties").getURL()
   * @param src     源文件
   * @param dest    目标文件
   * @param handler 处理
   */
  public static void readJar(JarFile jarFile, String src, String dest, Handler handler) {
    Enumeration<JarEntry> entries = jarFile.entries();
    String stripSrc = replace(trim(src, true, false));
    String stripDest = replace(trim(dest, false, true));
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      if (entry.getName().startsWith(stripSrc)) {
        String name = (replace(entry.getName())).substring(stripSrc.length());
        String newDest = stripDest + "/" + trim(name, true, true);
        handler.handle(jarFile, entry, newDest);
      }
    }
  }

  /**
   * 传输数据
   *
   * @param jar     JAR
   * @param entry   JAR条目
   * @param newDest 新地址
   */
  public static void transferTo(JarFile jar, JarEntry entry, String newDest) {
    try {
      File destFile = new File(newDest);
      if (entry.isDirectory()) {
        destFile.mkdirs();
      } else {
        // 拷贝文件
        IOUtils.write(jar.getInputStream(entry), IOUtils.createFile(destFile));
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  private static String trim(String path, boolean first, boolean last) {
    String out = StringUtils.getIfBlank(path, () -> "").replace("\\", "/").replace("//", "/");
    while (first && out.startsWith("/")) out = out.substring(1);
    while (last && out.endsWith("/")) out = out.substring(0, out.length() - 1);
    return out;
  }

  static String replace(String v) {
    return v != null ? v.replace("\\", "/").replace("//", "/") : v;
  }


  public interface Handler {

    /**
     * 处理
     *
     * @param jar     JAR
     * @param entry   JAR条目
     * @param newDest 新地址
     */
    void handle(JarFile jar, JarEntry entry, String newDest);

  }

}
