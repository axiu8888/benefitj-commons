package com.benefitj.core.file;

import com.benefitj.core.BaseTest;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.IdUtils;
import com.benefitj.core.Utils;
import org.junit.jupiter.api.Test;

public class RaFileTest extends BaseTest {

  @Test
  public void testRaFile() {
    try (final RaFile raFile = new RaFile("D:\\临时文件\\abc.log", "rw");) {
      //raFile.seekLast();
      raFile.seekFirst();
      int size = 5_0_000;
      for (int i = 0; i < size; i++) {
        raFile.write(DateFmtter.fmtNowS() + " " + IdUtils.uuid() + "\n");
      }
      // 重置最大长度
//      raFile.resizeEnd(2 * DUtils.MB);
      raFile.resizeStart(1024 * Utils.KB);
//      System.err.println(String.join("\n", raFile.readLines(size - 10, 10)));
    }
  }

}
