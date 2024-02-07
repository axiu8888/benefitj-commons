package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.*;
import com.benefitj.jpuppeteer.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
public class ChromiumTest {

  @BeforeEach
  public void setUp() throws Exception {
  }

  @Test
  public void testChromiumArgs() throws IOException {
    JSONObject json = new JSONObject(new LinkedHashMap());
    List<String> lines = new ArrayList<>();
    IOUtils.readLines(IOUtils.wrapReader(Files.newInputStream(Paths.get("D:/tmp/chromium_startup_args_tmp.txt")), StandardCharsets.UTF_8), (line, lineNumber) -> lines.add(line));
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
    log.info("user.dir ==> {}", SystemProperty.getProperty("user.dir"));
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
            "--auto-open-devtools-for-tabs", // 打开开发者工具
            "about:blank",
            ""
        )
        .setRemoteDebuggingPort(NetworkUtils.availablePort())
    );
    Browser browser = chromium.launch();
    try {
      Browser.Version version = browser.getVersion();
      log.info("version: {}", JSON.toJSONString(version));

      Target target = chromium.getTarget();
      target.setDiscoverTargets(true, null);
      target.setAutoAttach(true, false, true, null);
      JSONObject targets = target.getTargets(null);
      List<Target.TargetInfo> targetInfos = targets.getList("targetInfos", Target.TargetInfo.class);
      log.info("targets: {}", JSON.toJSONString(targetInfos));

      String url = "http://research.sensecho.com/monitorReports/physical?reportZid=8146bbac1b284d31a9e683d09f75138e&loginName=haoyanli&version=undefined&extend=undefined";

      if (!targetInfos.isEmpty()) {
        // 需要创建新页面
        Target.TargetInfo targetInfo = targetInfos.get(0);

        JSONObject sessionIdResult = target.attachToTarget(targetInfo.getTargetId(), true);
        String sessionId = sessionIdResult.getString("sessionId");
        Page page = chromium.getPage();
        chromium.setLocalSessionId(sessionId);
        page.setLifecycleEventsEnabled(true);
        page.enable();
        chromium.getRuntime().enable();
        chromium.getNetwork().enable(null, null, null);
//        chromium.getNetwork().enable(50L * (1024 << 10), 20L * (1024 << 10), 20L * (1024 << 10));
//        chromium.getEmulation().canEmulate(true);
        chromium.getEmulation().setScriptExecutionDisabled(false);

        CountDownLatch latch = new CountDownLatch(1);
        chromium.once(new MessageListener.MatchMessageListener() {
          @Override
          public void onHandle(String method, JSONObject msg) {
            //EventLoop.await(500);
            JSONObject pdf = page.printToPDF(false,
                true,
                true,
                1.0,
                PaperFormats.a4.width,
                PaperFormats.a4.height,
                0,
                0,
                0,
                0,
                "",
                "",
                "",
                false,
                Page.TransferMode.ReturnAsBase64,
                true
            );
            log.info("pdf ==>: \n{}", pdf);
            latch.countDown();
          }

          @Override
          public boolean match(String method, JSONObject msg) {
            return method.equalsIgnoreCase("Page.frameStoppedLoading");
          }
        });

        chromium.exec(() -> {
          JSONObject navigate = page.navigate(url, null, Page.TransitionType.link, targetInfo.getTargetId(), Page.ReferrerPolicy.noReferrer);
          log.info("navigate ==>: {}", navigate);
        });
        CatchUtils.ignore(() -> latch.await(3, TimeUnit.SECONDS));

//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        JSONObject targetId = target.createTarget(targetInfo.getTargetId(), screenSize.width, screenSize.height
//            , targetInfo.getBrowserContextId(), true, true, true, true);
//        log.info("targetId: {}", targetId);
      }
      log.error("start await...");
      EventLoop.await(20, TimeUnit.SECONDS);
      log.error("end await...");
    } finally {
      log.info("关闭...");
      // 关闭
      browser.close();
    }
  }

  @AfterEach
  public void tearDown() throws Exception {
  }
}