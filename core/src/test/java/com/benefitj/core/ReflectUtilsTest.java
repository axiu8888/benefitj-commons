package com.benefitj.core;

import org.junit.Test;

import java.util.Date;

public class ReflectUtilsTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void testNewInstance() {

    ReflectUtils.newInstance(TestA.class);
    ReflectUtils.newInstance(TestA.class, "厉害", new Date());
    ReflectUtils.newInstance(TestA.class, null, new Date());
    try {
      ReflectUtils.newInstance(TestA.class, 10, new Date());
    } catch (Exception e) {
      System.err.println("抛异常了：" + e.getMessage());
    }

    ReflectUtils.newInstance(TestA.class, new Date(), "厉害2");

  }


  static class TestA {
    public TestA() {
      System.err.printf("3. name[%s], birthday[%s]%n", "...", "...");
    }

    public TestA(Date birthday, String name) {
      System.err.printf("2. name[%s], birthday[%s]%n", name, DateFmtter.fmtDate(birthday));
    }

    public TestA(String name, Date birthday) {
      System.err.printf("1. name[%s], birthday[%s]%n", name, DateFmtter.fmtDate(birthday));
    }

  }

}
