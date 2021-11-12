package com.benefitj.core;

import org.junit.Test;

import java.net.URL;
import java.util.List;


public class ClasspathUtilsTest extends BaseTest {

  @Test
  public void testClasspath() {
    String classpathDir = ClasspathUtils.getDefaultClasspathDir();
    logger.info("currentClasspathDir: {}", classpathDir);

    URL url = ClasspathUtils.getURL("abb_application-dev.properties", null);
    logger.info("{} , isJar: {}", url.getPath(), ClasspathUtils.isJar(url.getPath()));

    ClasspathUtils.copy("abb_application-dev.properties", classpathDir);
  }

  @Test
  public void testFindClasses() {
    List<String> classes = ClasspathUtils.findClasses("com.benefitj.core.cmd");
    System.err.println(String.join("\n", classes));
  }

}