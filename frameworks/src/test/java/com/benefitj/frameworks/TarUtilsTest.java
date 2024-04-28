package com.benefitj.frameworks;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class TarUtilsTest extends BaseTest {

  @Test
  public void zcvf() {
    File file = new File("D:/tmp/che/01001049-2023_08_11-09_57_56.CHE");
    //TarUtils.tar(file);
    TarUtils.zcvf(file);
  }

  @Test
  public void zxvf() {
    File file = new File("D:/tmp/che/01001049-2023_08_11-09_57_56.tar.gz");
    TarUtils.zxvf(file, new File(file.getParentFile(), "CHE-untar"));
  }

}