package com.benefitj.core.cmd;

import com.benefitj.core.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 系统
 */
public enum SystemOS {

  win32,
  win64,
  linux,
  mac,
  ;

  public boolean is_win() {
    return this == win32 || this == win64;
  }

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

  /**
   * 监测docker运行的系统
   */
  //if grep -qi microsoft /proc/version; then
  //    echo "宿主机是 Windows (WSL2/Docker Desktop)"
  //else
  //    echo "宿主机是 Linux"
  //fi
  public static final String DOCKER_PLATFORM_CMD = "\n"
      + "if grep -qi microsoft /proc/version; then\n"
      + "    echo \"Windows\"\n"
      + "else\n"
      + "    echo \"Linux\"\n"
      + "fi\n";

  static final SingletonSupplier<Boolean> windowsDocker = SingletonSupplier.of(() -> {
    CmdCall call = execWindowsDocker();
    return call != null && call.getMessage().contains("Windows");
  });

  public static CmdCall execWindowsDocker() {
    final File tmp = IOUtils.createFile("./DockerPlatform__" + IdUtils.uuid(0, 8) + ".sh");
    try {
      IOUtils.write(DOCKER_PLATFORM_CMD.getBytes(StandardCharsets.UTF_8), tmp);
      return CmdExecutor.get().call("bash " + tmp.getAbsolutePath());
    } catch (Exception e) {
      System.err.println(PlaceHolder.get().format("error --> \n{}", CatchUtils.getLogStackTrace(e)));
    } finally {
      IOUtils.delete(tmp);
    }
    return null;
  }

  /**
   * 是否为 windows docker
   */
  public static boolean isWindowsDocker() {
    return isWindows() || windowsDocker.get();
  }

}
