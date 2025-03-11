package com.benefitj.core.file;

import com.benefitj.core.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class JarFileCopyTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void copy() throws Throwable {
    File src = new File("D:\\code\\company\\backend\\znsx-v6\\support\\system\\build\\resources\\main\\download".replace("\\", "/"));
    String dest = "D:/develop/.tmp/cache/download";
    IOUtils.mkDirs(dest);
    JarFileCopy.copy(src.toURI().toURL(), "download", dest);

  }
}