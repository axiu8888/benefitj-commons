package com.benefitj.netty.server.udpdevice;

import com.benefitj.netty.log.NettyLogger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UDP设备客户端超时检查
 */
public class DefaultExpireChecker<C extends UdpDeviceClient> implements ExpireChecker<C> {

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
      long now = System.currentTimeMillis();
      final Map<String, C> removalMap = getRemovalMap();
      for (Map.Entry<String, C> entry : manager.entrySet()) {
        final C client = entry.getValue();
        // 上线时间和接收到最近一个UDP包的时间都超时
        if ((now - client.getOnlineTime() > manager.getExpired())
            && (now - client.getLastRecvTime() >= manager.getExpired())) {
          removalMap.put(entry.getKey(), client);
        }
      }

      if (!removalMap.isEmpty()) {
        for (Map.Entry<String, C> entry : removalMap.entrySet()) {
          try {
            manager.expire(entry.getKey());
          } catch (Exception e) {
            log.warn("throws on expired device: " + e.getMessage());
          }
        }
        removalMap.clear();
      }
    }
  }
}
