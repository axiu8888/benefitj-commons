package com.benefitj.frameworks;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.ArrayCopy;
import com.benefitj.core.IOUtils;
import com.benefitj.core.file.IWriter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

class MedianFilterTest extends BaseTest {

  @Test
  void test() {
    String type = "rr";
    File src = new File("D:/tmp/influxdb/hs_darma_mattress.line");
    File dest = new File(src.getParentFile(), src.getName().replace(".line", "_" + type + ".line"));
//    PythonInterpreter interpreter = new PythonInterpreter();
//    File py = ClasspathUtils.getFile("influxdb.py");
//    interpreter.execfile(py.getAbsolutePath());
    MedianFilter filter = new MedianFilter(30);
    IWriter writer = IWriter.createWriter(dest, false);
    IOUtils.readLines(src, (line, lineNumber) -> {
      LineProtocol lineProtocol = parseLine(line);
      if (Objects.equals(lineProtocol.tags.get("type"), "bcg")) {
        String text = (String) lineProtocol.fields.get(type + "_points");
        int[] wave = filter.process(JSON.parseObject(text, int[].class));
        writer.writeAndFlush(JSON.toJSONString(wave)).writeAndFlush("\n");
      }
    });
  }


  static class MedianFilter {
    /**
     * 窗口大小为3，取中间值
     */
    int windowSize;
    /**
     * 窗口
     */
    int[] window;

    final ArrayCopy<int[]> copy = ArrayCopy.newIntArrayCopy();

    public MedianFilter(int windowSize) {
      this.windowSize = windowSize;
      this.window = new int[windowSize];
    }

    public int[] process(int[] input) {
      int[] output = copy.getCache(input.length);
      for (int i = 0; i < input.length; i++) {
        for (int j = 1; j < window.length; j++) {
          window[j - 1] = window[j];
        }
        window[window.length - 1] = input[i];
        int[] buf = copy.copy(window);
        Arrays.sort(buf);
        output[i] = buf[windowSize / 2];
      }
      return output;
    }
  }


  /**
   * 解析行协议
   *
   * @param line 数据行
   * @return 返回行协议对象
   */
  public static LineProtocol parseLine(String line) {
    if (StringUtils.isBlank(line)) {
      throw new IllegalArgumentException("数据不能为空");
    }
    LineProtocol lineProtocol = new LineProtocol();
    lineProtocol.setTags(new LinkedHashMap<>());
    lineProtocol.setFields(new LinkedHashMap<>());
    // 引号
    boolean hasSingleQuote = false, hasDoubleQuote = false, escape = false;
    int stage = 0;
    for (int i = 0, startAt = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (escape || ch == '\\') {
        // 有转义符，跳过
        escape = !escape;
      } else {
        if (hasSingleQuote || hasDoubleQuote) {
          // 有引号，需要退出引号后记录
          if (hasSingleQuote && ch == '/') {
            hasSingleQuote = false;
          }
          if (hasDoubleQuote && ch == '"') {
            hasDoubleQuote = false;
          }
        } else {
          if (ch == ' ' || ch == ',') {
            // 0 =>: tag和字段的分割
            switch (stage) {
              case 0:
                lineProtocol.setMeasurement(line.substring(startAt, i));
                stage++;// tag
                break;
              case 1: {
                String str = line.substring(startAt, i);
                if (StringUtils.isNotBlank(str)) {
                  String[] splits = str.split("=");
                  lineProtocol.getTags().put(splits[0], splits[1]);
                }
              }
              break;
              case 2: {
                String str = line.substring(startAt, i);
                String[] splits = str.split("=");
                lineProtocol.getFields().put(splits[0], parseValue(splits[1]));
              }
              break;
              case 3:
                lineProtocol.setTime(Long.parseLong(line.substring(startAt)));
                break;
            }
            stage = stage + (ch == ' ' ? 1 : 0);
            startAt = i + 1;
          } else {
            if (ch == '\'') {
              hasSingleQuote = true;
            }
            if (ch == '"') {
              hasDoubleQuote = true;
            }
          }
        }
      }
    }
    int spaceLastIndexOf = line.lastIndexOf(" ");
    if (spaceLastIndexOf >= 0) {
      lineProtocol.setTime(Long.parseLong(line.substring(spaceLastIndexOf).trim()));
    } else {
      throw new IllegalStateException("缺少时间戳");
    }
    return lineProtocol;
  }

  /**
   * 解析值
   *
   * @param value 值
   * @return 返回解析的数据
   */
  public static Object parseValue(String value) {
    if (value.startsWith("\"") && value.endsWith("\"")) {
      return value.substring(1, value.length() - 1);
    }
    if (value.endsWith("i")) {
      return Long.parseLong(value.substring(0, value.length() - 1));
    }
    switch (value.toUpperCase()) {
      case "false":
      case "true":
        return Boolean.parseBoolean(value);
      default:
        return Double.parseDouble(value.substring(0, value.length() - 1));
    }
  }

  @Data
  @NoArgsConstructor
  @SuperBuilder
  public static class LineProtocol {
    /**
     * 表名
     */
    String measurement;
    /**
     * 时间戳
     */
    long time;
    /**
     * TAG
     */
    @Builder.Default
    Map<String, String> tags = new LinkedHashMap<>();
    /**
     * 字段和值
     */
    @Builder.Default
    Map<String, Object> fields = new LinkedHashMap<>();
  }
}
