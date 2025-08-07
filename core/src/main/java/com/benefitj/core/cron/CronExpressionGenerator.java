package com.benefitj.core.cron;


import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;


public class CronExpressionGenerator {

  /**
   * 生成Cron表达式
   *
   * @param params 包含以下字段的Map：
   *               - "second": 秒 (0-59, 默认0)
   *               - "minute": 分 (0-59, 默认0)
   *               - "hour": 时 (0-23, 默认0)
   *               - "day": 日 (1-31, 默认*)
   *               - "month": 月 (1-12, 默认*)
   *               - "year": 年 (1970-2099, 默认*)
   *               - "dayOfWeek": 周 (1-7, SUN=1, SAT=7, 默认?)
   *               - "special": 特殊标识 ("last"=最后一天, "workday"=工作日, "lastWorkday"=最后一个工作日)
   * @return 标准Cron表达式
   */
  public static String generate(Map<CronTimeUnit, String> params) {
    String second = params.getOrDefault(CronTimeUnit.second, "0");
    String minute = params.getOrDefault(CronTimeUnit.minute, "0");
    String hour = params.getOrDefault(CronTimeUnit.hour, "0");
    String day = params.getOrDefault(CronTimeUnit.day, "*");
    String month = params.getOrDefault(CronTimeUnit.month, "*");
    String year = params.getOrDefault(CronTimeUnit.year, "*");
    String dayOfWeek = params.getOrDefault(CronTimeUnit.dayOfWeek, "?");
    String special = params.getOrDefault(CronTimeUnit.special, "");

    // 处理特殊日期标识
    switch (special.toLowerCase()) {
      case "last":
        day = "L";
        break;
      case "workday":
        day = "W";
        break;
      case "lastworkday":
        day = "LW";
        break;
    }

    // 处理日和周互斥逻辑
    if (!"?".equals(dayOfWeek)) {
      day = "?";  // 如果指定了周，则日设为?
    } else if (!"?".equals(day)) {
      dayOfWeek = "?";  // 如果指定了日，则周设为?
    }

    // 构建Cron表达式
    return String.format("%s %s %s %s %s %s %s",
        validateField(second, 0, 59),
        validateField(minute, 0, 59),
        validateField(hour, 0, 23),
        day,
        validateField(month, 1, 12),
        dayOfWeek,
        year
    );
  }

  /**
   * 验证字段值是否在有效范围内
   */
  private static String validateField(String value, int min, int max) {
    if ("*".equals(value) || "?".equals(value) ||
        value.contains(",") || value.contains("-") ||
        value.contains("/") || value.contains("L") ||
        value.contains("W")) {
      return value;
    }

    try {
      int num = Integer.parseInt(value);
      if (num >= min && num <= max) {
        return value;
      }
      throw new IllegalArgumentException(
          String.format("值 %s 超出范围 [%d-%d]", value, min, max)
      );
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无效的数值: " + value);
    }
  }

  /**
   * 星期枚举（兼容多种表示法）
   */
  public enum DayOfWeek {
    SUN(1, "SUN", "周日"),
    MON(2, "MON", "周一"),
    TUE(3, "TUE", "周二"),
    WED(4, "WED", "周三"),
    THU(5, "THU", "周四"),
    FRI(6, "FRI", "周五"),
    SAT(7, "SAT", "周六");

    private final int value;
    private final String abbr;
    private final String cnName;

    private static final Map<String, DayOfWeek> ABBR_MAP =
        EnumSet.allOf(DayOfWeek.class).stream()
            .collect(Collectors.toMap(e -> e.abbr, e -> e));

    private static final Map<String, DayOfWeek> CN_MAP =
        EnumSet.allOf(DayOfWeek.class).stream()
            .collect(Collectors.toMap(e -> e.cnName, e -> e));

    DayOfWeek(int value, String abbr, String cnName) {
      this.value = value;
      this.abbr = abbr;
      this.cnName = cnName;
    }

    public static String parse(String input) {
      if (input == null || input.trim().isEmpty()) {
        return "?";
      }

      // 尝试解析为数字
      try {
        int num = Integer.parseInt(input);
        if (num >= 1 && num <= 7) {
          return String.valueOf(num);
        }
      } catch (NumberFormatException ignored) {}

      // 尝试英文缩写
      DayOfWeek dow = ABBR_MAP.get(input.toUpperCase());
      if (dow != null) {
        return String.valueOf(dow.value);
      }

      // 尝试中文名称
      dow = CN_MAP.get(input);
      if (dow != null) {
        return String.valueOf(dow.value);
      }

      throw new IllegalArgumentException("无效的星期表示: " + input);
    }
  }

//  // 使用示例
//  public static void main(String[] args) {
//    // 示例1：每天8:30执行
//    log.info("【 0 30 8 * * ? * 】 ===>: {}", CronExpressionGenerator.generateCron(Map.of(
//        "hour", "8",
//        "minute", "30"
//    ))); // 0 30 8 * * ? *
//
//    // 示例2：每周一9:00执行
//    log.info("【 0 0 9 ? * 2 * 】 ===>: {}", CronExpressionGenerator.generateCron(Map.of(
//        "dayOfWeek", "MON", // 或"2"或"周一"
//        "hour", "9"
//    ))); // 0 0 9 ? * 2 *
//
//    // 示例3：每月最后一天23:59执行
//    log.info("【 0 59 23 L * ? * 】 ===>: {}", CronExpressionGenerator.generateCron(Map.of(
//        "special", "last",
//        "hour", "23",
//        "minute", "59"
//    ))); // 0 59 23 L * ? *
//
//    // 示例4：2025年每月第一个工作日12:00执行
//    log.info("【 0 0 12 W * ? 2025 】 ===>: {}", CronExpressionGenerator.generateCron(Map.of(
//        "special", "workday",
//        "hour", "12",
//        "year", "2025"
//    ))); // 0 0 12 W * ? 2025
//
//    // 示例5：每5分钟执行一次
//    log.info("【 0 0/5 * * * ? * 】 ===>: {}", CronExpressionGenerator.generateCron(Map.of(
//        "minute", "0/5"
//    ))); // 0 0/5 * * * ? *
//  }
}
