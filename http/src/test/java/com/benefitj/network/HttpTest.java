package com.benefitj.network;

import com.benefitj.core.IOUtils;
import com.benefitj.http.ApiBuilder;
import com.benefitj.http.BodyUtils;
import com.benefitj.http.HttpHelper;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;


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

  @Test
  void test_influxdb() throws IOException {
    String url = "http://pr.sensecho.com:58086/query?u=admin&p=hsrg8888&db=hsrg&q=SELECT first(ecg_points) AS first FROM \"hs_wave_package\" WHERE time >= '2024-04-24T16:00:00.000Z' AND time <= '2024-04-25T16:00:00.000Z' AND patient_id = '23dfdebf4302482fa4f75c38046eb4d9' AND (ecg_conn_state = 0 OR ecg_lead_state = 0 OR resp_conn_state = 0) ORDER BY time ASC LIMIT 1";
    Response response = HttpHelper.get().post(url);
      log.info("请求 ==> code: {}, msg: {}", response.code(), response.message());
    if (response.isSuccessful()) {
      String content = response.body().string();
      log.info("请求内容 ==>: \n\n{}\n", content);
    }
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