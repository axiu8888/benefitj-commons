package com.benefitj.network;

import com.benefitj.core.IOUtils;
import com.benefitj.http.ApiBuilder;
import com.benefitj.http.BodyUtils;
import com.benefitj.http.SimpleObserver;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.File;


@Slf4j
class HttpTest {

  final Api api = Api.create();

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void test_che_algo() {
    File che = new File("D:/tmp/znsx/01000650-2021_03_22-12_20_17.CHE");
    api.realtime(BodyUtils.formBody(che, "file"))
        .subscribe(SimpleObserver.create(request -> {
          BodyUtils.transferTo(request.body(), IOUtils.createFile(che.getParentFile(), "test.json"));
        }));
  }


  interface Api {

    static Api create() {
      return ApiBuilder.createApiProxy(Api.class, BASE_URL, builder -> {
        builder
            .addInterceptors(new HttpLoggingInterceptor(log::info).setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .setGzipEnable(true)
        ;
      });
    }

    String BASE_URL = "http://192.168.1.198:480/api/";

    @POST("realtime")
    Observable<Response> realtime(@Body RequestBody body);

  }

}