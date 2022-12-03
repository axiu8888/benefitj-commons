package com.benefitj.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MIME类型
 */
public class MimeTypes {

  /**
   * 获取MIME类型
   *
   * @param filename 文件名
   * @return 返回获取的类型
   */
  public static String get(String filename) {
    return get(filename, "*/*");
  }

  /**
   * 获取MIME类型
   *
   * @param filename     文件名
   * @param defaultValue 默认的类型
   * @return 返回获取的类型
   */
  public static String get(String filename, String defaultValue) {
    int lastIndexOf = filename.lastIndexOf(".");
    if (lastIndexOf >= 0) {
      String suffix = filename.substring(lastIndexOf).toLowerCase(Locale.ROOT);
      return MINME_TYPES.getOrDefault(suffix, defaultValue);
    }
    return defaultValue;
  }

  static final Map<String, String> MINME_TYPES;

  static {
    Map<String, String> map = new HashMap<>();
    map.put(".ofd", "application/ofd");
    map.put(".3gp", "video/3gpp");
    map.put(".apk", "application/vnd.android.package-archive");
    map.put(".asf", "video/x-ms-asf");
    map.put(".avi", "video/x-msvideo");
    map.put(".bin", "application/octet-stream");
    map.put(".bmp", "image/bmp");
    map.put(".c", "text/plain");
    map.put(".class", "application/octet-stream");
    map.put(".conf", "text/plain");
    map.put(".cpp", "text/plain");
    map.put(".doc", "application/msword");
    map.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    map.put(".xls", "application/vnd.ms-excel");
    map.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    map.put(".exe", "application/octet-stream");
    map.put(".gif", "image/gif");
    map.put(".gtar", "application/x-gtar");
    map.put(".gz", "application/x-gzip");
    map.put(".h", "text/plain");
    map.put(".htm", "text/html");
    map.put(".html", "text/html");
    map.put(".jar", "application/java-archive");
    map.put(".java", "text/plain");
    map.put(".jpeg", "image/jpeg");
    map.put(".jpg", "image/jpeg");
    map.put(".js", "application/x-javascript");
    map.put(".log", "text/plain");
    map.put(".m3u", "audio/x-mpegurl");
    map.put(".m4a", "audio/mp4a-latm");
    map.put(".m4b", "audio/mp4a-latm");
    map.put(".m4p", "audio/mp4a-latm");
    map.put(".m4u", "video/vnd.mpegurl");
    map.put(".m4v", "video/x-m4v");
    map.put(".mov", "video/quicktime");
    map.put(".mp2", "audio/x-mpeg");
    map.put(".mp3", "audio/x-mpeg");
    map.put(".mp4", "video/mp4");
    map.put(".mpc", "application/vnd.mpohun.certificate");
    map.put(".mpe", "video/mpeg");
    map.put(".mpeg", "video/mpeg");
    map.put(".mpg", "video/mpeg");
    map.put(".mpg4", "video/mp4");
    map.put(".mpga", "audio/mpeg");
    map.put(".msg", "application/vnd.ms-outlook");
    map.put(".ogg", "audio/ogg");
    map.put(".pdf", "application/pdf");
    map.put(".png", "image/png");
    map.put(".pps", "application/vnd.ms-powerpoint");
    map.put(".ppt", "application/vnd.ms-powerpoint");
    map.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    map.put(".prop", "text/plain");
    map.put(".rc", "text/plain");
    map.put(".rmvb", "audio/x-pn-realaudio");
    map.put(".rtf", "application/rtf");
    map.put(".sh", "text/plain");
    map.put(".tar", "application/x-tar");
    map.put(".tgz", "application/x-compressed");
    map.put(".txt", "text/plain");
    map.put(".wav", "audio/x-wav");
    map.put(".wma", "audio/x-ms-wma");
    map.put(".wmv", "audio/x-ms-wmv");
    map.put(".wps", "application/vnd.ms-works");
    map.put(".xml", "text/plain");
    map.put(".z", "application/x-compress");
    map.put(".zip", "application/x-zip-compressed");
    map.put("", "*/*");
    MINME_TYPES = Collections.unmodifiableMap(map);
  }

}
