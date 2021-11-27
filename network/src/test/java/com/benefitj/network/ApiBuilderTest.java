package com.benefitj.network;

import com.benefitj.core.EventLoop;
import com.benefitj.core.HexUtils;
import com.benefitj.core.Unit;
import com.benefitj.core.file.IWriter;
import io.reactivex.Observable;
import junit.framework.TestCase;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiBuilderTest extends TestCase {

  private Logger log = LoggerFactory.getLogger(getClass());

  private ServiceApi api;

  public void setUp() throws Exception {
    this.api = ApiBuilder.newBuilder(ServiceApi.class)
        .setBaseUrl(ServiceApi.BASE_URL)
        .addHttpLogging(HttpLoggingInterceptor.Level.NONE)
        .setUseDefault(true) // 启用默认的转换器和适配器
        .build();
  }

  @Test
  public void testRequest() {
    api.getJS()
        .subscribe(js -> log.info("js: \n{}", js));

//    // 写入文件
//    api.getBody()
//        .subscribe(body -> BodyUtils.transferTo(body, new File("D:/opt/tmp/super_load-eb15f1e5a8.js")));
  }

  @Test
  public void testUploadFile() {
    long start = Unit.now();
    File file = new File("D:/develop/tools/simulator.zip");
    final AtomicInteger index = new AtomicInteger();
    api.upload(BodyUtils.progressRequestBody(file, "files", (totalLength, progress, done) -> {
          if (index.incrementAndGet() % 50 == 0 || done) {
            log.info("总长度: {}, 已上传: {}, 进度: {}%， done[{}]"
                , totalLength
                , progress
                , Unit.fmt((progress * 100.f) / totalLength, "0.00")
                , done
            );
          }
        }))
        .subscribe(new SimpleObserver<String>() {
          @Override
          public void onNext(@NotNull String result) {
            log.info("上传结果: {}", result);
          }
        });
    log.info("耗时: {}", Unit.diffNow(start));
  }

  @Test
  public void testDownload() {
    // 下载
    long start = Unit.now();
    api.download("simulator.zip")
        .subscribe(new SimpleObserver<Response<ResponseBody>>() {
          @Override
          public void onNext(@NotNull Response<ResponseBody> response) {
            if (!response.isSuccessful()) {
              log.info("请求失败, {}, {}", response.code(), response.message());
              return;
            }
            // 处理响应
            final AtomicInteger index = new AtomicInteger();
            IWriter writer = IWriter.newFileWriter(new File("D:/opt/tmp/simulator2.zip"));
            BodyUtils.progressResponseBody(response.body()
                , (buf, len) -> {
                  // 写入道文件中
                  writer.write(buf, 0, len).flush();
                }, (totalLength, progress, done) -> {
                  if (index.incrementAndGet() % 50 == 0 || done) {
                    log.info("总长度: {}, 已下载: {}, 进度: {}%， done[{}]"
                        , totalLength
                        , progress
                        , Unit.fmt((progress * 100.f) / totalLength, "0.00")
                        , done
                    );
                  }
                  if (done) {
                    writer.close();
                  }
                });
          }

          @Override
          public void onError(Throwable e) {
            log.info("错误: {}", e.getMessage());
            super.onError(e);
          }
        });
    log.info("耗时: {}", Unit.diffNow(start));
  }

  @Test
  public void testWebSocket() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);

    String url = "ws://127.0.0.1:80/api/sockets/simple";
    WebSocket socket = HttpClientHolder.newWebSocket(url, new WebSocketListener() {
      @Override
      public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
        latch.countDown();
        log.info("onOpen, code: {}", response.code());
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
        log.info("onFailure, error: {}", e.getMessage());
        latch.countDown();
      }

      @Override
      public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("onClosing, code: {}, reason: {}", code, reason);
      }

      @Override
      public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("onClosed, code: {}, reason: {}", code, reason);
        latch.countDown();
      }
    });

    latch.await();
    for (int i = 0; i < 20; i++) {
      socket.send("from okhttp websocket: " + i);
      EventLoop.sleepSecond(1);
    }
    socket.close(1000, "done");

  }

  public void tearDown() throws Exception {
  }


  interface ServiceApi {

    String BASE_URL = "https://dss0.bdstatic.com/";
//    String BASE_URL = "http://127.0.0.1:80/api/";

    @GET("5aV1bjqh_Q23odCf/static/superman/js/super_load-eb15f1e5a8.js")
    Observable<String> getJS();

    @GET("5aV1bjqh_Q23odCf/static/superman/js/super_load-eb15f1e5a8.js")
    Observable<ResponseBody> getBody();


    @POST("simple/upload")
    Observable<String> upload(@Body RequestBody body);

    @GET("simple/download")
    Observable<Response<ResponseBody>> download(@Query("filename") String filename);

  }
}