package com.benefitj.core;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;


public class ClasspathUtilsTest extends BaseTest {

  @Test
  public void testClasspath() {
    String classpathDir = ClasspathUtils.getDefaultClasspathDir();
    log.info("currentClasspathDir: {}", classpathDir);

    URL url = ClasspathUtils.getURL("abb_application-dev.properties", null);
    log.info("{} , isJar: {}", url.getPath(), ClasspathUtils.isJar(url.getPath()));

    ClasspathUtils.copy("abb_application-dev.properties", classpathDir);
  }

  @Test
  public void testFindClasses() {
    List<String> classes = ClasspathUtils.findClasses("com.benefitj.core.cmd");
    System.err.println(String.join("\n", classes));
  }

}