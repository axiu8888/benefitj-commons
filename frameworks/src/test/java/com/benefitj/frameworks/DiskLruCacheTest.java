package com.benefitj.frameworks;

import com.benefitj.frameworks.cache.DiskLruCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class DiskLruCacheTest extends BaseTest {

  @Test
  public void test() {
    File dir = new File("D:/tmp/lru");
    DiskLruCache cache = DiskLruCache.open(dir, 1, 10, 20);
  }

}
