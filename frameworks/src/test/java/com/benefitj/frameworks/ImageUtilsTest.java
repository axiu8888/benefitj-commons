package com.benefitj.frameworks;

import org.junit.Test;

import java.io.File;

public class ImageUtilsTest extends BaseTest {


  @Test
  public void test_resize() {
    File src = new File("D:/tmp/cache/icon.png");
    File dest = new File(src.getParentFile(), "favorite.png");
    ImageUtils.resize(src, dest, 256);
  }


  @Test
  public void test_imgToIco() {
    File src = new File("D:/tmp/cache/icon.png");
    File dest = new File(src.getParentFile(), "favorite.ico");
    ImageUtils.imgToIco(src, dest, 256);
  }

}
