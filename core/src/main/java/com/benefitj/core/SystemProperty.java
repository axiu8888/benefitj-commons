package com.benefitj.core;

import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统配置
 */
public class SystemProperty {

  private static final AtomicReference<WeakReference<Properties>> HOLDER = new AtomicReference<>();


  public static int getProcessors() {
    return Runtime.getRuntime().availableProcessors();
  }

  public static int getThreadCount(int ratio) {
    return getProcessors() * ratio;
  }

  public static Properties getSystemProperties() {
    WeakReference<Properties> ref = HOLDER.get();
    Properties prop;
    if (ref != null) {
      prop = ref.get();
      if (prop != null) {
        return prop;
      }
    }
    prop = System.getProperties();
    HOLDER.set(new WeakReference<>(prop));
    return prop;
  }

  public static String getProperty(String key) {
    return getSystemProperties().getProperty(key);
  }

  public static String getJavaVendor() {
    // Oracle Corporation
    return getProperty("java.vendor");
  }

  public static String getJavaExtDirs() {
    // D:\develop-env\Java\jdk1.8\jre\lib\ext;C:\Windows\Sun\Java\lib\ext
    return getProperty("java.ext.dirs");
  }

  public static String getJavaVersion() {
    // 1.8.0_121
    return getProperty("java.version");
  }

  public static String getJavaVMInfo() {
    // mixed mode
    return getProperty("java.vm.info");
  }

  public static String getAwtToolkit() {
    // sun.awt.windows.WToolkit
    return getProperty("awt.toolkit");
  }

  public static String getUserLanguage() {
    // zh
    return getProperty("user.language");
  }

  public static String getJavaSpecificationVendor() {
    // Oracle Corporation
    return getProperty("java.specification.vendor");
  }


  public static String getJavaHome() {
    // D:\develop-env\Java\jdk1.8\jre
    return getProperty("java.home");
  }


  public static String getJavaVMSpecificationVersion() {
    // 1.8
    return getProperty("java.vm.specification.version");
  }

  public static String getJavaClassPath() {
    return getProperty("java.class.path");
  }

  public static String getUserName() {
    // admin
    return getProperty("user.name");
  }

  public static String getFileEncoding() {
    // GBK
    return getProperty("file.encoding");
  }

  public static String getJavaSpecificationVersion() {
    // 1.8
    return getProperty("java.specification.version");
  }

  public static String getJavaAwtPrinterjob() {
    // sun.awt.windows.WPrinterJob
    return getProperty("java.awt.printerjob");
  }

  public static String getUserTimezone() {
    return getProperty("user.timezone");
  }

  public static String getUserHome() {
    // C:\Users\admin
    return getProperty("user.home");
  }

  public static String getOsVersion() {
    // 10.0
    return getProperty("os.version");
  }

  public static String getJavaSpecificationName() {
    // Java Platform API Specification
    return getProperty("java.specification.name");
  }

  public static String getJavaClassVersion() {
    // 52.0
    return getProperty("java.class.version");
  }

  public static String getJavaLibraryPath() {
    // D:\develop-env\Java\jdk1.8\bin;......
    return getProperty("java.library.path");
  }

  public static String getOsName() {
    // Windows 10
    return getProperty("os.name");
  }

  public static String getUserVariant() {
    return getProperty("user.variant");
  }

  public static String getJavaVMSpecificationVendor() {
    // Oracle Corporation
    return getProperty("java.vm.specification.vendor");
  }

  /**
   * java临时缓存目录
   */
  public static String getJavaIOTmpdir() {
    // C:\Users\admin\AppData\Local\Temp\
    return getProperty("java.io.tmpdir");
  }

  public static String getLineSeparator() {
    return getProperty("line.separator");
  }

  public static String getJavaEndorsedDirs() {
    // D:\develop-env\Java\jdk1.8\jre\lib\endorsed
    return getProperty("java.endorsed.dirs");
  }

