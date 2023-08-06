package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.NetworkUtils;
import com.benefitj.core.cmd.CmdCall;
import com.benefitj.core.cmd.CmdExecutor;
import com.benefitj.jpuppeteer.ChromiumLauncher;
import com.benefitj.jpuppeteer.LauncherOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class ChromiumLauncherTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testChromiumArgs() throws IOException {
    JSONObject json = new JSONObject(new LinkedHashMap());
    List<String> lines = new ArrayList<>();
    IOUtils.readLines(IOUtils.wrapReader(Files.newInputStream(Paths.get("D:/tmp/chromium_startup_args_tmp.txt")), StandardCharsets.UTF_8), lines::add);
    List<String> nweLines = lines.stream()
        .map(line -> line.endsWith("⊗") ? line.substring(line.length() - "⊗".length()) : line)
        .map(line -> line.endsWith(" ↪") ? line.substring(0, line.length() - " ↪".length()) : line)
        .map(line -> line.split("⊗"))
        .map(twoLine -> String.join(" ==>: ", twoLine[0].trim(), twoLine.length >= 2 ? twoLine[1].trim() : ""))
        .peek(line -> {
          String[] split = line.split(" ==>: ");
          json.put(split[0], split[1]);
        })
        .collect(Collectors.toList());
    String startupArgs = String.join("\n", nweLines);
    System.err.println(startupArgs);
    IOUtils.write(IOUtils.newFOS(IOUtils.createFile(new File("D:/tmp/chromium_startup_args.txt"))), startupArgs);
    IOUtils.write(IOUtils.newFOS(IOUtils.createFile(new File("D:/tmp/chromium_startup_args.json"))), json.toJSONString(JSONWriter.Feature.PrettyFormat));
  }

  @Test
  public void test() {
    String dir = "D:/tmp/.local-browser/win64-1132420";
    String cmd = dir + "/chrome-win/chrome.exe"
        + " --user-data-dir=" + dir + "/userDataDir"
        + " about:blank"
        + " --start-maximized"
        + " --auto-open-devtools-for-tabs"
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
    CmdExecutor executor = CmdExecutor.get();
    executor.setTimeout(10_000);
    CmdCall call = executor.call(cmd, 5_000);
    Process process = call.getProcess();
    log.info("call: {}, \ncmd: {}, \nmsg: {}, \nerror: {}"
        , call.getId()
        , call.getCmd()
        , call.getMessage()
        , call.getError()
    );
    log.info("process: {}", process.isAlive());

  }

  @Test
  public void testLauncher() {
    String dir = "D:/tmp/.local-browser/win64-1132420";
    ChromiumLauncher launcher = new ChromiumLauncher();
    launcher.setOptions(new LauncherOptions()
        .setExecutablePath(new File(dir + "/chrome-win/chrome.exe"))
        .setUserDataDir(new File(dir))
        .useDefaultArgs()
        .add(
            "--start-maximized", // 最大化
            //"--auto-open-devtools-for-tabs", // 打开开发者工具
            "about:blank",
            ""
        )
        .setRemoteDebuggingPort(NetworkUtils.availablePort())
    );
    Browser browser = launcher.launch();
    try {
      Browser.Version version = browser.getVersion();
      log.info("version: {}", JSON.toJSONString(version));

      Target target = launcher.newTarget();
      //target.createTarget("");
      //target.setDiscoverTargets(true, new Target.TargetFilter());
      JSONObject targets = target.getTargets(new Target.TargetFilter());
      List<Target.TargetInfo> targetInfos = targets.getList("targetInfos", Target.TargetInfo.class);
      log.info("targets: {}", JSON.toJSONString(targetInfos));

      if (targetInfos.isEmpty()) {
        // 需要创建新页面
      }
      Target.TargetInfo targetInfo = targetInfos.get(0);
      JSONObject targetId = target.createTarget(null, null, null, targetInfo.getBrowserContextId(), true, false, false, true);
      log.info("targetId: {}", targetId);
      EventLoop.sleepSecond(2);
    } finally {
      log.info("关闭...");
      // 关闭
      browser.close();
    }
  }

  @After
  public void tearDown() throws Exception {
  }
}