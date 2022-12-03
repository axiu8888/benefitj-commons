package com.benefitj.core;

import org.junit.Test;

public class TimeUtilsTest extends BaseTest {

  @Test
  public void getFirstDayOfMonth() {
    logger.info("获取当月的第一天: {}", DateFmtter.fmt(TimeUtils.getFirstDayOfMonth(2022, 11)));
  }

  @Test
  public void getLastDayOfMonth() {
    logger.info("获取当月的最后一天: {}", DateFmtter.fmt(TimeUtils.getLastDayOfMonth(2022, 11)));
  }

  @Test
  public void getWeek() {
    logger.info("获取周类型: {}", TimeUtils.getWeek(TimeUtils.now()).getName());
  }


}