package com.benefitj.core.file;

import com.benefitj.core.*;
import org.junit.Test;

public class RaFileTest extends BaseTest {

  @Test
  public void testRaFile() {
    try (final RaFile raFile = new RaFile("D:\\临时文件\\abc.log", "rw");) {
      raFile.seekLast();
      for (int i = 0; i < 5_00_000; i++) {
        raFile.write(DateFmtter.fmtNowS() + " " + IdUtils.uuid() + "\n");
      }
//      // 重置最大长度
      raFile.resizeEnd(10 * DUtils.MB);
//      raFile.resizeStart(50 * DUtils.KB);
    }
  }

}
