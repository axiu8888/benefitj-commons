package com.benefitj.core;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class IOUtilsTest extends TestCase {

  @Test
  public void testReadFile() {
    File file = ClasspathUtils.getFile("abb_application-dev.properties");
    IOUtils.readFileLines(file).forEach(System.err::println);
  }

}