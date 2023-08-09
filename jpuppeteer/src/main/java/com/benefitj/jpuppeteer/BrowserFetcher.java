package com.benefitj.jpuppeteer;

import com.benefitj.core.IOUtils;
import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.Utils;
import com.benefitj.core.cmd.SystemOS;
import com.benefitj.core.file.CompressUtils;
import com.benefitj.http.FileProgressListener;
import com.benefitj.http.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * 用于下载chrome/firefox浏览器
 */
@Slf4j
public class BrowserFetcher {

  public static final String VERSION = "1132420"; // 浏览器版本

  static final SingletonSupplier<BrowserFetcher> singleton = SingletonSupplier.of(BrowserFetcher::new);

  public static BrowserFetcher get() {
    return singleton.get();
  }

  public static final Map<String, Map<String, String>> downloadURLs = Collections.unmodifiableMap(new LinkedHashMap<String, Map<String, String>>() {
    private static final long serialVersionUID = -6918778699407093058L;

    {
      put("chrome", new HashMap<String, String>() {
        private static final long serialVersionUID = 3441562966233820720L;

        {
          put("host", "https://npm.taobao.org/mirrors");
          put("linux", "%s/chromium-browser-snapshots/Linux_x64/%s/%s.zip");
          put("mac", "%s/chromium-browser-snapshots/Mac/%s/%s.zip");
          put("win32", "%s/chromium-browser-snapshots/Win/%s/%s.zip");
          put("win64", "%s/chromium-browser-snapshots/Win_x64/%s/%s.zip");
        }
      });
      put("firefox", new HashMap<String, String>() {
        private static final long serialVersionUID = 2053771138227029401L;

        {
          put("host", "https://github.com/puppeteer/juggler/releases");
          put("linux", "%s/download/%s/%s.zip");
          put("mac", "%s/download/%s/%s.zip");
          put("win32", "%s/download/%s/%s.zip");
          put("win64", "%s/download/%s/%s.zip");
        }
      });
    }
  });

  private RevisionInfo revisionInfo;

  public BrowserFetcher() {
    this(new File(System.getProperty("user.dir")), VERSION, Product.chrome);
  }

  public BrowserFetcher(File folder, String version, Product product) {
    this.revisionInfo = revisionInfo(folder, version, product, SystemOS.getLocale());
  }

  public RevisionInfo getRevisionInfo() {
    return revisionInfo;
  }

  public void setRevisionInfo(RevisionInfo revisionInfo) {
    this.revisionInfo = revisionInfo;
  }

  /**
   * 根据给定得浏览器版本下载浏览器, 可以利用下载回调显示下载进度
   *
   * @return RevisionInfo
   */
  public RevisionInfo download() {
    RevisionInfo info = getRevisionInfo();
    String revision = info.getRevision();
    File executablePath = getExecutablePath(info.getFolder(), info.getProduct(), info.getPlatform(), revision);
    if (executablePath.exists()) {
      return info;
    }
    String url = info.getUrl();
    String filename = info.getPlatform() + "-" + revision;
    File archive = IOUtils.createFile(info.getFolder(), filename + "/" + url.substring(url.lastIndexOf("/")));
    try {
      log.info("Downloading binary from " + url);
      HttpHelper.get().download(url, archive.getParentFile(), archive.getName(), false, new FileProgressListener() {
        @Override
        public void onProgressChange(long totalLength, long progress, boolean done) {
          double v = ((progress * 1.0) / totalLength) * 100;
          log.info("Download: {}, total: {}MB, progress: {}%, done: {}", archive, Utils.fmtMB(totalLength, "0.00"), Utils.fmt(v, "0.00"), done);
        }
      });
      log.info("Download successfully from " + url);
      install(archive, new File(info.getFolder(), filename));
    } finally {
      archive.delete();
    }
    return revisionInfo;
  }

  /**
   * intall archive file: *.zip,*.tar.bz2,*.dmg
   *
   * @param archive zip路径
   * @param destDir 存放的路径
   */
  private void install(File archive, File destDir) {
    log.info("Installing " + archive + " to " + destDir);
    String name = archive.getName();
    if (name.endsWith(".zip")) {
      CompressUtils.unzip(archive, destDir);
    } else if (name.endsWith(".tar.bz2")) {
      extractTar(archive, destDir);
    } else if (name.endsWith(".dmg")) {
      installDMG(archive, destDir);
    } else {
      throw new IllegalArgumentException("Unsupported archive format: " + archive);
    }
  }

