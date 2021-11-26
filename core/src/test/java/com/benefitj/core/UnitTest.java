package com.benefitj.core;

import junit.framework.TestCase;
import org.junit.Test;

public class UnitTest extends TestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMB() {
    System.err.println("MB: " + Unit.fmtKB(122, "0.00"));
    System.err.println("MB: " + Unit.fmtMB(8 * 1024 + 10.5 * Unit.MB, "0.00"));
  }


  public void tearDown() throws Exception {
  }
}