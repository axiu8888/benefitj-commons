package com.benefitj.core;

import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.stream.Collectors;

public class NetworkUtilsTest extends BaseTest {


  @Test
  public void test() {
    logger.info("getLocalHostAddress ==>: {}", NetworkUtils.getLocalHostAddress());
    logger.info("getInetAddressAll ==>: {}", NetworkUtils.getInetAddressAll());
    logger.info("getInetAddressAll ==>: {}", NetworkUtils.getInetAddressAll()
        .stream()
        .map(inetAddress -> inetAddress.getClass().getSimpleName() + "(" + inetAddress + ")")
        .collect(Collectors.toList()));

    InetSocketAddress address = new InetSocketAddress("192.168.1.198", 62014);
    logger.info("getHost ==>: {}", NetworkUtils.getHost(address));
    logger.info("getPort ==>: {}", NetworkUtils.getPort(address));
  }

}