package com.benefitj.jpuppeteer;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class LauncherOptions {


  /**
   * 启动浏览器时，如果没有指定路径，那么会从以下路径搜索可执行的路径
   */
  public static final List<String> PROBABLE_CHROME_EXECUTABLE_PATH = Collections.unmodifiableList(Arrays.asList(
      "/usr/bin/chromium",
      "/usr/bin/chromium-browser",
      "/usr/bin/google-chrome-stable",
      "/usr/bin/google-chrome",
      "/Applications/Chromium.app/Contents/MacOS/Chromium",
      "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
      "/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary",
      "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe",
      "C:/Program Files/Google/Chrome/Application/chrome.exe"
  ));

  /**
   * 谷歌浏览器默认启动参数
   */
  public static final List<String> DEFAULT_ARGS = Collections.unmodifiableList(Arrays.asList(
      "--disable-background-networking",
      "--disable-background-timer-throttling",
      "--disable-breakpad",
      "--disable-browser-side-navigation",
      "--disable-client-side-phishing-detection",
      "--disable-default-apps",
      "--disable-dev-shm-usage",
      "--disable-extensions",
      "--disable-features=site-per-process",
      "--disable-hang-monitor",
      "--disable-popup-blocking",
      "--disable-prompt-on-repost",
      "--disable-sync",
      "--disable-translate",
      "--metrics-recording-only",
      "--no-first-run",
      "--safebrowsing-disable-auto-update",
      "--enable-automation",
      "--password-store=basic",
      "--use-mock-keychain"
  ));


  /**
   * 可执行文件
   */
  private File executablePath;
  /**
   * 启动参数
   */
  private final Map<String, String> arguments = new LinkedHashMap<>();
  /**
   * 超时时长
   */
  private long timeout = 1000;

  public File getExecutablePath() {
    return executablePath;
  }

  public LauncherOptions setExecutablePath(File executablePath) {
    this.executablePath = executablePath;
    return this;
  }

  public Map<String, String> getArguments() {
    return arguments;
  }

  public LauncherOptions add(String... args) {
    return add(Arrays.asList(args));
  }

  public LauncherOptions add(List<String> args) {
    return add(args, false);
  }

  public LauncherOptions add(List<String> args, boolean absent) {
    args.stream()
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .map(arg -> arg.contains("=") ? arg.split("=") : new String[]{arg, ""})
        .forEach(argValue -> {
          if (absent) {
            this.getArguments().putIfAbsent(argValue[0], argValue.length > 1 ? argValue[1] : "");
          } else {
            this.getArguments().put(argValue[0], argValue.length > 1 ? argValue[1] : "");
          }
        });
    return this;
  }

  /**
   * 使用默认参数
   */
  public LauncherOptions useDefaultArgs() {
    return add(DEFAULT_ARGS);
  }

  public LauncherOptions remove(String... args) {
    return remove(Arrays.asList(args));
  }

  public LauncherOptions remove(List<String> args) {
    args.stream()
        .map(String::trim)
        .map(arg -> arg.contains("=") ? arg.split("=") : new String[]{arg})
        .forEach(argValue -> getArguments().remove(argValue[0]));
    return this;
  }

  public long getTimeout() {
    return timeout;
  }

  public LauncherOptions setTimeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  public boolean has(String name) {
    return getArguments().containsKey(name);
  }

  public String getArgValue(String name) {
    return getArgValue(name, null);
  }

  public String getArgValue(String name, String defaultValue) {
    return getArguments().getOrDefault(name, defaultValue);
  }

  public LauncherOptions setUserDataDir(File dir) {
    return add("--user-data-dir=" + dir.getAbsolutePath().replace("\\", "/"));
  }


  public String getUserDataDir() {
    return getArgValue("--user-data-dir");
  }

  public LauncherOptions setRemoteDebuggingPort(int port) {
    return add("--remote-debugging-port=" + port);
  }

  public int getRemoteDebuggingPort() {
    return Integer.parseInt(getArgValue("--remote-debugging-port", "0"));
  }

  public String getArgumentsCommandLine() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : getArguments().entrySet()) {
      sb.append(" ").append(entry.getKey());
      if (StringUtils.isNotBlank(entry.getValue())) {
        sb.append("=").append(entry.getValue());
      }
    }
    return sb.toString();
  }
}
