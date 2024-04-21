package com.benefitj.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  @BeforeEach
  public void setUp() throws Exception {
    log.info("-------------  setUp   ----------------" + DateFmtter.fmtNowS());
  }

  @AfterEach
  public void tearDown() throws Exception {
    log.info("------------- tearDown ----------------" + DateFmtter.fmtNowS());
  }

}
