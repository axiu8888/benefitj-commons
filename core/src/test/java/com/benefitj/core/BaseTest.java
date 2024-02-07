package com.benefitj.core;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() throws Exception {
		log.info("-------------  setUp   ----------------" + DateFmtter.fmtNowS());
	}

	@AfterEach
	public void tearDown() throws Exception {
		log.info("------------- tearDown ----------------" + DateFmtter.fmtNowS());
	}

}
