package com.benefitj.core.file;

import com.benefitj.core.BaseTest;
import org.junit.Test;

import java.io.File;

public class CompressUtilsTest extends BaseTest {

  @Override
  public void setUp() {
    System.out.println("------------- start ----------------");
  }

  /**
   * 压缩
   */
  @Test
  public void testGzip() {
//    File src = new File("D:\\develop\\CHE\\01000514-2020_02_28-22_26_17带血氧.CHE");
//    File dest = new File("D:\\develop\\CHE\\01000514-2020_02_28-22_26_17带血氧.gzip");
//    File src = new File("D:\\11000082-2021_05_17-10_54_13-001.CHE");
//    File dest = new File("D:\\11000082-2021_05_17-10_54_13-001.gzip");
    File src = new File("D:\\develop\\Virtual Machines\\CentOS_8_x64");
    CompressUtils.zip(src);
  }

  @Test
  public void testUnzip() {
    File zip = new File("D:\\develop\\Virtual Machines\\CentOS 8 64 位.zip");
    CompressUtils.unzip(zip, new File("D:\\develop\\Virtual Machines\\CentOS-8x64位"));
  }

  /**
   * 解码
   */
  @Test
  public void testUngzip() {
//    File src = new File("D:\\develop\\CHE\\01000514-2020_02_28-22_26_17带血氧.gzip");
//    File dest = new File("D:\\develop\\CHE\\01000514-2020_02_28-22_26_17带血氧_byGzip.CHE");
    File src = new File("D:\\11000082-2021_05_17-10_54_13-001.gzip");
    File dest = new File("D:\\11000082-2021_05_17-10_54_13-001_byGzip.CHE");
    CompressUtils.ungzip(src, dest);
  }


  @Override
  public void tearDown() {
    System.out.println("------------- over ----------------");
  }

}
