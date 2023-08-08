package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.NetworkUtils;
import com.benefitj.jpuppeteer.BrowserFetcher;
import com.benefitj.jpuppeteer.Chromium;
import com.benefitj.jpuppeteer.LauncherOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
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
public class ChromiumTest {

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
  public void testDownloadIfNotExist() {
    BrowserFetcher fetcher = BrowserFetcher.get();
    fetcher.getRevisionInfo().setFolder(new File("D:/tmp/.local-browser"));
    BrowserFetcher.downloadIfNotExist(fetcher);
  }

  @Test
  public void testLauncher() {
    String dir = "D:/tmp/.local-browser/win64-1132420";
    Chromium chromium = new Chromium();
    chromium.setOptions(new LauncherOptions()
        .setExecutablePath(new File(dir + "/chrome-win/chrome.exe"))
        .setUserDataDir(new File(dir, "userDataDir"))
        .useDefaultArgs()
        .add(
            "--start-maximized", // 最大化
            //"--auto-open-devtools-for-tabs", // 打开开发者工具
            "about:blank",
            ""
        )
        .setRemoteDebuggingPort(NetworkUtils.availablePort())
    );
    Browser browser = chromium.launch();
    try {
      Browser.Version version = browser.getVersion();
      log.info("version: {}", JSON.toJSONString(version));

      Target target = chromium.newTarget();
      target.setDiscoverTargets(true, null);
      //target.attachedToTarget();
      //target.createTarget("");
      //target.setDiscoverTargets(true, new Target.TargetFilter());
      JSONObject targets = target.getTargets(null);
      List<Target.TargetInfo> targetInfos = targets.getList("targetInfos", Target.TargetInfo.class);
      log.info("targets: {}", JSON.toJSONString(targetInfos));

      if (!targetInfos.isEmpty()) {
        // 需要创建新页面
        Target.TargetInfo targetInfo = targetInfos.get(0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JSONObject targetId = target.createTarget(targetInfo.getTargetId(), screenSize.width, screenSize.height, targetInfo.getBrowserContextId(), false, false, false, true);
        log.info("targetId: {}", targetId);
      }
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