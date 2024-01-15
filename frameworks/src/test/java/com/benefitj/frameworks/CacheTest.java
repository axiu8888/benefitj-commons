package com.benefitj.frameworks;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.frameworks.cache.CaffeineCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheTest {

  @Test
  public void test_cache() {
    LoadingCache<String, String> cache = CaffeineCache.<String, String>newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .removalListener((key, value, cause) -> {
          log.info("removalListener, key: {}, value: {}, wasEvicted: {}, {}"
              , key, value, cause.wasEvicted(), DateFmtter.fmtNowS());
        })
        .build(new CacheLoader<String, String>() {
          @Override
          public @Nullable String load(String o) throws Exception {
            return null;
          }
        });

    for (int i = 0; i < 100; i++) {
      cache.put("" + i, "" + (i * 3));
    }

    final CountDownLatch latch = new CountDownLatch(1);
    EventLoop.asyncIOFixedRate(() -> {
      for (int i = 0; i < 10; i++) {
        EventLoop.sleepSecond(1);
        String key = "" + i;
        String value = cache.getIfPresent(key);
        log.info("{}, {}, {}", key, value, DateFmtter.fmtNowS());
      }
      latch.countDown();
    }, 1, 1, TimeUnit.SECONDS);

    CatchUtils.ignore(() -> latch.await());
  }

}
