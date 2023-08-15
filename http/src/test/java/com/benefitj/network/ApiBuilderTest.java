package com.benefitj.network;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.*;
import com.benefitj.core.file.IWriter;
import com.benefitj.core.file.PathWatcher;
import com.benefitj.http.*;
import io.reactivex.Observable;
import junit.framework.TestCase;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ApiBuilderTest extends TestCase {

  private Logger log = LoggerFactory.getLogger(getClass());

  private ServiceApi api;

  public void setUp() throws Exception {
    this.api = ApiBuilder.createApiProxy(ServiceApi.class
        , ServiceApi.BASE_URL
        , builder -> builder.setLogLevel(HttpLoggingInterceptor.Level.NONE)
    );
  }

  @Test
  public void testRequest() {
//    api.getJS()
//        .subscribe(js -> log.info("js: \n{}", js));

//    // 写入文件
//    api.getBody()
//        .subscribe(body -> BodyUtils.transferTo(body, new File("D:/tmp/super_load-eb15f1e5a8.js")));
    // 写入文件
//    api.getImg()
//        .subscribe(body -> BodyUtils.transferTo(body, new File("D:/tmp/ew4nf5737jvn.jpg_760w.png")));

    CountDownLatch latch = new CountDownLatch(1);
    api.getImg()
        .subscribe(body -> {
          log.info("线程: {}", EventLoop.threadName());
          final IWriter img = IWriter.createWriter("D:/tmp/ew4nf5737jvn.jpg_760w.png", false);
          BodyUtils.progressResponseBody(body
              , (buf, len) -> img.write(buf, 0, len)
              , (totalLength, progress, done) ->
                  log.info("总长度: {}, 已下载: {}, 进度: {}%， done[{}]"
                      , totalLength
                      , progress
                      , Utils.fmt((progress * 100.f) / totalLength, "0.00")
                      , done
                  ));
          latch.countDown();
        });
    CatchUtils.ignore(() -> latch.await());
  }

  @Test
  public void testUploadFile() {
    long start = TimeUtils.now();
    File file = new File("D:/develop/tools/simulator.zip");
    final AtomicInteger index = new AtomicInteger();
    api.upload(BodyUtils.progressRequestBody(file, "files", (totalLength, progress, done) -> {
          if (index.incrementAndGet() % 50 == 0 || done) {
            log.info("总长度: {}, 已上传: {}, 进度: {}%， done[{}]"
                , totalLength
                , progress
                , Utils.fmt((progress * 100.f) / totalLength, "0.00")
                , done
            );
          }
        }))
        .subscribe(SimpleObserver.create(result -> log.info("上传结果: {}", result)));
    log.info("耗时: {}", TimeUtils.diffNow(start));
  }

  @Test
  public void testDownload() {
    // 下载
    long start = TimeUtils.now();
    api.download("simulator.zip")
        .subscribe(SimpleObserver.create(response -> {
          if (!response.isSuccessful()) {
            log.info("请求失败, {}, {}", response.code(), response.message());
            return;
          }
          // 处理响应
          final AtomicInteger index = new AtomicInteger();
          BodyUtils.progressResponseBody(response.body()
              , new File("D:/tmp/simulator2.zip") // 写入文件中
              , (totalLength, progress, done) -> {
                if (index.incrementAndGet() % 50 == 0 || done) {
                  log.info("总长度: {}, 已下载: {}, 进度: {}%， done[{}]"
                      , totalLength
                      , progress
                      , Utils.fmt((progress * 100.f) / totalLength, "0.00")
                      , done
                  );
                }
              });
        }));
    log.info("耗时: {}", TimeUtils.diffNow(start));
  }

  @Test
  public void testDownloadFile() {
    long start = TimeUtils.now();
    //okhttp3.Response response = HttpHelper.get().get("https://downloads.gradle-dn.com/distributions/gradle-7.3.2-all.zip");
    //okhttp3.Response response = HttpHelper.get().get("https://raw.github.com/axiu8888/GradleBuild/main/Java/base.gradle");
    okhttp3.Response response = HttpHelper.get().get("https://gitee.com/axiu8888/GradleBuild/raw/main/Java/base.gradle");
    final AtomicInteger index = new AtomicInteger();
    BodyUtils.progressResponseBody(response.body()
        , IOUtils.createFile("D:/tmp/base.gradle")
        , (totalLength, progress, done) -> {
          if (index.incrementAndGet() % 100 == 0 || done) {
            log.info("总长度: {}, 已下载: {}, 进度: {}%， done[{}]"
                , totalLength
                , progress
                , Utils.fmt((progress * 100.f) / totalLength, "0.00")
                , done
            );
          }
        });
    log.info("耗时: {}", TimeUtils.diffNow(start));

  }

  @Test
  public void testDownloadFile2() {
    long start = TimeUtils.now();
    String url = "https://dl-tc.coolapkmarket.com/down/apk_file/2022/1104/Coolapk-12.5.2-2211031-coolapk-app-sign.apk?t=1677423508&sign=135b70037c588e76c96bb633a2bffffd";
//    String url = "http://192.168.85.128/api/athenapdf/create?filename=&force&encodeType=&url=https://www.cnblogs.com/felixzh/p/5869212.html";
//    String url = "http://192.168.85.128/api/athenapdf/create?filename=&force&encodeType=&url=https://xilidou.com/2022/05/09/sre6/";
    HttpHelper.get().download(url, new File("D:/tmp/"), null, false, new FileProgressListener() {
      @Override
      public void onStart(Call call) {
        log.info("开始下载...");
      }

      @Override
      public void onProgressChange(long totalLength, long progress, boolean done) {
        log.info("总长度: {}, 已下载: {}, 进度: {}%， done[{}]"
            , totalLength
            , progress
            , Utils.fmt((progress * 100.f) / totalLength, "0.00")
            , done
        );
      }

      @Override
      public void onSuccess(Call call, @NotNull okhttp3.Response response, @Nullable File file) {
        log.error("下载成功：{}, {}", file.getName(), Utils.fmtMB(file.length(), "0.00MB"));
        log.error("headers: {}", response.headers().toMultimap());
      }

      @Override
      public void onFailure(Call call, @NotNull Exception e, @Nullable File file) {
        log.error("下载失败", e);
      }

      @Override
      public void onFinish(Call call) {
        log.info("耗时: {}", TimeUtils.diffNow(start));
      }
    });
  }

  @Test
  public void testWebSocket2() {
    AtomicReference<WebSocket> socketRef = new AtomicReference<>();
    new PathWatcher(Paths.get("D:/tmp/.local-browser/win64-1132420/userDataDir/"))
        .setKinds(StandardWatchEventKinds.ENTRY_MODIFY)
        .setWatchEventListener((watcher, key, path, filename, kind) -> {
          if (filename.equalsIgnoreCase("ws-info.txt")) {
            log.info("文件：" + ((new File(path.toFile(), filename)) + " " + PathWatcher.ofDesc(kind)) + ", 发生事件：" + kind.name() + ", " + DateFmtter.fmtNowS());
            String wsEndpoint = FileUtil.readString(new File(path.toFile(), filename), StandardCharsets.UTF_8);
            log.info("wsEndpoint: {}", wsEndpoint);
            if (socketRef.get() == null) {
              EventLoop.io().execute(() -> {
                CountDownLatch latch = new CountDownLatch(1);
                WebSocket socket = createWebSocket(wsEndpoint, latch, socketRef);
                socketRef.set(socket);
                CatchUtils.ignore(() -> latch.await());
                socket.send("{\"id\":1,\"method\":\"Target.setDiscoverTargets\",\"params\":{\"discover\":true}}");
              });
            }
          }
        })
        .start();
  }

  @Test
  public void testWebSocket() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    String url = "ws://127.0.0.1:80/api/sockets/simple";
    WebSocket socket = createWebSocket(url, latch, new AtomicReference<>());
    latch.await();
    for (int i = 0; i < 20; i++) {
      socket.send("about:blank");
      EventLoop.sleepSecond(1);
    }
    EventLoop.sleepSecond(10);
    socket.close(1000, "done");
  }

  public WebSocket createWebSocket(String url, CountDownLatch latch, AtomicReference<WebSocket> socketRef) {
    return HttpClient.newWebSocket(url, new WebSocketListener() {
      @Override
      public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
        log.info("onOpen, code: {}, {}, {}", response.code(), response.message(), CatchUtils.ignore(() -> response.body().string()));
        socketRef.set(webSocket);
        if (latch != null) {
          latch.countDown();
        }
      }

      @Override
      public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        log.info("onMessage, text: {}", text);
      }

      @Override
      public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        log.info("onMessage, bytes: {}", HexUtils.bytesToHex(bytes.toByteArray()));
      }

      @Override
      public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable e, @Nullable okhttp3.Response response) {
        log.info("onFailure, error: {}, {}, {}", e.getMessage(), response != null ? response.message() : null, CatchUtils.ignore(() -> response.body().string()));
        if (latch != null) {
          latch.countDown();
        }
        e.printStackTrace();
      }

      @Override
      public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("onClosing, code: {}, reason: {}", code, reason);
      }

      @Override
      public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("onClosed, code: {}, reason: {}", code, reason);
        socketRef.set(null);
        if (latch != null) {
          latch.countDown();
        }
      }
    });
  }

  @Test
  public void testDownloadPdf() {
    String url = "http://192.168.1.198/api/report/download?reportZid=0fd2a02669bb4a0d842df1ff92d56581&login=admin";
    okhttp3.Response response = HttpHelper.get()
        .setLogLevel(HttpLoggingInterceptor.Level.BODY)
        .get(url);
    if (response.isSuccessful()) {
      log.info("headers ==>: \n{}", response.headers());
      String filename = BodyUtils.getFilename(response.headers());
      log.info("filename: {}", filename);
      filename = filename.endsWith(".pdf") ? filename : filename + ".pdf";
      BodyUtils.progressResponseBody(response.body()
          , new File("D:/" + filename)
          , (totalLength, progress, done) -> log.info("totalLength: {}, progress: {}, done: {}", totalLength, progress, done)
      );
    } else {
      log.info("error: " + response.message());
    }

  }

  @Test
  public void testLogin() throws IOException {
    okhttp3.Response response = HttpHelper.get()
        .post("http://192.168.1.198/api/login/user",
            BodyUtils.jsonBody(JSON.toJSONBytes(new HashMap<String, String>() {{
              put("loginName", "admin");
              put("loginPassword", HexUtils.bytesToHex("hsrg8888".getBytes(StandardCharsets.UTF_8)));
            }}))
        );

    if (response.isSuccessful()) {
      log.info("headers ==>: \n{}", response.headers());
      String body = response.body().string();
      log.info("body ==>: " + body);
    } else {
      log.info("error: " + response.message());
    }
  }

  @Test
  public void testDownload2() throws Exception {
    String url = "https://npm.taobao.org/mirrors/chromium-browser-snapshots/Win_x64/1132420/chrome-win.zip";
    okhttp3.Response response = HttpHelper.get().get(url);
    log.info("headers: {}", response.headers());
    File dest = IOUtils.createFile("D:/tmp/chrome-win222.zip");
    BodyUtils.transferTo(response.body(), dest, (total, progress) -> {
      log.info("{}, {}, {}， {}"
          , dest.getName()
          , total
          , progress,
          Utils.fmt((progress * 100.0) / total, "0.00")
      );
    });

  }

  @Test
  public void testDownload3() throws Exception {
//    String url = "https://mirrors.tuna.tsinghua.edu.cn/centos-stream/9-stream/BaseOS/x86_64/iso/CentOS-Stream-9-latest-x86_64-dvd1.iso";
    String url = "https://npm.taobao.org/mirrors/chromium-browser-snapshots/Win_x64/1132420/chrome-win.zip";
//    String url = "http://127.0.0.1/api/files/download?bucketName=test&filename=/测试/chrome-win.zip";
    okhttp3.Response response = HttpHelper.get()
        .setGzipEnable(false)
        .get(url);
    if (response.isSuccessful()) {
      log.info("headers ==>: \n{}", response.headers());

      HttpUrl httpUrl = response.request().url();
      String filename = httpUrl.queryParameter("filename");
      filename = StringUtils.isNotBlank(filename) ? filename : httpUrl.pathSegments().get(httpUrl.pathSize() - 1);
      filename = filename.substring(filename.lastIndexOf("/") + 1);

      long MAX_SIZE = 200 * Utils.MB;
      long contentLength = BodyUtils.getContentLength(response.headers());
      CountDownLatch latch = new CountDownLatch((int) (contentLength / MAX_SIZE) + 1);
      File dest = IOUtils.createFile("D:/tmp/" + filename);
      new RandomAccessFile(dest, "rw").setLength(contentLength);
      for (int i = 0, j = 0; i < contentLength; i += MAX_SIZE, j++) {
        long startPosition = i, endPosition = Math.min(i + MAX_SIZE, contentLength) - 1;
        int index = j;
        EventLoop.asyncIO(() -> {
          try {
            download(IOUtils.createFile(dest.getParentFile(), index + "__" + dest.getName()), url, startPosition, endPosition, index);
          } finally {
            latch.countDown();
            log.info("{} end: [{} - {}]", index, startPosition, endPosition);
          }
        });
      }
      latch.await();
      System.err.println("---------------------------------");
    } else {
      log.info("error: {}, code: {}", response.message(), response.code());
    }
  }

  public void download(File dest, String url, long startPosition, long endPosition, int index) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Range", "bytes=" + startPosition + "-" + endPosition);
    headers.put("accept-encoding", "gzip, deflate, br");
    okhttp3.Response response = HttpHelper.get()
        .setGzipEnable(false)
        .get(url, headers);
    log.warn("{}, start~end: {} ~ {}. headers: {}", dest.getName(), startPosition, endPosition, response.headers());
    long totalLength = BodyUtils.getContentLength(response.headers());

    // 直接保存
    try (final RandomAccessFile rw = new RandomAccessFile(dest, "rw");) {
      AtomicLong progress = new AtomicLong();
      IOUtils.read(response.body().byteStream(), 1024 << 10, true, (buf, len) -> {
        progress.addAndGet(len);
        log.info("{} [{} - {}]下载中：{}, totalLength: {}, len: {}, progress: {}%, ..."
            , index
            , startPosition
            , endPosition
            , dest.getName()
            , Utils.ofMB(totalLength, 2)
            , len
            , Utils.fmt(((progress.get() * 1.0) / totalLength * 100.0), "0.00")
        );
        //rw.write(buf, 0, len);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }


//    BodyUtils.transferTo(response.body(), dest, startPosition, endPosition, (total, progress) -> {
//      log.info("{} [{} - {}]下载中：{}, totalLength: {}, total: {}, progress: {}, {}%, ..."
//          , index
//          , startPosition
//          , endPosition
//          , dest.getName()
//          , Utils.ofMB(totalLength, 2)
//          , Utils.ofMB(total, 2)
//          , progress
//          , Utils.fmt(((progress * 1.0) / totalLength * 100.0), "0.00")
//      );
//    });
  }

  @Test
  public void testAppUpgrade() throws IOException {
//    okhttp3.Response response = HttpHelper.get().get("http://192.168.1.47/api/app/version?appType=5");
    okhttp3.Response response = HttpHelper.get().get("http://free.sensecho.com/api/app/version?appType=8");
//    okhttp3.Response response = HttpHelper.get().get("http://192.168.1.47/api/app/download?id=");
    if (response.isSuccessful()) {
      log.info("headers ==>: \n{}", response.headers());
      String body = response.body().string();
      log.info(" ==>: {}", body);
      JSONObject json = JSON.parseObject(body);
      JSONObject data = json.getJSONObject("data");
      String zid = data.getString("zid");
      String apkName = data.getString("downloadAppame");
      /*String version = data.getString("version");
      String updateDesc = data.getString("updateDesc");
      Integer versionCode = data.getInteger("versionCode");*/
      HttpHelper.get().download("http://free.sensecho.com/api/app/download?id=" + zid
          , new File("D:/home/tmp/")
          , apkName
          , false
          , new FileProgressListener() {

            final AtomicInteger index = new AtomicInteger();
            final AtomicLong start = new AtomicLong();

            @Override
            public void onStart(Call call) {
              log.info("开始下载：{}", apkName);
              start.set(System.currentTimeMillis());
            }

            @Override
            public void onProgressChange(long totalLength, long progress, boolean done) {
              if (index.incrementAndGet() % 150 == 0 || done) {
                log.info("下载中：{}, {}, {}%, {} ...", apkName, totalLength, Utils.fmt((progress * 100.0) / totalLength, "0.00"), done);
              }
            }

            @Override
            public void onSuccess(Call call, @NotNull okhttp3.Response response, @Nullable File file) {
              log.info("下载成功：{}, {}", apkName, Utils.fmt(Utils.ofMB(file.length()), "0.00MB"));
            }

            @Override
            public void onFailure(Call call, @NotNull Exception e, @Nullable File file) {
              log.info("下载失败：{}, {}", apkName, e.getMessage());
            }

            @Override
            public void onFinish(Call call) {
              log.info("结束，耗时：{}", TimeUtils.diffNow(start.get()));
            }
          });


//      String filename = BodyUtils.getFilename(response);
//      log.info("filename: {}", filename);
//      filename = filename.endsWith(".apk") ? filename : filename + ".apk";
//      BodyUtils.progressResponseBody(response.body()
//          , new File("D:/" + filename)
//          , (totalLength, progress, done) -> log.info("totalLength: {}, progress: {}, done: {}", totalLength, progress, done)
//      );
    } else {
      log.info("error: " + response.message());
    }
  }

  @Test
  public void testDownloadApk() {
    long start = TimeUtils.now();
//    String url = "http://192.168.1.47/api/app/download?id=969f7a19751d4c0e91519e9eebfeb067";
    String url = "http://192.168.1.47/api/app/download?id=fad84b9e98814482b86398ee22ba8632";
    okhttp3.Response response = HttpHelper.get()
        .setLogLevel(HttpLoggingInterceptor.Level.BODY)
        .get(url);
    if (response.isSuccessful()) {
      log.info("headers ==>: \n{}", response.headers());
      String filename = BodyUtils.getFilename(response.headers(), IdUtils.uuid() + ".apk");
      log.info("filename: {}", filename);
      BodyUtils.progressResponseBody(response.body()
          , new File("D:/" + filename)
          , (totalLength, progress, done) -> {
            log.info("totalLength: {}, progress: {}, {}, done: {}"
                , totalLength, progress, Utils.fmt(progress * 1.0 / totalLength, ".0%"), done);
            if (done) {
              log.info("耗时: {}", TimeUtils.diffNow(start));
            }
          });
    } else {
      log.info("error: " + response.message());
    }
  }

  @Test
  public void test() {
    JSONArray array = JSON.parseArray("[{\"name\":\"湖南省\",\"code\":\"430000\"},{\"name\":\"长沙市\",\"code\":430100,\"lon\":\"112.94547319535288\",\"lat\":\"28.23488939994364\"}]");
    JSONObject province = array.getJSONObject(0);
    System.err.println("==>: " + province.containsValue("湖南省"));
    System.err.println(province.keySet());
    System.err.println(province.values());
  }

  public void tearDown() throws Exception {
  }


  interface ServiceApi {

    //    String BASE_URL = "https://dss0.bdstatic.com/";
//    String BASE_URL = "http://127.0.0.1:80/api/";
    String BASE_URL = "https://image.taoguba.com.cn/";

    @GET("5aV1bjqh_Q23odCf/static/superman/js/super_load-eb15f1e5a8.js")
    Observable<String> getJS();

    @GET("5aV1bjqh_Q23odCf/static/superman/js/super_load-eb15f1e5a8.js")
    Observable<ResponseBody> getBody();

    @SchedulerOn(subscribeOn = SchedulerOn.Type.IO, observeOn = SchedulerOn.Type.IO)
    @GET("img/2021/12/14/ew4nf5737jvn.jpg_760w.png")
    Observable<ResponseBody> getImg();


    @POST("simple/upload")
    Observable<String> upload(@Body RequestBody body);

    @GET("simple/download")
    Observable<Response<ResponseBody>> download(@Query("filename") String filename);

  }
}