package com.benefitj.core.cron;

/**
 * - "second": 秒 (0-59, 默认0)
 * - "minute": 分 (0-59, 默认0)
 * - "hour": 时 (0-23, 默认0)
 * - "day": 日 (1-31, 默认*)
 * - "month": 月 (1-12, 默认*)
 * - "year": 年 (1970-2099, 默认*)
 * - "dayOfWeek": 周 (1-7, SUN=1, SAT=7, 默认?)
 * - "special": 特殊标识 ("last"=最后一天, "workday"=工作日, "lastWorkday"=最后一个工作日)
 */
public enum CronTimeUnit {
  /**
   * 秒 (0-59, 默认0)
   */
  second,
  /**
   * 分 (0-59, 默认0)
   */
  minute,
  /**
   * 时 (0-23, 默认0)
   */
  hour,
  /**
   * 日 (1-31, 默认*)
   */
  day,
  /**
   * 月 (1-12, 默认*)
   */
  month,
  /**
   * 年 (1970-2099, 默认*)
   */
  year,
  /**
   * 周 (1-7, SUN=1, SAT=7, 默认?)
   */
  dayOfWeek,
  /**
   * 特殊标识 ("last"=最后一天, "workday"=工作日, "lastWorkday"=最后一个工作日)
   */
  special,
  ;

}
