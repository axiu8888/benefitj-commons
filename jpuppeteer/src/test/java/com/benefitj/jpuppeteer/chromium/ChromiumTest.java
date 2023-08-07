package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.NetworkUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.jpuppeteer.Chromium;
import com.benefitj.jpuppeteer.LauncherOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class ChromiumTest {
  public static void main(String[] args) {
    new ChromiumTest().testProxyMethods();
  }

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
  public void testProxyMethods() {
    ReflectUtils.findMethods(ReflectUtils.class
        , m -> true
        , m -> {
          log.info("{}.{}({})"
              , m.getDeclaringClass().getSimpleName()
              , m.getName()
              , Stream.of(m.getParameters()).map(Parameter::getName).collect(Collectors.toList())
          );
        }
        , m -> false
    );
  }

  @Test
  public void testLauncher() {
    String dir = "D:/tmp/.local-browser/win64-1132420";
    Chromium launcher = new Chromium();
    launcher.setOptions(new LauncherOptions()
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
      JSONObject targetId = target.createTarget(targetInfo.getTargetId(), null, null, targetInfo.getBrowserContextId(), true, false, false, true);
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