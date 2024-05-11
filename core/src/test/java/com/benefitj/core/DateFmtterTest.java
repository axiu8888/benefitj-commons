package com.benefitj.core;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

class DateFmtterTest extends BaseTest {

  @Test
  void test_isOffsetDate() {
    String iosDateTime = "1979-05-27T07:32:00-08:00";
    log.info("isOffsetDate: {}", DateFmtter.isOffsetDate(iosDateTime));
    OffsetDateTime time = DateFmtter.parseOffset(iosDateTime);
    log.info("parseOffset: {}", time);
    log.info("getOffset: {}", time.getOffset());
  }

}