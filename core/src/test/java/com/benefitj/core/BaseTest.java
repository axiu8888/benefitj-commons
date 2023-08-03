package com.benefitj.core;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() throws Exception {
		log.info("-------------  setUp   ----------------" + DateFmtter.fmtNowS());
	}

	@After
	public void tearDown() throws Exception {
		log.info("------------- tearDown ----------------" + DateFmtter.fmtNowS());
	}

}
