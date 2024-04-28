package com.benefitj.frameworks;

import com.benefitj.core.IOUtils;
import com.benefitj.frameworks.cache.DiskLruCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
class DiskLruCacheTest extends BaseTest {

  @Test
  void test() {
    File dir = IOUtils.mkDirs("D:/tmp/cache/lru");
    DiskLruCache cache = DiskLruCache.open(dir, 1, 10, 20);
    DiskLruCache.Snapshot snapshot = cache.get("1");
    if (snapshot != null) {
      snapshot.edit()
          .set(0, "123")
          .commit();
    } else {
      cache.edit("1")
          .set(0, "12345")
          .commit();
    }
  }

}
