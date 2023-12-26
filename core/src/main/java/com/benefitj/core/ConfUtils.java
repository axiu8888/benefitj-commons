package com.benefitj.core;

import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * INI文件解析
 */
public class ConfUtils {

  /**
   * 加载配置
   *
   * @param conf 配置文件
   * @return 返回解析后的结果
   */
  public static JSONObject load(File conf) {
    String content = IOUtils.readFileAsString(conf);
    return load(content);
  }

  /**
   * 加载配置
   *
   * @param content 配置内容
   * @return 返回解析后的结果
   */
  public static JSONObject load(String content) {
    List<String> lines = Stream.of(content
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .split("\n"))
        .map(String::trim)
        .filter(str -> !str.isBlank())
        .collect(Collectors.toList());
    JSONObject json = new JSONObject(new LinkedHashMap<>());
    String key, value;
    for (String line : lines) {
      if (!(line.startsWith("#") || line.startsWith(";"))) {
        int commentAt = line.indexOf('#');
        if (commentAt >= 0) {
          line = line.substring(0, commentAt);
        }
        int splitAt = line.indexOf('=');
        splitAt = splitAt >= 0 ? splitAt : line.indexOf(':');
        splitAt = splitAt >= 0 ? splitAt : line.indexOf(' ');
        key = line.substring(0, splitAt);
        value = line.substring(splitAt + 1).trim();
        json.put(key, value);
      }
    }
    return json;
  }
}
