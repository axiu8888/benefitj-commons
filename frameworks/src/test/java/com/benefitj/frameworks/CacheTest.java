package com.benefitj.frameworks;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.frameworks.cache.CaffeineCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheTest {

  @Test
  public void test_write() {
    test_cache(CaffeineCache.<String, String>newBuilder()
        .expireAfterWrite(Duration.ofSeconds(5))
        .removalListener((key, value, cause) -> {
          log.info("removalListener, key: {}, value: {}, wasEvicted: {}, {}"
              , key, value, cause.wasEvicted(), DateFmtter.fmtNowS());
        })
        .build(new CacheLoader<String, String>() {
          @Override
          public @Nullable String load(String o) throws Exception {
            return null;
          }
        }));
  }

  @Test
  public void test_access_write() {
    test_cache(CaffeineCache.<String, String>newBuilder()
        //.expireAfterWrite(Duration.ofSeconds(5))
        .expireAfterAccess(Duration.ofSeconds(5))
        .removalListener((key, value, cause) -> {
          log.info("removalListener, key: {}, value: {}, wasEvicted: {}, {}"
              , key, value, cause.wasEvicted(), DateFmtter.fmtNowS());
        })
        .build(new CacheLoader<String, String>() {
          @Override
          public @Nullable String load(String o) throws Exception {
            return null;
          }
        }));
  }

  static void test_cache(Cache<String, String> cache) {
    for (int i = 0; i < 100; i++) {
      cache.put(String.valueOf(i), String.valueOf(i * 3));
    }

    log.info("estimatedSize: {}", cache.estimatedSize());

    final CountDownLatch latch = new CountDownLatch(1);
    EventLoop.asyncIOFixedRate(() -> {
      for (int i = 0; i < 10_000; i++) {
        String key = String.valueOf(i % 10);
        String value = cache.getIfPresent(key);
        log.info("estimatedSize: {}, {}, {}, {}", cache.estimatedSize(), key, value, DateFmtter.fmtNowS());
        EventLoop.sleepMillis(100);
      }
      latch.countDown();
    }, 1, 1, TimeUnit.SECONDS);

    CatchUtils.ignore(() -> latch.await());
  }

}
