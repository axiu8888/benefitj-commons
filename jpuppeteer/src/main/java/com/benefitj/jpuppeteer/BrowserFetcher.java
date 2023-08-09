package com.benefitj.jpuppeteer;

import com.benefitj.core.IOUtils;
import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.Utils;
import com.benefitj.core.cmd.SystemOS;
import com.benefitj.core.file.CompressUtils;
import com.benefitj.frameworks.TarUtils;
import com.benefitj.http.FileProgressListener;
import com.benefitj.http.HttpHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


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
      TarUtils.extractTar(archive, destDir);
    } else if (name.endsWith(".dmg")) {
      TarUtils.extractDMG(archive, destDir);
    } else {
      throw new IllegalArgumentException("Unsupported archive format: " + archive);
    }
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
   * 版本信息
   */
  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class RevisionInfo {

    /**
     * 版本
     */
    private String revision;
    /**
     * 下载的目录
     */
    private File folder;

    /**
     * 下载的URL路径
     */
    private String url;

    /**
     * 平台 win linux mac
     */
    @Builder.Default
    private SystemOS platform = SystemOS.getLocale();
    /**
     * 目前支持两种产品：chrome or firefix
     */
    @Builder.Default
    private Product product = Product.chrome;

  }

}
