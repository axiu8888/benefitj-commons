package com.benefitj.network;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.*;
import com.benefitj.http.*;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.File;
import java.io.IOException;


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

  @Test
  void test_ruicaho() {
//    String url = "ws://192.168.0.33:8089";
    String url = "ws://192.168.9.105:8089";
//    String url = "ws://192.168.1.198/api/management/socket";
//    AtomicReference<IWriter> writerRef = new AtomicReference<>(IWriter.createWriter(IOUtils.createFile("D:/tmp/cache/走-呼吸.txt"), false));

    WebSocket socket = HttpClient.newWebSocket(url, new WebSocketListener() {

      @Override
      public void onOpen(WebSocket socket, Response response) {
        log.info("onOpen...");
      }

      @Override
      public void onMessage(WebSocket socket, String text) {
//        System.err.println(text);
//        log.info("onMessage ==>: ", text);
        JSONObject json = JSON.parseObject(text);
        json.put("date", DateFmtter.fmtNowS());
        String out = json.toJSONString();
        System.err.println(out);
//        writerRef.get()
//            .writeAndFlush(out)
//            .writeAndFlush("\n");

        // 实时数据
        //{"Type":"1058","Data":{"Index":4533.0,"Time":22.669999999999774,"Volume":2.0729549778763063,"Flow":-0.031567169205410125}}

//        String type = json.getString("Type");
//        switch (type) {
//          case "102": // 病人导入CPF
//            break;
//          case "1058": // 实时数据
//            break;
//          case "1059": // 结果数据
//            break;
//        }

      }

      @Override
      public void onMessage(WebSocket socket, ByteString bytes) {
        log.info("onMessage, {}", HexUtils.bytesToHex(bytes.toByteArray()));
      }

      @Override
      public void onClosing(WebSocket socket, int code, String reason) {
        log.info("onClosing, code: {}, reason: {}", code, reason);
      }

      @Override
      public void onClosed(WebSocket socket, int code, String reason) {
        log.info("onClosed, code: {}, reason: {}", code, reason);
      }

      @Override
      public void onFailure(WebSocket socket, Throwable error, Response response) {
        log.info("onFailure, error: {}", error.getMessage());
        error.printStackTrace();
      }
    }, true, 5);
    EventLoop.sleepSecond(3);

    long startAt = TimeUtils.now();
    for(;;) {
//      if (!socket.isOpen()) {
//        socket.reconnect();
//      }
      if (TimeUtils.diffNow(startAt) > 30_000) {
        if (!socket.isClosed()) {
          socket.close();
          System.err.println("关闭...");
        }
//        break;
      }
      EventLoop.sleepSecond(10);
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