package com.benefitj.frameworks;

import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.TimeUtils;
import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;

import java.io.File;

class JythonTest extends BaseTest {

  @Test
  void test() {
    //运行test.py脚本
    PythonInterpreter interp1 = new PythonInterpreter();
    File testPy = ClasspathUtils.getFile("test.py");

    long startAt = TimeUtils.now();
    for (int i = 0; i < 1000; i++) {
      interp1.execfile(testPy.getAbsolutePath());
    }
    log.info("耗时: " + TimeUtils.diffNow(startAt));
//    //运行python命令
//    PythonInterpreter interp = new PythonInterpreter();
//    log.info("Hello, brave new world");
//    interp.exec("import sys");
//    interp.exec("print sys");
//    interp.set("a", new PyInteger(42));
//    interp.exec("print a");
//    interp.exec("x = 2+2");
//    PyObject x = interp.get("x");
//    log.info("x: " + x);
//    log.info("Goodbye, cruel world");

  }

}
