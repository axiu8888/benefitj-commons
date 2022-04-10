package com.benefitj.core.cmd;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

/**
 * 测试CMD命令调用
 */
public class CmdExecutorTest extends TestCase {

  @Test
  public void testJavaVersion() {
    CmdExecutor executor = CmdExecutorHolder.getInstance();
    CmdCall call = executor.call("java --version");
    call.print("java version");
  }

  @Test
  public void testGitPull() {
//    new GitPull(new File("D:\\code\\github"), 5).pull();
    new GitPull(new File("D:\\code\\github\\java"), 5).pull();
  }

}