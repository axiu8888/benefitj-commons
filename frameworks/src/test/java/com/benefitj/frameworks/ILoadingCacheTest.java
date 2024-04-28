package com.benefitj.frameworks;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.frameworks.cache.ILoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class ILoadingCacheTest extends BaseTest {

  @Test
  void test() {
    ILoadingCache<String, String> cache = ILoadingCache.wrap(CacheBuilder.newBuilder()
            .initialCapacity(20)
            //.expireAfterAccess(5, TimeUnit.SECONDS)
            .expireAfterWrite(5, TimeUnit.SECONDS) // 写入超时
            .refreshAfterWrite(Duration.ofNanos(1)) // 刷新
            .removalListener(new RemovalListener<String, String>() {
              @Override
              public void onRemoval(RemovalNotification<String, String> notification) {
                log.info("onRemoval, key: {}, value: {}, wasEvicted: {}, cause: {}"
                    , notification.getKey()
                    , notification.getValue()
                    , notification.wasEvicted()
                    , JSON.toJSONString(notification.getCause())
                );
              }
            })
        //, new ConcurrentHashMap<>()
        , (loader, key) -> {
          log.info("load: {}", key);
          return loader.map.get(key);
        });

    CountDownLatch latch = new CountDownLatch(1);

    EventLoop.asyncIO(() -> {
      for (int i = 0; i < 100; i++) {
        cache.put("k-" + i, "v-" + i);
      }
      EventLoop.sleepSecond(1);
      EventLoop.asyncIOFixedRate(() -> {
        //cache.cleanUp();
        ConcurrentMap<String, String> map = cache.asMap();
        map.forEach((key, v) -> {
          if (key.equals("k-2")) {
            cache.refresh(key);
          } else {
            cache.getIfPresent(key);
//            cache.get(key);
            cache.getUnchecked(key);
          }
        });
        log.info("size: {}, all: {}", cache.size(), map);
      }, 1, 1, TimeUnit.SECONDS);

      EventLoop.sleepSecond(20);
      latch.countDown();

    });

    CatchUtils.ignore(() -> latch.await());
  }

}