package com.benefitj.core.cmd;

import com.benefitj.core.BaseTest;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * 测试CMD命令调用
 */
public class CmdExecutorTest extends BaseTest {

  @Test
  public void testJavaVersion() {
    CmdCall call = CmdExecutor.get().call("java --version");
    call.print("java version");
  }

  @Test
  public void testGitPull() {
//    new GitPull(new File("D:\\code\\github"), 5).pull();
    new GitPull(new File("D:/code/github/go"), 5).pull();
  }

  @Test
  public void testKeystoreFile() {
    String keyStorePath = "D:/home/test.jks";

    String strPublicKey = KeystoreUtils.getStrPublicKey(keyStorePath, "test", "123456");
    System.out.println("公钥：" + strPublicKey);

    String strPrivateKey = KeystoreUtils.getStrPrivateKey(keyStorePath, "test", "123456", "123456");
    System.out.println("私钥：" + strPrivateKey);
  }

  @Test
  public void testGenerateKeystore() throws Exception {
    String cmd = "keytool -genkeypair "
        + " -alias test"
        + " -keypass 123456"
        + " -keyalg RSA"
        + " -keysize 2048"
        + " -validity 365"
        + " -storepass 123456"
        + " -keystore test.jks";

    System.err.println(cmd);

    ProcessBuilder builder = new ProcessBuilder()
        .command(cmdarray(cmd))
        .directory(new File("D:/home/"))
        .redirectError(ProcessBuilder.Redirect.to(IOUtils.createFile("D:/home/error.txt")))
        .redirectInput(ProcessBuilder.Redirect.from(IOUtils.createFile("D:/home/in.txt")))
        .redirectOutput(ProcessBuilder.Redirect.to(IOUtils.createFile("D:/home/out.txt")));
    Process process = builder.start();
    process.waitFor();
  }


  @Test
  public void testGenerateKeystore2() throws Exception {
    String cmd = "keytool -genkeypair "
        + " -alias test"
        + " -keypass 123456"
        + " -keyalg RSA"
        + " -keysize 2048"
        + " -validity 365"
        + " -storepass 123456"
        + " -keystore test.jks";

    System.err.println(cmd);

    String envdir = "D:/home/";
    ProcessBuilder builder = new ProcessBuilder()
        .command(cmdarray(cmd))
        .directory(new File(envdir))
        //.redirectError(ProcessBuilder.Redirect.to(IOUtils.createFile(envdir, "error.txt")))
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .redirectOutput(ProcessBuilder.Redirect.to(IOUtils.createFile(envdir, "out.txt")))
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        ;

    Process process = builder.start();

    int index = 0;
    while (process.isAlive() && index < 7) {
      String message = IOUtils.readFully(process.getErrorStream(), false).toString(StandardCharsets.UTF_8);
      logger.info("error  ==>:  {}", message);
      message = IOUtils.readFully(process.getInputStream(), false).toString(StandardCharsets.UTF_8);
      logger.info("input  ==>:  {}", message);

      int i = index;
      CatchUtils.ignore(() -> {
        logger.info("i ==>: {}", i);
        switch (i) {
          case 0:
            process.getOutputStream().write("hsrg".getBytes(StandardCharsets.UTF_8));
            break;
          case 1:
          case 2:
            process.getOutputStream().write("Sensecho".getBytes(StandardCharsets.UTF_8));
            break;
          case 3:
          case 4:
            process.getOutputStream().write("BeiJing".getBytes(StandardCharsets.UTF_8));
            break;
          case 5:
            process.getOutputStream().write("86".getBytes(StandardCharsets.UTF_8));
            break;
          case 6:
            process.getOutputStream().write("Y".getBytes(StandardCharsets.UTF_8));
            break;
          default:
            break;
        }
        process.getOutputStream().flush();
      });
      index++;
    }
    process.waitFor(1, TimeUnit.SECONDS);
    process.getOutputStream().write("yes".getBytes("GBK"));
    process.getOutputStream().flush();

    EventLoop.sleepSecond(1);
    process.destroyForcibly();

    System.exit(0);

  }

  public static String[] cmdarray(String cmd) {
    StringTokenizer st = new StringTokenizer(cmd);
    String[] cmdarray = new String[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++)
      cmdarray[i] = st.nextToken();
    return cmdarray;
  }


//  @SuperBuilder
//  @NoArgsConstructor
//  @AllArgsConstructor
//  @Data
//  static class KeystoreOptions {
//    /**
//     * 别名
//     */
//    private String alias;
//    /**
//     * 密码
//     */
//    private String keypass;
//    /**
//     * 算法: SHA1、SHA256、RSA
//     */
//    private String keyalg = "RSA";
//    /**
//     * 长度
//     */
//    private int keysize = 2048;
//    /**
//     * 有效期，默认365
//     */
//    private long validity = 365;
//    /**
//     * keystore 文件名
//     */
//    private String keystore;
//    /**
//     * keystore 密码
//     */
//    private String storepass;
//
//
//    public String obtainCmd() {
//      if (StringUtils.isBlank(getAlias())) {
//        throw new IllegalStateException("请输入别名");
//      }
//      if (StringUtils.isBlank(getKeypass())) {
//        throw new IllegalStateException("请输入密码");
//      }
//      if (StringUtils.isBlank(getKeyalg())) {
//        throw new IllegalStateException("请输入算法类型");
//      }
//      if (StringUtils.isBlank(getKeystore())) {
//        throw new IllegalStateException("请输入keystore文件名");
//      }
//      if (StringUtils.isBlank(getStorepass())) {
//        throw new IllegalStateException("请输入keystore密码");
//      }
//
////      "keytool -genkeypair "
////          + " -alias dongbao-alias"
////          + " -keypass 123456"
////          + " -keyalg RSA"
////          + " -keysize 1024"
////          + " -validity 365"
////          + " -keystore test.jks"
////          + " -storepass 123456"
//
//
//      StringBuilder sb = new StringBuilder("keytool -genkeypair ");
//      sb.append(getStr(" -alias ", getAlias()));
//      sb.append(getStr(" -keypass ", getKeypass()));
//      sb.append(getStr(" -keyalg ", getKeyalg()));
//      sb.append(getStr(" -keysize ", getKeysize()));
//      sb.append(getStr(" -validity ", getValidity()));
//      sb.append(getStr(" -storepass ", getStorepass()));
//      sb.append(getStr(" -keystore ", getKeystore()));
////      sb.append(getStr("", ));
////      sb.append(getStr("", ));
//      return sb.toString();
//    }
//
//    static String getStr(String cmd, Object o) {
//      return o != null ? cmd + o : "";
//    }
//
//  }

}