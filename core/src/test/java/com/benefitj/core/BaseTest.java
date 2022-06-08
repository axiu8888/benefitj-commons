package com.benefitj.core;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public abstract class BaseTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() {
		logger.info("------------- setUp ----------------" + DateFmtter.fmtNowS());
	}

	@After
	public void tearDown() {
		logger.info("------------- tearDown ----------------" + DateFmtter.fmtNowS());
	}

}
