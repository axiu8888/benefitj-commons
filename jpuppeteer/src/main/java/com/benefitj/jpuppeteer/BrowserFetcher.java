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
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * 用于下载chrome浏览器
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

  public BrowserFetcher(File dir, String version, Product product) {
    this.revisionInfo = createRevisionInfo(dir, version, product, SystemOS.getLocale());
  }

  public static RevisionInfo createRevisionInfo(File dir, String version, Product product, SystemOS platform) {
    return RevisionInfo.builder()
        .folder(new File(dir, ".local-browser"))
        .platform(SystemOS.getLocale())
        .product(product)
        .platform(platform)
        .revision(version)
        .url(downloadURLs.get(product.name()).get(platform.name()))
        .build();
  }

  public RevisionInfo getRevisionInfo() {
    return revisionInfo;
  }

  public void setRevisionInfo(RevisionInfo revisionInfo) {
    this.revisionInfo = revisionInfo;
  }

  /**
   * <p>下载默认配置版本(722234)的浏览器, 下载到项目目录下</p>
   */
  public static RevisionInfo downloadIfNotExist() {
    return downloadIfNotExist(null);
  }

  /**
   * <p>下载浏览器, 如果项目目录下不存在对应版本时</p>
   * <p>如果不指定版本, 则使用默认配置版本</p>
   *
   * @param version 浏览器版本
   */
  public static RevisionInfo downloadIfNotExist(String version) {
    BrowserFetcher fetcher = BrowserFetcher.get();
    String downLoadVersion = StringUtils.isEmpty(version) ? VERSION : version;
    RevisionInfo revisionInfo = fetcher.revisionInfo(downLoadVersion);
    if (!revisionInfo.isLocal()) {
      return fetcher.download(downLoadVersion, (progress, total) -> {
        log.info("Download[{} - {}] progress: total[{}M], downloaded[{}M], {}"
            , revisionInfo.getProduct(), SystemOS.platform(), total, progress, Utils.fmt(((progress * 1.0) / total) * 100.0, "%"));
      });
    }
    return revisionInfo;
  }

  /**
   * 根据给定得浏览器版本下载浏览器, 可以利用下载回调显示下载进度
   *
   * @param revision         浏览器版本
   * @param progressCallback 下载回调
   * @return RevisionInfo
   */
  public RevisionInfo download(String revision, BiConsumer<Long, Long> progressCallback) {
    File folderPath = this.getFolderPath(revision);
    File executablePath = this.getExecutablePath(folderPath, revision);
    if (executablePath.exists()) {
      return this.revisionInfo(revision);
    }

    RevisionInfo info = revisionInfo;

    String url = downloadURL(info.getProduct(), info.getPlatform(), info.getUrl(), revision);
    File archivePath = new File(info.getFolder(), info.getPlatform() + "-" + revision + "/" + url.substring(url.lastIndexOf("/")));
    try {
      if (progressCallback == null) {
        progressCallback = (progress, total) -> {
          BigDecimal decimal1 = new BigDecimal(progress);
          BigDecimal decimal2 = new BigDecimal(total);
          int percent = decimal1.divide(decimal2, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();
          log.info("Download[{} - {}] progress: total[{}M], downloaded[{}M], {}", info.getProduct(), info.getPlatform(), decimal2, decimal1, percent + "%");
        };
      }
      downloadFile(url, archivePath, progressCallback);
      install(archivePath, folderPath);
    } finally {
      archivePath.delete();
    }
    RevisionInfo revisionInfo = this.revisionInfo(revision);
    if (revisionInfo != null) {
      try {
        File executableFile = revisionInfo.getFolder();
        executableFile.setExecutable(true, false);
      } catch (Exception e) {
        log.error("Set executablePath:{} file execution permission fail.", revisionInfo.getFolder());
      }
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
  public void extractTar(File archive, File destDir) {
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
  public static void extractZip(File archive, File destDir) {
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
  }

  /**
   * Install *.app directory from dmg file
   *
   * @param archive zip路径
   * @param destDir 存放路径
   */
  public static void installDMG(File archive, File destDir) {
    try {
      net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(archive);
      destDir.mkdirs();
      zipFile.extractAll(destDir.getAbsolutePath());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 下载浏览器到具体的路径
   * ContentTypeapplication/x-zip-compressed
   *
   * @param url              url
   * @param archivePath      zip路径
   * @param progressCallback 回调函数
   */
  private void downloadFile(String url, File archivePath, BiConsumer<Long, Long> progressCallback) {
    log.info("Downloading binary from " + url);
    File dest = IOUtils.createFile(archivePath);
    HttpHelper.get().download(url, dest.getParentFile(), dest.getName(), false, new FileProgressListener() {
      @Override
      public void onProgressChange(long totalLength, long progress, boolean done) {
        progressCallback.accept(progress, totalLength);
      }

      @Override
      public void onFailure(okhttp3.Call call, @Nonnull Exception e, @Nullable File file) {
        throw new IllegalStateException(e);
      }
    });
    log.info("Download successfully from " + url);
  }

  /**
   * 根据浏览器版本获取对应浏览器路径
   *
   * @param revision 浏览器版本
   * @return string
   */
  public File getFolderPath(String revision) {
    return new File(revisionInfo.getFolder(), revisionInfo.getPlatform() + "-" + revision);
  }

  /**
   * 获取浏览器版本相关信息
   *
   * @param revision 版本
   * @return RevisionInfo
   */
  public RevisionInfo revisionInfo(String revision) {
    File folder = this.getFolderPath(revision);
    File executablePath = this.getExecutablePath(folder, revision);
    RevisionInfo info = revisionInfo;
    String url = downloadURL(info.getProduct(), info.getPlatform(), info.getUrl(), revision);
    boolean local = executablePath.exists();
    log.trace("revision: {}, executablePath: {}, folder: {}, local:{}, url: {}, product: {}", revision, executablePath, folder, local, url, info.getProduct());
    return RevisionInfo.builder()
        .folder(folder)
        .revision(revision)
        .url(url)
        .product(info.getProduct())
        .local(local)
        .build();
  }

  /**
   * 获取可执行路径
   *
   * @param folderPath 目录
   * @param revision   版本
   * @return 返回可执行地址
   */
  public File getExecutablePath(File folderPath, String revision) {
    return getExecutablePath(folderPath, revisionInfo.getProduct(), revisionInfo.getPlatform(), revision);
  }

  /**
   * 确定下载的路径
   *
   * @param product  产品：chrome or firefox
   * @param platform win linux mac
   * @param host     域名地址
   * @param revision 版本
   * @return 下载浏览器的url
   */
  public String downloadURL(Product product, SystemOS platform, String host, String revision) {
    String baseUrl = downloadURLs.get(product.name()).get(platform.name());
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
   * @param folderPath 目录
   * @param product    产品
   * @param platform   平台
   * @param revision   版本
   * @return 返回可执行地址
   */
  public static File getExecutablePath(File folderPath, Product product, SystemOS platform, String revision) {
    String executablePath;
    if (product == Product.chrome) {
      if (platform == SystemOS.mac) {
        executablePath = getPath(folderPath, archiveName(product, platform, revision), "Chromium.app", "Contents", "MacOS", "Chromium");
      } else if (platform == SystemOS.linux) {
        executablePath = getPath(folderPath, archiveName(product, platform, revision), "chrome");
      } else if (platform == SystemOS.win32 || platform == SystemOS.win64) {
        executablePath = getPath(folderPath, archiveName(product, platform, revision), "chrome.exe");
      } else {
        throw new IllegalArgumentException("Unsupported platform: " + platform);
      }
    } else if (product == Product.firefox) {
      if (platform == SystemOS.mac) {
        executablePath = getPath(folderPath, "Firefox Nightly.app", "Contents", "MacOS", "firefox");
      } else if (platform == SystemOS.linux) {
        executablePath = getPath(folderPath, "firefox", "firefox");
      } else if (platform == SystemOS.win32 || platform == SystemOS.win64) {
        executablePath = getPath(folderPath, "firefox", "firefox.exe");
      } else {
        throw new IllegalArgumentException("Unsupported platform: " + platform);
      }
    } else {
      throw new IllegalArgumentException("Unsupported product: " + product);
    }
    return new File(executablePath);
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
