package com.benefitj.frameworks;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;

public abstract class BaseTest {

	@Before
	public abstract void setUp();
	
	@AfterEach
	public abstract void tearDown();
}
