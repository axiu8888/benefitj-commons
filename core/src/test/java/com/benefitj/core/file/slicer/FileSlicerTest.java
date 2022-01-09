package com.benefitj.core.file.slicer;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.IdUtils;
import com.benefitj.core.DUtils;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSlicerTest extends TestCase {

  private final Logger log = LoggerFactory.getLogger(getClass());

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
    slicer.setMaxSize(5 * DUtils.MB);
    // 文件工厂对象
    slicer.setFileFactory(dir -> {
      String filename = IdUtils.nextLowerLetterId(DateFmtter.fmtNow("yyyyMMdd_HHmmss") + "__", ".txt", 10);
      File file = FileFactory.createFile(dir, filename);
      log.info("创建文件: {}", file.getAbsolutePath());
      return new SliceFileWriter(file);
    });
    // 文件监听
    slicer.setFileListener((writer, file) -> {
      log.info("处理文件: {}, {}MB", file.getAbsolutePath(), DUtils.ofMB(file.length(), 2));
      stopFlag.incrementAndGet();
    });

    for(;;) {
      slicer.write(IdUtils.nextId(null, "\n", 32));
      if (stopFlag.get() >= 10) {
        break;
      }
    }

  }

}