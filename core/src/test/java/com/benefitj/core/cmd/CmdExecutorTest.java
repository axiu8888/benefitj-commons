package com.benefitj.core.cmd;

import com.benefitj.core.*;
import com.benefitj.core.file.PathWatcher;
import com.benefitj.core.functions.IRunnable;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 测试CMD命令调用
 */
public class CmdExecutorTest extends BaseTest {

  public static final Pattern WS_ENDPOINT_PATTERN = Pattern.compile("^DevTools listening on (ws://.*)$");

  @Test
  public void testChromium() {
    // D:\tmp\.local-browser\win64-1132420\chrome-win\chrome.exe--user-data-dir=D:\home\tmp\.local-browser\win64-1132420/userDataDir about:blank --start-maximized --auto-open-devtools-for-tabs --disable-background-timer-throttling --disable-breakpad --disable-browser-side-navigation --disable-client-side-phishing-detection --disable-default-apps --disable-dev-shm-usage --disable-features=site-per-process --disable-hang-monitor --disable-popup-blocking --disable-prompt-on-repost --disable-translate --metrics-recording-only --no-first-run --safebrowsing-disable-auto-update --enable-automation --password-store=basic --use-mock-keychain --remote-debugging-port=61370
    String dir = "D:/tmp/.local-browser/win64-1132420";
    String cmd = dir + "/chrome-win/chrome.exe";
    String envparams = ""
        + " --user-data-dir=" + dir + "/userDataDir about:blank"
        + " --start-maximized"
        //+ " --auto-open-devtools-for-tabs"
        + " --disable-background-timer-throttling"
        + " --disable-breakpad"
        + " --disable-browser-side-navigation"
        + " --disable-client-side-phishing-detection"
        + " --disable-default-apps"
        + " --disable-dev-shm-usage"
        + " --disable-features=site-per-process"
        + " --disable-hang-monitor"
        + " --disable-popup-blocking"
        + " --disable-prompt-on-repost"
        + " --disable-translate"
        + " --metrics-recording-only"
        + " --no-first-run"
        + " --safebrowsing-disable-auto-update"
        + " --enable-automation"
        + " --password-store=basic"
        + " --use-mock-keychain"
        + " --remote-debugging-port=61370";

    List<String> envp = Stream.of(envparams.split(" --")).filter(StringUtils::isNotBlank).map(str -> "--" + str).collect(Collectors.toList());
    CmdCall call = CmdExecutor.get().call(cmd, envp, null, -1, new Callback() {
      @Override
      public void onMessage(CmdCall call, List<String> lines, String line, boolean error) {
        log.info("onMessage: {}, \nlines: {}, \nline: {}, \nerror: {}", call.getId(), String.join("; ", lines), line, error);
        Matcher matcher = WS_ENDPOINT_PATTERN.matcher(line);
        if (matcher.find()) {
          log.info("we endpoint: {}", matcher.group(1));
        }
      }
    });
    Process process = call.getProcess();
    System.err.println(call.toPrintInfo("HtmlToPdf", null));
    log.info("process: {}", process.isAlive());

    EventLoop.sleepSecond(10);

    // 关闭
    process.destroyForcibly();

  }

  @Test
  public void testJavaVersion() {
    CmdCall call = CmdExecutor.get().call("java --version");
    System.err.println(call.toPrintInfo("java version", null));
  }

  @Test
  public void testProperties() {
    SystemProperty.getSystemProperties().forEach((key, value) -> System.err.println(key + " ==>: " + value));
  }

  @Test
  public void testMonoVersion() {
    CmdCall call = CmdExecutor.get().call("mono --version");
    System.err.println(call.toPrintInfo("mono version", null));
  }

  @Test
  public void testMmhgReport() {
    File dir = new File("D:\\tmp\\jdk-app\\hingmed");
    CmdCall call = CmdExecutor.get().call("cmd /c commandPrintPdf.exe report.xml report.pdf 127", null, dir);
    System.err.println(call.toPrintInfo("mmhg report", null));
  }

  @Test
  public void testGitPull() {
//    new GitPull(new File("D:/code/github"), 5).pull();
//    new GitPull(new File("D:/code/github/java"), 5).pull();

    CountDownLatch latch = new CountDownLatch(2);
    //pull("D:\\code\\github\\frontend", latch);
    pull("D:\\code\\github\\java\\vertx", latch);
    CatchUtils.ignore((IRunnable) latch::await);
  }

  private void pull(String dir, CountDownLatch latch) {
    EventLoop.io().execute(() -> {
      new GitPull(new File(dir), 5).pull();
      latch.countDown();
    });
  }

  @Test
  public void testKeystoreFile() {
    String keyStorePath = "D:/tmp/test.jks";

    String strPublicKey = KeystoreUtils.getStrPublicKey(keyStorePath, "axiu8888", "123456");
    System.out.println("公钥：" + strPublicKey);

    String strPrivateKey = KeystoreUtils.getStrPrivateKey(keyStorePath, "axiu8888", "123456", "123456");
    System.out.println("私钥：" + strPrivateKey);
  }

  @Test
  public void testGenerateKeystore() throws Exception {
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

    String dir = "D:/home/https";
    ProcessBuilder builder = new ProcessBuilder()
        .command(cmdarray(cmd))
        .directory(new File(dir))
        .redirectError(ProcessBuilder.Redirect.to(IOUtils.createFile(dir + "/error.txt")))
        .redirectInput(ProcessBuilder.Redirect.from(IOUtils.createFile(dir + "/in.txt")))
        .redirectOutput(ProcessBuilder.Redirect.to(IOUtils.createFile(dir + "/out.txt")));
    Process process = builder.start();
    process.waitFor();
  }


  @Test
  public void testGenerateKeystore2() throws Exception {
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
    PathWatcher watcher = new PathWatcher(Paths.get(envdir))
        .setWatchEventListener((key, context, kind) -> {
          File src = context.toFile();
          String type = src.getName();
          log.info("{}  ==>: {}, {}", type, kind.name(), IOUtils.readFileAsString(src, Charset.forName(charsetName)));
//          switch (context.toFile().getName()) {
//            case "in.txt":
//              break;
//            case "out.txt":
//              break;
//            case "error.txt":
//              break;
//          }
        });
    EventLoop.io().execute(watcher::start);

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