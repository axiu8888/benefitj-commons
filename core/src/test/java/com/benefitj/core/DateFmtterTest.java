package com.benefitj.core;

import org.junit.Test;

import java.time.OffsetDateTime;

public class DateFmtterTest extends BaseTest {

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
  }

  @Test
  public void test_isOffsetDate() {
    String iosDateTime = "1979-05-27T07:32:00-08:00";
    log.info("isOffsetDate: {}", DateFmtter.isOffsetDate(iosDateTime));
    OffsetDateTime time = DateFmtter.parseOffset(iosDateTime);
    log.info("parseOffset: {}", time);
    log.info("getOffset: {}", time.getOffset());
  }

}