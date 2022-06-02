package com.benefitj.core;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class IOUtilsTest extends BaseTest {

  @Test
  public void testReadFile() {
    File file = ClasspathUtils.getFile("abb_application-dev.properties");
    IOUtils.readFileLines(file).forEach(System.err::println);
  }

  @Test
  public void testProgress() {
    File dir = new File("D:\\develop\\tools");
    File[] files = dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".zip"));
    final AtomicInteger index = new AtomicInteger(0);
    IOUtils.process(files
        , (source, buf, len) -> {
          // 处理读取的数据
        }
        , (totalLength, totalProgress, source, progress) -> {
          if (index.incrementAndGet() % 20 == 0 || totalLength == totalProgress) {
            double currentProgress = totalProgress * 100.0 / totalLength;
            logger.info("{}, {}, {} ==>: {}%"
                , totalLength
                , totalProgress
                , source.getName()
                , DUtils.fmt(currentProgress, "0.00")
            );
          }
        });
  }

}