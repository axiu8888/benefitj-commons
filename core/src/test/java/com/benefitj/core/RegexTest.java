package com.benefitj.core;

import org.junit.Test;

import java.util.List;

public class RegexTest extends BaseTest {

  @Test
  public void matches() {
    Regex regex = new Regex(Regex.DATE_yMdHms);
    System.err.println("fmtNowS ==>: " + regex.matches(DateFmtter.fmtNowS()));
    System.err.println("fmtNow ==>: " + regex.matches(DateFmtter.fmtNow()));
    System.err.println(" ==>: " + DateFmtter.fmtNow());

    List<String> strings = regex.find(DateFmtter.fmtNow());
    System.err.println(strings);

  }
}