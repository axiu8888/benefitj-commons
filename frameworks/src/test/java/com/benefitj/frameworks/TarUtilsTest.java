package com.benefitj.frameworks;

import org.junit.Test;

import java.io.File;

public class TarUtilsTest extends BaseTest {
  public static void main(String[] args) {
    new TarUtilsTest().tarGzip();
  }

  @Override
  public void setUp() {

  }

  @Override
  public void tearDown() {

  }

  @Test
  public void tarGzip() {
//    File file = new File("D:\\code\\samples\\arduino-esp32");
    File file = new File("D:\\我的CHE\\11000142-2021_12_15-23_37_08-019.CHE");
    //TarUtils.tar(file);
    TarUtils.tarGzip(file);
  }

  @Test
  public void untarGzip() {
    File file = new File("D:\\code\\samples\\arduino-esp32.tar.gz");
    TarUtils.untarGzip(file, new File(file.getParentFile(), "arduino-esp32-untar"));
  }

}