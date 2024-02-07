package com.benefitj.core.file;

import com.benefitj.core.BaseTest;
import com.benefitj.core.ClasspathUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

public class CompressUtilsTest extends BaseTest {

  /**
   * 压缩
   */
  @Test
  public void testZip() {
    File src = new File(ClasspathUtils.getDefaultClasspathDir());
    CompressUtils.zip(src);
  }

  @Test
  public void testUnzip() {
    File dir = new File(ClasspathUtils.getDefaultClasspathDir());
    File zip = new File(dir.getAbsolutePath() + ".zip");
    CompressUtils.unzip(zip, new File(dir.getParentFile(), "unzip"));
  }


  /**
   * 压缩
   */
  @Test
  public void testGzip() {
  }

  /**
   * 解码
   */
  @Test
  public void testUngzip() {
  }

}
