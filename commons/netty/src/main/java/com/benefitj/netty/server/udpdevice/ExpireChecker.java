package com.benefitj.netty.server.udpdevice;

import com.benefitj.netty.log.NettyLogger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 过期检查
 */
public interface ExpireChecker<C extends UdpDeviceClient> {

  /**
   * 检查过期的客户端
   *
   * @param manager
   */
  void check(UdpDeviceClientManager<C> manager);

  /**
   * 默认过期检查的实现
   */
  static <C extends UdpDeviceClient> ExpireChecker<C> newInstance() {
    return new DefaultExpireChecker<>();
  }


  /**
   * UDP设备客户端超时检查
   */
  static class DefaultExpireChecker<C extends UdpDeviceClient> implements ExpireChecker<C> {

    private static final NettyLogger log = NettyLogger.INSTANCE;

    private final ThreadLocal<Map<String, C>> localRemovalMap = ThreadLocal.withInitial(LinkedHashMap::new);

    public DefaultExpireChecker() {
    }

    public Map<String, C> getRemovalMap() {
      return localRemovalMap.get();
    }

    @Override
    public void check(UdpDeviceClientManager<C> manager) {
      if (!manager.isEmpty()) {
        final Map<String, C> removalMap = getRemovalMap();
        for (Map.Entry<String, C> entry : manager.entrySet()) {
          final C client = entry.getValue();
          // 上线时间和接收到最近一个UDP包的时间都超时
          if (isExpired(client, manager)) {
            removalMap.put(entry.getKey(), client);
          }
        }

        if (!removalMap.isEmpty()) {
          for (Map.Entry<String, C> entry : removalMap.entrySet()) {
            try {
              entry.getValue().execute(() -> manager.expire(entry.getKey()));
            } catch (Exception e) {
              log.warn("throws on expired device: " + e.getMessage());
            }
          }
          removalMap.clear();
        }
      }
    }

    /**
     * 判断是否过期
     *
     * @param client  客户端
     * @param manager 客户端管理
     * @return 返回是否过期
     */
    public boolean isExpired(C client, UdpDeviceClientManager<C> manager) {
      long now = System.currentTimeMillis();
      return (now - client.getOnlineTime() > manager.getExpired())
          && (now - client.getRecvTime() >= manager.getExpired());
    }

  }

}
