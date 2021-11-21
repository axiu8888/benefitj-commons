package com.benefitj.core;

import junit.framework.TestCase;
import org.junit.Test;

public class UnitTest extends TestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMB() {
    System.err.println("MB: " + Unit.ofMB(122, 4));
  }


  public void tearDown() throws Exception {
  }
}