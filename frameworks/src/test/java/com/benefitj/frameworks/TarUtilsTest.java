package com.benefitj.frameworks;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class TarUtilsTest extends BaseTest {

  @Test
  public void tarGzip() {
    File file = new File("D:/tmp/che/01001049-2023_08_11-09_57_56.CHE");
    //TarUtils.tar(file);
    TarUtils.tarGzip(file);
  }

  @Test
  public void untarGzip() {
    File file = new File("D:/tmp/che/01001049-2023_08_11-09_57_56.tar.gz");
    TarUtils.untarGzip(file, new File(file.getParentFile(), "CHE-untar"));
  }

}