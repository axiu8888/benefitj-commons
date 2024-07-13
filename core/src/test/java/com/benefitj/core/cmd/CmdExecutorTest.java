package com.benefitj.core.cmd;

import com.benefitj.core.*;
import com.benefitj.core.file.PathWatcher;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * 测试CMD命令调用
 */
public class CmdExecutorTest extends BaseTest {

  @Test
  void testJavaVersion() {
    CmdCall call = CmdExecutor.get().call("java --version");
    System.err.println(call.toPrintInfo("java version", null));
    log.info("{}", call.toPrintInfo("java version", null));
  }

  @Test
  void testProperties() {
    SystemProperty.getSystemProperties().forEach((key, value) -> System.err.println(key + " ==>: " + value));
  }

  @Test
  void testMonoVersion() {
    CmdCall call = CmdExecutor.get().call("mono --version");
    System.err.println(call.toPrintInfo("mono version", null));
  }

  @Test
  void testMmhgReport() {
    File dir = new File("D:/tmp/jdk-app/hingmed");
    CmdCall call = CmdExecutor.get().call("cmd /c commandPrintPdf.exe report.xml report.pdf 127", null, dir);
    System.err.println(call.toPrintInfo("mmhg report", null));
  }

  @Test
  void testGitPull() {
    //new GitPull(new File("D:/code/github"), 5).pull();
    //new GitPull(new File("D:/code/github/java"), 5).pull();

    CountDownLatch latch = new CountDownLatch(1);
    //pull("D:/code/github/frontend", latch);
    pull("D:/code/github/java/vertx", latch);
//    pull("D:/code/github/golang", latch);
    CatchUtils.ignore(latch);
  }

  private void pull(String dir, CountDownLatch latch) {
    EventLoop.asyncIO(() -> {
      log.info("--------------->: \n");
      List<CmdCall> calls = new GitPull(new File(dir), 5).pull();
      String cmds = calls.stream()
          .map(c -> "cd " + c.getCtxDir().getAbsolutePath().replace("\\", "/") + " && git pull")
          .collect(Collectors.joining("\n"));
      log.info(" ==>: \n{}", cmds);
      log.info(":<---------------\n");
      latch.countDown();
    });
  }

  @Test
  void testKeystoreFile() {
    String keyStorePath = "D:/tmp/test.jks";

    String strPublicKey = KeystoreUtils.getStrPublicKey(keyStorePath, "axiu8888", "123456");
    System.out.println("公钥：" + strPublicKey);

    String strPrivateKey = KeystoreUtils.getStrPrivateKey(keyStorePath, "axiu8888", "123456", "123456");
    System.out.println("私钥：" + strPrivateKey);
  }

