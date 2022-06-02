package com.benefitj.core.property;

import com.benefitj.core.BaseTest;
import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.SystemProperty;
import org.junit.Test;

import java.io.File;

public class DefaultPropertiesConverterTest extends BaseTest {

  @Test
  public void testConvert() {
    String filename = "abb_application-dev.properties";
    File dest = new File(SystemProperty.getJavaIOTmpDir(), filename);
    try {
      ClasspathUtils.copy(filename, dest.getAbsolutePath());
      PropertiesConverter converter = new DefaultPropertiesConverter(dest);
//      converter.getAll().forEach((key, value)
//          -> System.err.println(key + " ==>: " + value));

      System.err.println("bv ==>: " + converter.getBoolean("com.hsrg.influxdb.gzip"));
      System.err.println("bv ==>: " + converter.getValue("com.hsrg.influxdb.gzip").getClass());

    } finally {
      IOUtils.deleteFile(dest);
    }
  }

}