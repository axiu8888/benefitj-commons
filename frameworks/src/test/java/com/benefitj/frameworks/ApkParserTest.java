package com.benefitj.frameworks;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
class ApkParserTest extends BaseTest {

  @Test
  void testParse() throws IOException {
    File apk = new File("D:/develop/adb/aurora-slim-1.0.2.apk");
    if (!(apk.exists() && apk.isFile())) {
      return;
    }
    try(final ApkFile apkFile = new ApkFile(apk);) {
      ApkMeta apkMeta = apkFile.getApkMeta();
      //  拷贝出的icon文件名 根据需要可以随便改
      log.info("应用名称\t: " + apkMeta.getLabel());
      log.info("包名\t: " + apkMeta.getPackageName());
      log.info("版本号\t: " + apkMeta.getVersionName());
      log.info("图标\t: " + apkMeta.getIcon());
      log.info("大小\t: " + Utils.fmtMB(apk.length(), "0.00 MB"));
      //  拷贝图标
      saveImages(apk, apkMeta, IOUtils.mkDirs("D:/tmp/apk/"));
    }
  }

  //  拷贝图标
  public static void saveImages(File apk, ApkMeta apkMeta, File cacheDir) throws IOException {
    //  访问apk 里面的文件
    ZipFile zf = new ZipFile(apk);
    List<ZipEntry> entries = find(zf, entry -> !entry.isDirectory() && entry.getName().endsWith(".png"));
    entries.forEach(entry -> {
      try {
        //  拷贝出图标
        File dest = IOUtils.createFile(cacheDir, entry.getName());
        IOUtils.write(zf.getInputStream(entry), dest);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    });

    ZipEntry iconEntry = getLauncherIcon(apkMeta, zf)
        .stream()
        .min((o1, o2) -> -Long.compare(o1.getSize(), o2.getSize()))
        .orElse(null);
    System.err.println("icon.name: " + iconEntry.getName());
    System.err.println("icon.size: " + Utils.fmtKB(iconEntry.getSize(), "0.00 KB"));
    System.err.println("icon.time: " + DateFmtter.fmt(iconEntry.getTime()));

    String name = iconEntry.getName();
    name = name.substring(name.lastIndexOf("/") + 1);
    IOUtils.write(zf.getInputStream(iconEntry), IOUtils.createFile(cacheDir, name));
    zf.close();
  }

  /**
   * 迭代 APK 的文件
   *
   * @param zf     Zip文件
   * @param filter 过滤
   * @return 返回匹配的数据
   */
  public static List<ZipEntry> find(ZipFile zf, Predicate<ZipEntry> filter) {
    ZipEntry ze;
    final List<ZipEntry> entries = new ArrayList<>();
    Enumeration<? extends ZipEntry> em = zf.entries();
    while (em.hasMoreElements()) {
      ze = em.nextElement();
      if (filter.test(ze)) {
        entries.add(ze);
      }
    }
    return entries;
  }

  /**
   * 查找启动图标
   *
   * @param meta 信息
   * @param zf   压缩文件
   * @return 返回全部的启动图标
   */
  public static List<ZipEntry> getLauncherIcon(ApkMeta meta, ZipFile zf) {
    String icon = meta.getIcon();
    String name = icon.substring(icon.lastIndexOf("/") + 1);
    return find(zf, entry -> !entry.isDirectory() && entry.getName().endsWith(name));
  }
}
