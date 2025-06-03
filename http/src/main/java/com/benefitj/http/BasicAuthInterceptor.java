package com.benefitj.http;


import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 认证
 */
public class BasicAuthInterceptor implements Interceptor {

  final String headerName;
  final String username;
  final String password;

  final String token;

  public BasicAuthInterceptor(String username, String password) {
    this("Authorization", username, password);
  }

  public BasicAuthInterceptor(String headerName, String username, String password) {
    this.headerName = headerName;
    this.username = username;
    this.password = password;
    this.token = Credentials.basic(username, password);
  }

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request newRequest = chain.request()
        .newBuilder()
        .header(headerName, token)
        .build();
    return chain.proceed(newRequest);
  }

//  public static String basicToken(String username, String password) {
//    byte[] bytes = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
//    return "Basic " + Base64.getEncoder().encodeToString(bytes);
//  }

}

