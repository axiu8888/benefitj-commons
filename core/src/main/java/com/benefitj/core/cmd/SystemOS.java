package com.benefitj.core.cmd;

import java.util.Locale;

/**
 * 系统
 */
public enum SystemOS {

  LOCALE;

  private final String name;
  private final String osName;

  SystemOS() {
    this.osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    if (isWindows()) {
      this.name = "windows";
    } else if (isMacOSX()) {
      this.name = "mac";
    } else {
      this.name = "linux";
    }
  }

  public String getName() {
    return name;
  }

  public String getOsName() {
    return osName;
  }

  public boolean isWindows() {
    return getOsName().contains("Windows");
  }

  public boolean isMacOSX() {
    return getOsName().contains("Mac");
  }

  public boolean isLinux() {
    return getOsName().contains("Linux");
  }

}
