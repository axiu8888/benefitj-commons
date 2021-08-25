package com.benefitj.frameworks;

import org.junit.Test;

import java.io.File;

public class TarUtilsTest extends BaseTest {

  @Override
  public void setUp() {

  }

  @Override
  public void tearDown() {

  }

  @Test
  public void tarGzip() {
    File file = new File("D:\\code\\samples\\arduino-esp32");
    TarUtils.tar(file);
    TarUtils.tarGzip(file);
  }

}