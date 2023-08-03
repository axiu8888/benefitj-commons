package com.benefitj.core;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class NetworkUtilsTest extends BaseTest {


  @Test
  public void test() {
    log.info("getLocalHostAddress ==>: {}", NetworkUtils.getLocalHostAddress());
    log.info("getInetAddressAll ==>: {}", NetworkUtils.getInetAddressAll());
    log.info("getInetAddressAll ==>: {}", NetworkUtils.getInetAddressAll()
        .stream()
        .map(inetAddress -> inetAddress.getClass().getSimpleName() + "(" + inetAddress + ")")
        .collect(Collectors.toList()));

    InetSocketAddress address = new InetSocketAddress("192.168.1.198", 62014);
    log.info("getHost ==>: {}", NetworkUtils.getHost(address));
    log.info("getPort ==>: {}", NetworkUtils.getPort(address));
  }

}