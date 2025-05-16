package com.benefitj.core;

import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

public class UtilsTest extends BaseTest {

  @Test
  public void testMB() {
    System.err.println("MB: " + Utils.fmtKB(122, "0.00"));
    System.err.println("MB: " + Utils.fmtMB(8 * 1024 + 10.5 * Utils.MB, "0.00"));
  }

  @Test
  public void testIp() {
    List<InetAddress> ipAddress = NetworkUtils.getInetAddresses(NetworkUtils::isValidInterface);
    System.err.println(ipAddress.stream()
        .filter(NetworkUtils::isValidAddress)
        // 仅获取IPv4的地址
        .filter(inetAddress -> inetAddress instanceof Inet4Address)
        .map(InetAddress::getHostAddress)
        .collect(Collectors.joining("\n")));
    System.err.println("\n");
    System.err.println("本机IP: " + NetworkUtils.getLocalHostAddress());
    System.err.println("\n");

  }

  @Test
  public void test() {
    new A();
  }

  public static class A {

    A() {
      System.err.println("A");
    }

    {
      init();
    }

    public void init() {
      System.err.println("init");
    }
  }

  @Test
  void test_md5Id() {
    log.info("md5 hello -->: {}", IdUtils.md5Id("hello"));
    log.info("md5 1 -->: {}", IdUtils.md5Id("1"));
  }
}