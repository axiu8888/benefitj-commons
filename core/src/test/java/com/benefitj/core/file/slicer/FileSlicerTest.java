package com.benefitj.core.file.slicer;

import com.benefitj.core.BaseTest;
import com.benefitj.core.Utils;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.IdUtils;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSlicerTest extends BaseTest {

  /**
   * 测试文件分割
   */
  @Test
  public void testWrite() {

    final AtomicInteger stopFlag = new AtomicInteger();

    FileSlicer slicer = new FileSlicer();
    // 缓存目录
    slicer.setCacheDir(new File("D:\\opt\\tmp\\"));
    // 文件最大的长度
    slicer.setMaxSize(5 * Utils.MB);
    // 文件工厂对象
    slicer.setFileFactory(dir -> {
      String filename = IdUtils.nextLowerLetterId(DateFmtter.fmtNow("yyyyMMdd_HHmmss") + "__", ".txt", 10);
      File file = FileFactory.createFile(dir, filename);
      logger.info("创建文件: {}", file.getAbsolutePath());
      return new SliceFileWriter(file);
    });
    // 文件监听
    slicer.setFileListener((writer, file) -> {
      logger.info("处理文件: {}, {}MB", file.getAbsolutePath(), Utils.ofMB(file.length(), 2));
      stopFlag.incrementAndGet();
    });

    for (;;) {
      slicer.write(IdUtils.nextId(null, "\n", 32));
      if (stopFlag.get() >= 10) {
        break;
      }
    }

  }

}