  /**
   * 解压tar文件
   *
   * @param archive zip路径
   * @param destDir 存放路径
   */
  public static void extractTar(File archive, File destDir) {
    try (final TarArchiveInputStream tarArchiveIs = new TarArchiveInputStream(new FileInputStream(archive));) {
      byte[] buf = new byte[1024 << 10];
      ArchiveEntry nextEntry;
      while ((nextEntry = tarArchiveIs.getNextEntry()) != null) {
        File dest = new File(destDir, nextEntry.getName());
        if (nextEntry.isDirectory()) {
          dest.mkdirs();
        } else {
          try (final BufferedInputStream in = new BufferedInputStream(tarArchiveIs);
               final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(IOUtils.createFile(dest)));) {
            int len;
            while ((len = in.read(buf)) > 0) {
              out.write(buf, 0, len);
              out.flush();
            }
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 解压zip文件
   *
   * @param archive zip路径
   * @param destDir 存放路径
   */
  public static File extractZip(File archive, File destDir) {
    try (ZipFile zipFile = new ZipFile(archive)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      byte[] buf = new byte[1024 << 10];
      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        Path path = Paths.get(destDir.getAbsolutePath(), zipEntry.getName());
        if (zipEntry.isDirectory()) {
          path.toFile().mkdirs();
        } else {
          try (final BufferedInputStream reader = new BufferedInputStream(zipFile.getInputStream(zipEntry));
               final BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(path.toString()));) {
            int perReadcount;
            while ((perReadcount = reader.read(buf, 0, buf.length)) != -1) {
              writer.write(buf, 0, perReadcount);
              writer.flush();
            }
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return destDir;
  }

  /**
   * Install *.app directory from dmg file
   *
   * @param archive zip路径
   * @param destDir 存放路径
   */
  public static File installDMG(File archive, File destDir) {
    try {
      net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(archive);
      destDir.mkdirs();
      zipFile.extractAll(destDir.getAbsolutePath());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return destDir;
  }

  /**
   * <p>下载默认配置版本(722234)的浏览器, 下载到项目目录下</p>
   */
  public static RevisionInfo downloadIfNotExist() {
    return downloadIfNotExist(BrowserFetcher.get());
  }

  /**
   * <p>下载浏览器, 如果项目目录下不存在对应版本时</p>
   * <p>如果不指定版本, 则使用默认配置版本</p>
   */
  public static RevisionInfo downloadIfNotExist(BrowserFetcher fetcher) {
    RevisionInfo revisionInfo = fetcher.getRevisionInfo();
    File executablePath = getExecutablePath(revisionInfo);
    if (!executablePath.exists()) {
      return fetcher.download();
    }
    return revisionInfo;
  }

  public static RevisionInfo revisionInfo(File folder, String reversion, Product product, SystemOS platform) {
    String url = downloadURL(product, platform, reversion);
    return RevisionInfo.builder()
        .folder(folder)
        .product(product)
        .platform(platform)
        .revision(reversion)
        .url(url)
        .build();
  }

  /**
   * 确定下载的路径
   *
   * @param product  产品：chrome or firefox
   * @param platform win linux mac
   * @param revision 版本
   * @return 下载浏览器的url
   */
  public static String downloadURL(Product product, SystemOS platform, String revision) {
    Map<String, String> platformUrls = downloadURLs.get(product.name());
    String host = platformUrls.get("host");
    String baseUrl = platformUrls.get(platform.name());
    return String.format(baseUrl, host, revision, archiveName(product, platform, revision));
  }

  /**
   * 根据平台信息和版本信息确定要下载的浏览器压缩包
   *
   * @param product  产品
   * @param platform 平台
   * @param revision 版本
   * @return 压缩包名字
   */
  public static String archiveName(Product product, SystemOS platform, String revision) {
    if (product == Product.chrome) {
      if (platform == SystemOS.linux) {
        return "chrome-linux";
      }
      if (platform == SystemOS.mac) {
        return "chrome-mac";
      }
      if (platform == SystemOS.win32 || platform == SystemOS.win64) {
        // Windows archive name changed at r591479.
        return Integer.parseInt(revision) > 591479 ? "chrome-win" : "chrome-win32";
      }
    } else if (product == Product.firefox) {
      if (platform == SystemOS.linux) {
        return "firefox-linux";
      }
      if (platform == SystemOS.mac) {
        return "firefox-mac";
      }
      if (platform == SystemOS.win32 || platform == SystemOS.win64) {
        return "firefox-" + platform;
      }
    }
    return null;
  }

  /**
   * 获取可执行路径
   *
   * @param info 版本信息
   * @return 返回可执行地址
   */
  public static File getExecutablePath(RevisionInfo info) {
    return getExecutablePath(info.getFolder(), info.getProduct(), info.getPlatform(), info.getRevision());
  }

  /**
   * 获取可执行路径
   *
   * @param folder   目录
   * @param product  产品
   * @param platform 平台
   * @param revision 版本
   * @return 返回可执行地址
   */
  public static File getExecutablePath(File folder, Product product, SystemOS platform, String revision) {
    String path;
    String platformReversion = platform + "-" + revision;
    if (product == Product.chrome) {
      String archiveName = archiveName(product, platform, revision);
      if (platform == SystemOS.mac) {
        path = getPath(folder, platformReversion, archiveName, "Chromium.app", "Contents", "MacOS", "Chromium");
      } else if (platform == SystemOS.linux) {
        path = getPath(folder, platformReversion, archiveName, "chrome");
      } else if (platform == SystemOS.win32 || platform == SystemOS.win64) {
        path = getPath(folder, platformReversion, archiveName, "chrome.exe");
      } else {
        throw new IllegalArgumentException("Unsupported platform: " + platform);
      }
    } else if (product == Product.firefox) {
      if (platform == SystemOS.mac) {
        path = getPath(folder, platformReversion, "Firefox Nightly.app", "Contents", "MacOS", "firefox");
      } else if (platform == SystemOS.linux) {
        path = getPath(folder, platformReversion, "firefox", "firefox");
      } else if (platform == SystemOS.win32 || platform == SystemOS.win64) {
        path = getPath(folder, platformReversion, "firefox", "firefox.exe");
      } else {
        throw new IllegalArgumentException("Unsupported platform: " + platform);
      }
    } else {
      throw new IllegalArgumentException("Unsupported product: " + product);
    }
    return new File(path);
  }

  public static String getPath(File root, String... args) {
    return java.nio.file.Paths.get(root.getAbsolutePath(), args).toString();
  }

  /**
   * 检测给定的路径是否存在
   *
   * @param filePath 文件路径
   * @return boolean
   */
  public static boolean exists(String filePath) {
    return Files.exists(Paths.get(filePath));
  }

}
