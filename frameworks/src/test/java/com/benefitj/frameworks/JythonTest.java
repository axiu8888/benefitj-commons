package com.benefitj.frameworks;

import com.benefitj.core.ClasspathUtils;
import org.junit.Test;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.File;

public class JythonTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void test() {
    //运行test.py脚本
    PythonInterpreter interp1 = new PythonInterpreter();
    File testPy = ClasspathUtils.getFile("test.py");

    interp1.execfile(testPy.getAbsolutePath());
    //运行python命令
    PythonInterpreter interp = new PythonInterpreter();
    System.out.println("Hello, brave new world");
    interp.exec("import sys");
    interp.exec("print sys");
    interp.set("a", new PyInteger(42));
    interp.exec("print a");
    interp.exec("x = 2+2");
    PyObject x = interp.get("x");
    System.out.println("x: " + x);
    System.out.println("Goodbye, cruel world");
  }

}
