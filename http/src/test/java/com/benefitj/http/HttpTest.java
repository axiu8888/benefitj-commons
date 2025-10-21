package com.benefitj.http;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.*;
import com.benefitj.core.file.IWriter;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


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
  void test_che_upload() throws IOException {
    File zip = new File("E:\\.tmp\\cache\\CHE\\11001310-2025_02_11-23_56_17.zip");
    String url = "http://192.168.1.198/support/api/che/upload?patientId=21ba42c5345b44dc9d3d3e0edd132ce5";
    Map<String, String> headers = new HashMap<String, String>(){{
      put("x-access-token", "eyJhbGciOiJIUzUxMiJ9.eyJvcmdJZCI6IjhhYzE4ZGMwMTZmODQ2NGI4ZGZiMjIzZWQ4YTljZmFkIiwicm9vdE9yZ0lkIjoiZjYyMmNlN2M3YWEwNGY2Mjk2OWVhZWQ5MTViMGRkMGMiLCJpc0FkbWluIjpmYWxzZSwianRpIjoiMzRhNzkwZDM4MzZiNDUxMjg1ZTM2MzY2ZTZmMGYxNDEiLCJzdWIiOiI0NTZlNmMxNWUyNGE1NjcwYmUxMmZkMjA3YTkxOTU3NSIsImlzcyI6ImhzcmciLCJpYXQiOjE3NTU0OTkxMjUsImV4cCI6MTc1NjEwMzkyNX0.sLQYQg_8pttQQt6ywL_K0wbZdnkCX8kzaTrybxUWq00fE0fPjW9vP10eBoqR2tyEaOcSH7yQ9h05VLZc-eft9w");
    }};
    Response response = HttpHelper.get().post(url, headers, zip, "ches");
    if(response.isSuccessful()) {
      log.info("code: {}, msg: {}", response.code(), response.body().string());
    } else {
      log.warn("code: {}, error: {}", response.code(), response.message());
    }
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
    String url = "ws://192.168.9.104:8089";
//    String url = "ws://192.168.1.198/api/management/socket";
//    File destFile = IOUtils.createFile("D:/cache/.tmp/瑞超小肺/走-呼吸.txt");
//    File destFile = IOUtils.createFile("D:/cache/.tmp/瑞超小肺/深-呼吸.txt");
    File destFile = IOUtils.createFile("D:/cache/.tmp/瑞超小肺/平静-呼吸__"+ DateFmtter.fmtNow("yyyyMMdd_HHmm") +".txt");
    AtomicReference<IWriter> writerRef = new AtomicReference<>(IWriter.create(destFile, false));
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
        json.put("timestamp", DateFmtter.now());
        String out = json.toJSONString();
        System.err.println(out);
        writerRef.get().writeAndFlush(out).writeAndFlush("\n");

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

    for(;;) {
      if (!socket.isOpen()) {
        socket.reconnect();
      }
      EventLoop.sleepSecond(10);
    }

  }


  @Test
  void test_ruichao22() {
    File lp = new File("D:\\code\\company\\python\\report-export\\report\\tmp\\smwt__3fd8897833d9461eb3c39865d15c0312");
    File txt = new File("D:\\tmp\\cache\\瑞超小肺\\平静-呼吸.txt");
    IWriter writer = IWriter.create(IOUtils.createFile(txt.getParentFile(), "new__" + txt.getName()), false);
    LinkedList<JSONObject> buf = new LinkedList<>();
    AtomicLong startAt = new AtomicLong(-1);
    IOUtils.readLines(txt, (line, index) -> {
      JSONObject lineJson = JSON.parseObject(line);
      if (lineJson.getIntValue("Type") != 1058) return;
      buf.add(lineJson);
      if (buf.size() >= 200) {
        if (startAt.get() <= 0) startAt.set(DateFmtter.parseToLong(buf.get(0).getString("date")));
        try {
          JSONObject json = new JSONObject();
          json.put("time", startAt.getAndAdd(1000));
          json.put("Index", buf.stream().map(j -> j.getJSONObject("Data")).mapToDouble(j -> j.getDouble("Index")).mapToInt(v -> (int)v).toArray());
          json.put("Volume", buf.stream().map(j -> j.getJSONObject("Data")).mapToDouble(j -> j.getDouble("Volume")).toArray());
          json.put("Flow", buf.stream().map(j -> j.getJSONObject("Data")).mapToDouble(j -> j.getDouble("Flow")).toArray());
          buf.clear();
          writer.writeAndFlush(JSON.toJSONBytes(json)).writeAndFlush("\n");
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
      }
    });
    writer.close();
  }



  @Test
  void test_managementWS() {
    String url = "ws://192.168.1.194/api/management/socket";
    WebSocket socket = HttpClient.newWebSocket(url, new WebSocketListener() {

      @Override
      public void onOpen(WebSocket socket, Response response) {
        log.info("onOpen...");

        socket.send("{\n" +
            "    \"op\": \"regist\",\n" +
            "    \"wardCode\": \"1\"\n" +
            "}");
      }

      @Override
      public void onMessage(WebSocket socket, String text) {
        log.info("onMessage ==>: {}", text);
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

    for(;;) {
      if (!socket.isOpen()) {
        socket.reconnect();
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