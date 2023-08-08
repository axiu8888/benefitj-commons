package com.benefitj.core.cmd;

/**
 * 系统
 */
public enum SystemOS {

  win32,
  win64,
  linux,
  mac,
  ;

  public static SystemOS getLocale() {
    if (isWindows()) {
      return isWin64() ? win64 : win32;
    }
    if (isLinux()) {
      return linux;
    }
    if (isMac()) {
      return mac;
    }
    throw new IllegalStateException("未知的操作系统: " + platform());
  }

  public static boolean isWindows() {
    return platform(true).contains("windows");
  }

  public static boolean isMac() {
    return platform(true).contains("mac");
  }

  public static boolean isLinux() {
    return platform(true).contains("linux");
  }

  /**
   * 是否是win64
   *
   * @return true is win64
   */
  public static boolean isWin64() {
    return System.getProperty("os.arch").contains("64");
  }

  public static String platform() {
    return platform(false);
  }

  public static String platform(boolean lowercase) {
    String os = System.getProperty("os.name");
    return lowercase ? os.toLowerCase() : os;
  }

}