  public static String getOSArch() {
    // amd64
    return getProperty("os.arch");
  }

  public static String getJavaAwtGraphicsenv() {
    // sun.awt.Win32GraphicsEnvironment
    return getProperty("java.awt.graphicsenv");
  }

  public static String getJavaRuntimeVersion() {
    // 1.8.0_121-b13
    return getProperty("java.runtime.version");
  }

  public static String getJavaVMSpecificationName() {
    // Java Virtual Machine Specification
    return getProperty("java.vm.specification.name");
  }

  public static String getUserDir() {
    // E:\code\company\projects\znsx-library
    return getProperty("user.dir");
  }

  public static String getUserScript() {
    return getProperty("user.script");
  }

  public static String getUserCountry() {
    // CN
    return getProperty("user.country");
  }

  public static String getFileEncodingPkg() {
    // sun.io
    return getProperty("file.encoding.pkg");
  }

  public static String getPathSeparator() {
    // ;
    return getProperty("path.separator");
  }

  public static String getJavaVMVersion() {
    // 25.121-b13
    return getProperty("java.vm.version");
  }

  public static String getJavaRuntimeName() {
    // Java(TM) SE Runtime Environment
    return getProperty("java.runtime.name");
  }


  @Deprecated
  public static String getSunCpuIsalist() {
    // amd64
    return getProperty("sun.cpu.isalist");
  }

  @Deprecated
  public static String getSunDesktop() {
    // windows
    return getProperty("sun.desktop");
  }

  @Deprecated
  public static String getSunIOUnicodeEncoding() {
    // UnicodeLittle
    return getProperty("sun.io.unicode.encoding");
  }

  @Deprecated
  public static String getSunCpuEndian() {
    // little
    return getProperty("sun.cpu.endian");
  }

  @Deprecated
  public static String getSunBootClassPath() {
    return getProperty("sun.boot.class.path");
  }

  @Deprecated
  public static String getSunJavaCommand() {
    // com.example.Test
    return getProperty("sun.java.command");
  }

  @Deprecated
  public static String getSunManagementCompiler() {
    // HotSpot 64-Bit Tiered Compilers
    return getProperty("sun.management.compiler");
  }

  @Deprecated
  public static String getSunJnuEncoding() {
    // GBK
    return getProperty("sun.jnu.encoding");
  }

  @Deprecated
  public static String getSunArchDataModel() {
    // 64
    return getProperty("sun.arch.data.model");
  }

  @Deprecated
  public static String getSunJavaLauncher() {
    // SUN_STANDARD
    return getProperty("sun.java.launcher");
  }


  //java.runtime.name
  //sun.boot.library.path
  //java.vm.version
  //java.vm.vendor
  //java.vendor.url
  //path.separator
  //java.vm.name
  //file.encoding.pkg
  //user.script
  //sun.java.launcher
  //user.country
  //sun.os.patch.level
  //java.vm.specification.name
  //user.dir
  //java.runtime.version
  //java.awt.graphicsenv
  //java.endorsed.dirs
  //os.arch
  //java.io.tmpdir
  //line.separator
  //java.vm.specification.vendor
  //user.variant
  //os.name
  //sun.jnu.encoding
  //java.library.path
  //java.specification.name
  //java.class.version
  //sun.management.compiler
  //os.version
  //user.home
  //user.timezone
  //java.awt.printerjob
  //file.encoding
  //java.specification.version
  //java.class.path
  //user.name
  //java.vm.specification.version
  //sun.java.command
  //java.home
  //sun.arch.data.model
  //user.language
  //java.specification.vendor
  //awt.toolkit
  //java.vm.info
  //java.version
  //java.ext.dirs
  //sun.boot.class.path
  //java.vendor
  //file.separator
  //java.vendor.url.bug
  //sun.io.unicode.encoding
  //sun.cpu.endian
  //sun.desktop
  //sun.cpu.isalist


}
