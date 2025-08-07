package com.benefitj.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


@Slf4j
class CatchUtilsTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void test_getStackTraceString() {
    String msg = CatchUtils.getLogStackTrace(new Exception("呵呵"));
    log.info(msg);
  }

}