  @Test
  void testGenerateKeystore() throws Exception {
    String name = "axiu8888";
    String cmd = "keytool -genkeypair "
        + " -alias " + name
        + " -keypass 123456"
        + " -keyalg RSA"
        + " -keysize 2048"
        + " -validity 365"
        + " -storepass 123456"
        + " -keystore " + name + ".jks";

    System.err.println(cmd);

    String dir = "D:/tmp/https";

    PathWatcher pathWatcher = new PathWatcher(Paths.get(dir))
        .setWatchEventListener((watcher, key, path, filename, kind) -> {
          log.info("文件：" + (filename + " " + PathWatcher.ofDesc(kind)) + ", 发生事件：" + kind.name() + ", " + DateFmtter.fmtNowS());
          if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
//            if (filename.getFileName().startsWith("error.txt")) {
//              File file = filename.getFileName().toFile();
//              log.info("{}, {}", filename.getFileName(), IOUtils.readFileLines(filename.getFileName().toFile()));
//            }
            log.info("{}, {}", filename, IOUtils.readLines(new File(path.toFile(), filename)));
          }
        });
    EventLoop.asyncIO(pathWatcher::start);
    ProcessBuilder builder = new ProcessBuilder()
        .command(cmdarray(cmd))
        .directory(new File(dir))
        .redirectError(ProcessBuilder.Redirect.to(IOUtils.createFile(dir + "/error.txt")))
        //.redirectInput(ProcessBuilder.Redirect.from(IOUtils.createFile(dir + "/in.txt")))
        .redirectOutput(ProcessBuilder.Redirect.to(IOUtils.createFile(dir + "/out.txt")));
    Process process = builder.start();
    process.waitFor();
  }


  @Test
  void testGenerateKeystore2() throws Exception {
    String name = "axiu8888";
    String cmd = "keytool -genkeypair "
        + " -alias " + name
        + " -keypass 123456"
        + " -keyalg RSA"
        + " -keysize 2048"
        + " -validity 365"
        + " -storepass 123456"
        + " -keystore " + name + ".jks";

    System.err.println(cmd);

    String envdir = "D:/home/https/";
    File in = IOUtils.createFile(envdir, "out.txt");
    File out = IOUtils.createFile(envdir, "out.txt");
    File error = IOUtils.createFile(envdir, "error.txt");
    ProcessBuilder builder = new ProcessBuilder()
        .command(cmdarray(cmd))
        .directory(new File(envdir))
//        .redirectInput(ProcessBuilder.Redirect.from(in))
//        .redirectOutput(ProcessBuilder.Redirect.to(out))
//        .redirectError(ProcessBuilder.Redirect.to(error))
        ;

    String charsetName = SystemProperty.getFileEncoding();
    // 监听文件
    PathWatcher pw = new PathWatcher(Paths.get(envdir))
        .setWatchEventListener((watcher, key, path, filename, kind) -> {
          File src = new File(path.toFile(), filename);
          String type = src.getName();
          log.info("{}  ==>: {}, {}", type, kind.name(), IOUtils.readAsString(src, Charset.forName(charsetName)));
//          switch (filename.toFile().getName()) {
//            case "in.txt":
//              break;
//            case "out.txt":
//              break;
//            case "error.txt":
//              break;
//          }
        });
    EventLoop.asyncIO(pw::start);

    Process process = builder.start();

    try (final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      int i = 0;
      for (String str; (str = br.readLine()) != null; i++) {
        log.info("i ==>: {}, {}", i, str);
        switch (i) {
          case 0:
            process.getOutputStream().write(name.getBytes(charsetName));
            break;
          case 1:
          case 2:
            process.getOutputStream().write(name.getBytes(charsetName));
            break;
          case 3:
          case 4:
            process.getOutputStream().write("BeiJing".getBytes(charsetName));
            break;
          case 5:
            process.getOutputStream().write("86".getBytes(charsetName));
            break;
          case 6:
            process.getOutputStream().write("Y".getBytes(charsetName));
            break;
          default:
            break;
        }
        process.getOutputStream().flush();
      }
    }

    //process.waitFor();

    log.info("系统编码格式 ==>: {}", SystemProperty.getFileEncoding());
//
//
//    int index = 0;
//    while (process.isAlive() && index < 7) {
//      String message = IOUtils.readFully(process.getErrorStream(), false).toString(charsetName);
//      logger.info("error  ==>:  {}", message);
//      message = IOUtils.readFully(process.getInputStream(), false).toString(charsetName);
//      logger.info("input  ==>:  {}", message);
//
//      int i = index;
//      CatchUtils.ignore(() -> {
//        logger.info("i ==>: {}", i);
//        switch (i) {
//          case 0:
//            process.getOutputStream().write(name.getBytes(charsetName));
//            break;
//          case 1:
//          case 2:
//            process.getOutputStream().write(name.getBytes(charsetName));
//            break;
//          case 3:
//          case 4:
//            process.getOutputStream().write("BeiJing".getBytes(charsetName));
//            break;
//          case 5:
//            process.getOutputStream().write("86".getBytes(charsetName));
//            break;
//          case 6:
//            process.getOutputStream().write("Y".getBytes(charsetName));
//            break;
//          default:
//            break;
//        }
//        process.getOutputStream().flush();
//      });
//      index++;
//    }
//    process.waitFor(1, TimeUnit.SECONDS);
//    process.getOutputStream().write("yes".getBytes("GBK"));
//    process.getOutputStream().flush();
//
//    EventLoop.sleepSecond(1);
//    process.destroyForcibly();
//
//    System.exit(0);

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