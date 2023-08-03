package com.benefitj.core;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeUtilsTest extends BaseTest {

  @Test
  public void getFirstDayOfMonth() {
    log.info("获取当月的第一天: {}", DateFmtter.fmt(TimeUtils.getFirstDayOfMonth(2022, 11)));
  }

  @Test
  public void getLastDayOfMonth() {
    log.info("获取当月的最后一天: {}", DateFmtter.fmt(TimeUtils.getLastDayOfMonth(2022, 11)));
  }

  @Test
  public void getDays() {
    log.info("获取昨天: {}", DateFmtter.fmt(TimeUtils.getYesterday(0, 0, 0)));
    log.info("获取今天: {}", DateFmtter.fmt(TimeUtils.getToday(0, 0, 0)));
    log.info("获取明天: {}", DateFmtter.fmt(TimeUtils.getTomorrow(0, 0, 0)));
  }

  @Test
  public void getAfter() {
    log.info("获取3天后: {}", DateFmtter.fmt(TimeUtils.getAfter(3, TimeUnit.DAYS)));
  }

  @Test
  public void getWeek() {
    log.info("获取周类型: {}", TimeUtils.getWeek(TimeUtils.now()).getName());
  }


}