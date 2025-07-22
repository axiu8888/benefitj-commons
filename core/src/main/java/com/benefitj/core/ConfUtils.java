package com.benefitj.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 配置解析
 */
public class ConfUtils {

  /**
   * ini或conf文件解析
   */
  public static class Ini {

    /**
     * 加载配置
     *
     * @param conf 配置文件
     * @return 返回解析后的结果
     */
    public static JSONObject load(File conf) {
      String content = IOUtils.readAsString(conf);
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


  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * TOML 解析
   */
  public static class Toml {

    /**
     * 单双引号、转义符
     */
    public static final String STRING_S_D_E = "['\"]((?:\\\\.|[^'\"]*))['\"]";

    public static final String[] STR_SYMBOL = {"\"\"\"", "'''", "```"};

    /**
     * 加载配置
     *
     * @param conf 配置文件
     * @return 返回解析后的结果
     */
    public static JSONObject load(File conf) {
      String content = IOUtils.readAsString(conf);
      return load(content);
    }

    /**
     * 加载配置
     *
     * @param content 配置内容
     * @return 返回解析后的结果
     */
    public static JSONObject load(String content) {
      String[] lines = Stream.of(content
              .replace("\r\n", "\n")
              .replace("\r", "\n")
              .split("\n"))
          .map(String::trim)
          .toArray(String[]::new);
      JSONObject root = new JSONObject(new LinkedHashMap<>());
      String key = null, value;
      JSONObject context = root;
      boolean[] multiStrFlags = new boolean[]{false, false, false}; // 是否有多行字符串
      StringBuilder ctxMultiLines = new StringBuilder();
      for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        int multiStringIndex = indexMultiString(multiStrFlags);
        if (multiStringIndex >= 0) {
          ctxMultiLines.append("\n").append(line);
          String symbol = STR_SYMBOL[multiStringIndex];
          if (line.indexOf(symbol) >= 0) {
            parseMultiString(context, key, ctxMultiLines, multiStrFlags);
          }
        } else {
          if (StringUtils.isNotBlank(line) && !(line.startsWith("#") || line.startsWith(";"))) {
            int commentAt = line.indexOf('#');
            line = commentAt >= 0 ? line.substring(0, commentAt).trim() : line;
            if (ctxMultiLines.length() <= 0) {
              if (line.startsWith("[") && line.endsWith("]")) {
                // 新的上下文
                String ctxName = line.substring(1, line.length() - 1).trim();
                String[] ctxNames = ctxName.split("\\.");
                JSONObject localCtx;
                for (int j = 0; j < ctxNames.length; j++) {
                  String name = ctxNames[j];
                  localCtx = j == 0 ? root : context;
                  context = localCtx.getJSONObject(name);
                  if (context == null) {
                    localCtx.put(name, context = new JSONObject());
                  }
                }
                continue;
              }
              int splitAt = line.indexOf('=');
              splitAt = splitAt >= 0 ? splitAt : line.indexOf(':');
              key = line.substring(0, splitAt);
              value = line.substring(splitAt + 1).trim();
              if (value.startsWith("[")) {
                // 数组
                ctxMultiLines.append(value);
                // 检查数组是否匹配结束
                parseArray(context, key, ctxMultiLines);
              } else {
                // 先检查多行文本
                String multiStrSymbol = null;
                int index = -1;
                for (int j = 0; j < STR_SYMBOL.length; j++) {
                  String symbol = STR_SYMBOL[j];
                  if (value.startsWith(symbol)) {
                    multiStrSymbol = symbol;
                    index = j;
                    break;
                  }
                }
                if (StringUtils.isNotBlank(multiStrSymbol)) {
                  ctxMultiLines.append(value);
                  multiStrFlags[index] = true;
                  if (value.indexOf(multiStrSymbol, multiStrSymbol.length()) >= 0) {
                    parseMultiString(context, key, ctxMultiLines, multiStrFlags);
                  }
                  continue;
                }
                parseValue(context, key, value);
              }
            } else {
              // 多行数据
              ctxMultiLines.append(line);
              // 检查数组是否匹配结束
              parseArray(context, key, ctxMultiLines);
            }
          }
        }
      }
      return root;
    }

    /**
     * 解析多行文本数据
     *
     * @param context       上下文
     * @param key           键
     * @param ctxMultiLines
     * @param multiStrFlags
     */
    private static void parseMultiString(JSONObject context, String key, StringBuilder ctxMultiLines, boolean[] multiStrFlags) {
      Arrays.fill(multiStrFlags, false);
      context.put(key, ctxMultiLines.substring(3, ctxMultiLines.length() - 3));
      ctxMultiLines.setLength(0);
    }

    /**
     * 解析数据
     *
     * @param context 上下文
     * @param key     键
     * @param lines   缓存的行数据
     */
    private static void parseArray(JSONObject context, String key, StringBuilder lines) {
      int count = 0;
      for (int i = 0, ch; i < lines.length(); i++) {
        ch = lines.charAt(i);
        count += ch == '[' ? 1 : 0;
        count += ch == ']' ? -1 : 0;
      }
      if (count == 0) {
        context.put(key, JSON.parseArray(lines.toString()));
        lines.setLength(0);
      }
    }

    /**
     * 解析值
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    private static void parseValue(JSONObject context, String key, String value) {
      if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
        context.put(key, value.substring(1, value.length() - 2));
      } else if (value.indexOf('.') > 0) {
        context.put(key, Double.parseDouble(value));
      } else if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
        context.put(key, Boolean.parseBoolean(value));
      } else {
        if (DateFmtter.isOffsetDate(value)) {
          context.put(key, DateFmtter.parseOffset(value));
        } else {
          Date date = null;
          List<String> patterns = Arrays.asList(
              "yyyy-MM-dd'T'HH:mm.ss.SSS'Z'",
              "yyyy-MM-dd'T'HH:mm.ss'Z'",
              "yyyy-MM-dd'T'HH:mm'Z'",
              "yyyy-MM-dd HH:mm:ss.SSS",
              "yyyy-MM-dd HH:mm:ss",
              "yyyy-MM-dd HH:mm",
              "yyyy-MM-dd"
          );
          for (String pattern : patterns) {
            date = DateFmtter.parse(value, pattern);
            if (date != null) {
              break;
            }
          }
          if (date != null) {
            context.put(key, date);
          } else if (value.startsWith("0b")) {
            context.put(key, Integer.parseInt(value, 2));
          } else if (value.startsWith("0o")) {
            context.put(key, Integer.parseInt(value, 8));
          } else if (value.startsWith("0x")) {
            context.put(key, Integer.parseInt(value, 16));
          } else if (Long.parseLong(value) > Integer.MAX_VALUE) {
            context.put(key, Long.parseLong(value));
          } else {
            context.put(key, Integer.parseInt(value));
          }
        }
      }
    }

    /**
     * 获取字符串数组的下表
     *
     * @param array 数据
     * @return
     */
    private static int indexMultiString(boolean[] array) {
      for (int i = 0; i < array.length; i++) {
        if (array[i]) {
          return i;
        }
      }
      return -1;
    }
  }
}
