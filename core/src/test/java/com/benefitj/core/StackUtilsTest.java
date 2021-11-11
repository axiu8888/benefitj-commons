package com.benefitj.core;

import org.junit.Test;

public class StackUtilsTest extends BaseTest {

  @Test
  public void testPrintStack() {

//    StackTraceElement stack = StackUtils.currentMethodStack();
    System.err.println(StackUtils.getTag() + ": hello world!");

  }

}