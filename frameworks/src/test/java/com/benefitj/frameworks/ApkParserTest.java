package com.benefitj.frameworks;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class ApkParserTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void testParse() throws IOException {
    File apk = new File("D:/tmp/伤员转运-230815.apk");
    if (apk.exists() && apk.isFile()) {
      ApkFile apkFile = new ApkFile(apk);
      ApkMeta apkMeta = apkFile.getApkMeta();

      //  拷贝出的icon文件名 根据需要可以随便改
      System.out.println("应用名称\t: " + apkMeta.getLabel());
      System.out.println("包名\t: " + apkMeta.getPackageName());
      System.out.println("版本号\t: " + apkMeta.getVersionName());
      System.out.println("图标\t: " + apkMeta.getIcon());
      System.out.println("大小\t: " + Utils.fmtMB(apk.length(), "0.00 MB"));
      //  System.out.println("全部       :===============================");
      //  System.out.println(apkMeta.toString());

      //  拷贝图标
      saveImages(apk, apkMeta);
    }
  }

  //  拷贝图标
  public static void saveImages(File apk, ApkMeta apkMeta) throws IOException {
    //  访问apk 里面的文件
    ZipFile zf = new ZipFile(apk);
    List<ZipEntry> entries = find(zf, entry -> !entry.isDirectory() && entry.getName().endsWith(".png"));
    entries.forEach(entry -> {
      try {
        //  拷贝出图标
        IOUtils.write(zf.getInputStream(entry), IOUtils.createFile(apk.getParentFile(), entry.getName()), true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    ZipEntry iconEntry = getLauncherIcon(apkMeta, zf)
        .stream()
        .min((o1, o2) -> -Long.compare(o1.getSize(), o2.getSize()))
        .orElse(null);
    System.err.println("icon.name: " + iconEntry.getName());
    System.err.println("icon.size: " + Utils.fmtKB(iconEntry.getSize(), "0.00 KB"));
    System.err.println("icon.time: " + DateFmtter.fmt(iconEntry.getTime()));
    IOUtils.write(zf.getInputStream(iconEntry), IOUtils.createFile(apk.getParentFile(), iconEntry.getName()), true);

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
    Enumeration<? extends ZipEntry> enumeration = zf.entries();
    while (enumeration.hasMoreElements()) {
      ze = enumeration.nextElement();
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
    String icon = meta.getIcon().substring(meta.getIcon().lastIndexOf("/"));
    return find(zf, entry -> !entry.isDirectory() && entry.getName().endsWith(icon));
  }
}
