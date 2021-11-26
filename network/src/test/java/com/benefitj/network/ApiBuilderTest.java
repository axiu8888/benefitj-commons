package com.benefitj.network;

import com.benefitj.core.Unit;
import com.benefitj.core.file.IWriter;
import io.reactivex.Observable;
import junit.framework.TestCase;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiBuilderTest extends TestCase {

  private Logger log = LoggerFactory.getLogger(getClass());

  private ServiceApi api;

  public void setUp() throws Exception {
    this.api = ApiBuilder.newBuilder(ServiceApi.class)
        .setBaseUrl(ServiceApi.BASE_URL)
        .addHttpLogging(HttpLoggingInterceptor.Level.NONE)
        .setUseDefault(true) // 启用默认的转换器和
        .build();
  }

  @Test
  public void testRequest() {
    api.getJS()
        .subscribe(js -> log.info("js: \n{}", js));

    // 写入文件
    api.getBody()
        .subscribe(body -> {
          File dest = new File("D:/tmp/super_load-eb15f1e5a8.js");
          BodyUtils.transferTo(body, dest);
        });
  }

  @Test
  public void testUploadFile() {
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
  }

  @Test
  public void testDownload() {
    // 下载
    api.download("simulator.zip")
        .subscribe(new SimpleObserver<ResponseBody>() {
          @Override
          public void onNext(@NotNull ResponseBody responseBody) {
            // 处理响应
            final AtomicInteger index = new AtomicInteger();
            IWriter writer = IWriter.newFileWriter(new File("D:/opt/tmp/simulator2.zip"));
            BodyUtils.process(responseBody
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
        });
  }


  public void tearDown() throws Exception {
  }


  interface ServiceApi {

    //String BASE_URL = "https://dss0.bdstatic.com/";
    String BASE_URL = "http://127.0.0.1:80/api/";

    @GET("5aV1bjqh_Q23odCf/static/superman/js/super_load-eb15f1e5a8.js")
    Observable<String> getJS();

    @GET("5aV1bjqh_Q23odCf/static/superman/js/super_load-eb15f1e5a8.js")
    Observable<ResponseBody> getBody();


    @POST("simple/upload")
    Observable<String> upload(@Body RequestBody body);

    @GET("simple/download")
    Observable<ResponseBody> download(@Query("filename") String filename);

  }
}