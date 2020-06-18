package com.benefitj.netty.server.udp;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 超时检查
 */
public class SimpleExpiredChecker<C extends UdpClient> implements ExpiredChecker<C> {

  private final ThreadLocal<Map<String, C>> localRemovalMap = ThreadLocal.withInitial(LinkedHashMap::new);

  public SimpleExpiredChecker() {
  }

  public Map<String, C> getRemovalMap() {
    return localRemovalMap.get();
  }

  @Override
  public void check(UdpClientManager<C> manager) {
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
            manager.expiredClient(entry.getKey());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        removalMap.clear();
      }
    }
  }
}
