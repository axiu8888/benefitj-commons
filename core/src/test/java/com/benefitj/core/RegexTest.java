package com.benefitj.core;

import org.junit.jupiter.api.Test;

import java.util.List;

class RegexTest extends BaseTest {

  @Test
  void matches() {
    Regex regex = new Regex(Regex.DATE_yMdHms);
    System.err.println("fmtNowS ==>: " + regex.matches(DateFmtter.fmtNowS()));
    System.err.println("fmtNow ==>: " + regex.matches(DateFmtter.fmtNow()));
    System.err.println(" ==>: " + DateFmtter.fmtNow());

    List<String> strings = regex.find(DateFmtter.fmtNow());
    System.err.println(strings);
  }

  @Test
  void test_placeHolder() {
    System.err.println(PlaceHolder.get().format("name: {}, age: {}", "李白", 233));
    System.err.println(PlaceHolder.get().format(false,"name: {abc}, age: {}", "李白", 233));
    System.err.println(PlaceHolder.get().format(true,"name: {abc}, age: {}", "李白", 233));
  }

}
