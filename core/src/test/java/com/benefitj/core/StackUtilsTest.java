package com.benefitj.core;

import org.junit.Test;

public class StackUtilsTest extends BaseTest {

  @Test
  public void testPrintStack() {
    System.err.println(StackUtils.getTag(": ") + "hello world!");
  }

  @Test
  public void testFilter() {
    StackUtils.filterClass(StackUtilsTest.class, element
        -> System.err.println("" + element.getClassName() + "." + element.getMethodName() + ", " + element.getLineNumber()));
  }

}