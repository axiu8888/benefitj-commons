package com.benefitj.frameworks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@BeforeEach
	public void setUp() {
		log.info("\n--------------------------- setUp ---------------------------\n");
	}
	
	@AfterEach
	public void tearDown() {
		log.info("\n--------------------------- tearDown ---------------------------\n");
	}
}
