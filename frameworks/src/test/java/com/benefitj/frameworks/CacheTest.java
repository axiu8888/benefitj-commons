package com.benefitj.frameworks;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.frameworks.cache.CaffeineCache;
import com.benefitj.frameworks.cache.IEncache;
import com.benefitj.frameworks.cache.ILoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class CacheTest {

  @Test
  void test_LoadingCache() {
    ILoadingCache<String, String> cache = ILoadingCache.wrap(CacheBuilder.<String, String>newBuilder()
            .initialCapacity(1000)
            .expireAfterWrite(Duration.ofMinutes(1))
            .expireAfterAccess(Duration.ofMinutes(1))
            .removalListener((RemovalListener<String, String>) notification -> {
              // 移除
              log.info("remove --> key: {}, value: {}, wasEvicted: {}, cause: {}"
                  , notification.getKey()
                  , notification.getValue()
                  , notification.wasEvicted()
                  , notification.getCause()
              );
            })
        , new ConcurrentHashMap<>()
        , (cacheLoader, key) -> cacheLoader.map().get(key));
    for (int i = 0; i < 100; i++) {
      cache.put("key" + i, "value" + i);
    }
    cache.asMap().forEach((k, v) -> log.info("{} --->: {}", k, v));


    cache.invalidate("key22");

  }

  @Test
  void test_LoadingCache2() {
    ILoadingCache<String, String> cache = ILoadingCache.newWriteCache(Duration.ofSeconds(5), (RemovalListener<String, String>) notification -> {
      // 移除
      log.info("remove --> key: {}, value: {}, wasEvicted: {}, cause: {}"
          , notification.getKey()
          , notification.getValue()
          , notification.wasEvicted()
          , notification.getCause()
      );
    });
    for (int i = 0; i < 10; i++) {
      cache.put("key" + i, "value" + i);
    }
    cache.asMap().forEach((k, v) -> log.info("{} --->: {}", k, v));
    cache.invalidate("key22");
    EventLoop.sleepSecond(6);
    cache.put("key0012", "ping...");
    EventLoop.sleepSecond(6);
    log.info("cleanUp1.1 ----------------------------------- cleanUp before");
    cache.put("key0012", "ping...");
    cache.cleanUp();
    log.info("cleanUp1.2 ----------------------------------- cleanUp after");
    cache.put("key0012", "ping...");
    cache.cleanUp();
    log.info("cleanUp1.3 ----------------------------------- cleanUp after");
    String key0012 = cache.getIfPresent("key0012");
    log.info("key0012: {}", key0012);

    log.info("put2 ----------------------------------- put2 before");
    for (int i = 0; i < 10; i++) {
      cache.put("key" + i, "value" + i);
    }
    log.info("put2 ----------------------------------- put2 after");
    EventLoop.sleepSecond(3);
    log.info("cleanUp2 ----------------------------------- cleanUp2 before");
    cache.cleanUp();
    log.info("cleanUp2 ----------------------------------- cleanUp2 after");

  }

  @Test
  void test_LoadingCache3() {
    ILoadingCache<String, String> cache = ILoadingCache.newAccessCache(Duration.ofSeconds(3), (RemovalListener<String, String>) notification -> {
      // 移除
      log.info("remove --> key: {}, value: {}, wasEvicted: {}, cause: {}"
          , notification.getKey()
          , notification.getValue()
          , notification.wasEvicted()
          , notification.getCause()
      );
    });

    log.info("put 1 ----------------------------------- put2 before");
    for (int i = 0; i < 10; i++) {
      cache.put("key" + i, "value" + i);
    }
    log.info("put 1 ----------------------------------- put2 after");

    EventLoop.sleepSecond(1);
    for (int i = 0; i < 10; i++) {
      String key = "key" + (i % 2);
      String value = cache.getIfPresent(key);
      log.info("{} -> {}", key, value);
      EventLoop.sleepSecond(1);
      cache.cleanUp();
    }

    log.info("size -->: {}", cache.size());
    EventLoop.sleepSecond(3);
  }

  @Test
  void test_LoadingCache4() {
    ILoadingCache<String, String> cache = ILoadingCache.newAccessCache(Duration.ofSeconds(3), (RemovalListener<String, String>) notification -> {
      // 移除
      log.info("remove --> key: {}, value: {}, wasEvicted: {}, cause: {}"
          , notification.getKey()
          , notification.getValue()
          , notification.wasEvicted()
          , notification.getCause()
      );
    });

    cache.startCleanup();//开启调度

    log.info("put 1 ----------------------------------- put2 before");
    for (int i = 0; i < 10; i++) {
      cache.put("key" + i, "value" + i);
    }
    log.info("put 1 ----------------------------------- put2 after");

    EventLoop.sleepSecond(1);
    for (int i = 0; i < 10; i++) {
      String key = "key" + (i % 2);
      String value = cache.getIfPresent(key);
      log.info("{} -> {}", key, value);
      EventLoop.sleepSecond(1);
    }

    log.info("size -->: {}", cache.size());
    EventLoop.sleepSecond(3);
  }

  @Test
  void test_IEncache() {
    System.setProperty(IEncache.Factory.CACHE_DIR, "./build/cache");
    IEncache.Factory factory = IEncache.Factory.get();
    IEncache<String, String> cache = factory.getIfAbsentCreate("cache", String.class, String.class);

    log.info("[1] start --------------------------------- start");
    for (org.ehcache.Cache.Entry<String, String> entry : cache) {
      log.info("{} --->: {}", entry.getKey(), entry.getValue());
    }
    log.info("[1] end --------------------------------- end");

    for (int i = 0; i < 10; i++) {
      cache.put("key" + i, "value" + i);
    }
    log.info("[2] start --------------------------------- start");
    for (org.ehcache.Cache.Entry<String, String> entry : cache) {
      log.info("{} --->: {}", entry.getKey(), entry.getValue());
    }
    log.info("[2] end --------------------------------- end");
  }

  @Test
  void test_write() {
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
  void test_access_write() {
    test_cache(CaffeineCache.<String, String>newBuilder()
        //.expireAfterWrite(Duration.ofSeconds(5))
        .expireAfterAccess(Duration.ofSeconds(5))
        .removalListener((key, value, cause) -> {
          log.info("removalListener, key: {}, value: {}, wasEvicted: {}, {}"
              , key, value, cause.wasEvicted(), DateFmtter.fmtNowS());
        })
        .build(new CacheLoader<String, String>() {
          @Override
          public @Nullable String load(String key) throws Exception {
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
        EventLoop.sleep(100);
      }
      latch.countDown();
    }, 1, 1, TimeUnit.SECONDS);

    CatchUtils.ignore(() -> latch.await());
  }

}
