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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * jar包文件拷贝
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
   * @param url  JAR包中资源的路径，如: new ClassPathResource("application.properties").getURL()
   * @param src  源文件
   * @param dest 目标文件
   */
  public static void copy(URL url, String src, String dest) {
    try {
      // 拷贝jar中的文件
      URLConnection conn = url.openConnection();
      if (conn instanceof JarURLConnection) {
        JarURLConnection jarConn = (JarURLConnection) url.openConnection();
        try (JarFile jf = jarConn.getJarFile();) {
          copy(jf, src, dest);
        }
      } else {
        try (JarFile jf = new JarFile(url.getPath());) {
          copy(jf, src, dest);
        }
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 从jar包中拷贝
   *
   * @param jarFile JAR包，如: new ClassPathResource("application.properties").getURL()
   * @param src     源文件
   * @param dest    目标文件
   */
  public static void copy(JarFile jarFile, String src, String dest) {
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
            IOUtils.write(jarFile.getInputStream(entry), IOUtils.createFile(destFile));
          }
        }
      }
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  private static String trim(String path, boolean first, boolean last) {
    String out = StringUtils.getIfBlank(path, () -> "")
        .replace("\\", "/")
        .replace("//", "/");
    while (first && out.startsWith("/")) out = out.substring(1);
    while (last && out.endsWith("/")) out = out.substring(0, out.length() - 1);
    return out;
  }

}
