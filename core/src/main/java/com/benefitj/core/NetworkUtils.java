package com.benefitj.core;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NetworkUtils {

  /**
   * 地址是否可达
   *
   * @param host 主机
   * @param port 端口
   * @return 是否可达
   */
  public static boolean isReachable(String host, Integer port) {
    return isReachable(host, port, 2000);
  }

  /**
   * 地址是否可达
   *
   * @param host    主机
   * @param port    端口
   * @param timeout 超时时长
   * @return 是否可达
   */
  public static boolean isReachable(String host, Integer port, int timeout) {
    return isReachable(new InetSocketAddress(host, port), timeout);
  }

  /**
   * 地址是否可达
   *
   * @param address 地址
   * @param timeout 超时时长
   * @return 是否可达
   */
  public static boolean isReachable(InetSocketAddress address, int timeout) {
    try {
      return address.getAddress().isReachable(timeout);
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * 地址是否可连接
   *
   * @param address 地址
   * @param timeout 超时时长
   * @return 是否可连接
   */
  public static boolean isConnectable(InetSocketAddress address, int timeout) {
    try (final Socket sock = new Socket()) {
      sock.connect(address, timeout);
      return sock.isConnected();
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * 获取一个可用的端口
   *
   * @return 返回可用的端口
   */
  public static int availablePort() {
    try (final DatagramSocket socket = new DatagramSocket(0);) {
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 获取主机地址
   *
   * @param address InetSocketAddress
   * @return 返回主机地址或空
   */
  @Nullable
  public static String getHost(SocketAddress address) {
    return address instanceof InetSocketAddress ? ((InetSocketAddress) address).getHostString() : null;
  }

  /**
   * 获取端口
   *
   * @param address InetSocketAddress
   * @return 返回端口或空
   */
  @Nullable
  public static Integer getPort(SocketAddress address) {
    return address instanceof InetSocketAddress ? ((InetSocketAddress) address).getPort() : null;
  }

  /**
   * 获取网络接口
   *
   * @param filter 过滤
   * @return 返回全部的网络接口
   */
  public static List<NetworkInterface> getNetworkInterfaces(Predicate<NetworkInterface> filter) {
    final List<NetworkInterface> list = new LinkedList<>();
    Enumeration<NetworkInterface> interfaces = CatchUtils.ignore(NetworkInterface::getNetworkInterfaces);
    if (interfaces != null) {
      while (interfaces.hasMoreElements()) {
        NetworkInterface network = interfaces.nextElement();
        if (filter.test(network)) {
          list.add(network);
        }
      }
    }
    return list;
  }

  /**
   * 获取网络地址
   *
   * @param network 网络接口
   * @return 返回网络地址
   */
  public static List<InetAddress> getInetAddresses(NetworkInterface network) {
    List<InetAddress> list = new LinkedList<>();
    Enumeration<InetAddress> addresses = network.getInetAddresses();
    while (addresses.hasMoreElements()) {
      InetAddress address = addresses.nextElement();
      if ((address instanceof Inet4Address || address instanceof Inet6Address)) {
        list.add(address);
      }
    }
    return list;
  }

  /**
   * 获取网络地址
   *
   * @return 返回网络地址
   */
  public static List<InetAddress> getInetAddressAll() {
    return getInetAddresses(ni -> true);
  }

  /**
   * 获取网络地址
   *
   * @param filter 过滤网络地址
   * @return 返回网络地址
   */
  public static List<InetAddress> getInetAddresses(Predicate<NetworkInterface> filter) {
    return getNetworkInterfaces(filter)
        .stream()
        .flatMap(ni -> getInetAddresses(ni).stream())
        .collect(Collectors.toList());
  }

  public static InetAddress getLocalHost() {
    return CatchUtils.ignore(InetAddress::getLocalHost);
  }

  /**
   * 获取本机地址
   */
  public static String getLocalHostAddress() {
    return CatchUtils.ignore(() -> InetAddress.getLocalHost().getHostAddress());
  }

  /**
   * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
   *
   * @param ni 网卡
   * @return 如果满足要求则true，否则false
   */
  public static boolean isValidInterface(NetworkInterface ni) {
    return CatchUtils.ignore(() -> {
      if (ni.isLoopback() || ni.isPointToPoint() || ni.isVirtual()) return false;
      return ni.isUp() && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    });
  }

  /**
   * 判断是否是IPv4或IPv6，并且内网地址并过滤回环地址.
   */
  public static boolean isValidAddress(InetAddress address) {
    return (address instanceof Inet4Address || address instanceof Inet6Address)
        && address.isSiteLocalAddress() && !address.isLoopbackAddress();
  }

}
