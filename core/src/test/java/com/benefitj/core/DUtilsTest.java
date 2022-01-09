package com.benefitj.core;

import junit.framework.TestCase;
import org.junit.Test;

public class DUtilsTest extends TestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMB() {
    System.err.println("MB: " + DUtils.fmtKB(122, "0.00"));
    System.err.println("MB: " + DUtils.fmtMB(8 * 1024 + 10.5 * DUtils.MB, "0.00"));
  }


  public void tearDown() throws Exception {
  }
}