package com.benefitj.http;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.BufferedReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * fastjson 解析
 */
public class FastJsonConverterFactory extends Converter.Factory {

  static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

  /**
   * Create an instance using a default {@link ObjectMapper} instance for conversion.
   */
  public static FastJsonConverterFactory create() {
    return new FastJsonConverterFactory();
  }

  private FastJsonConverterFactory() {
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                          Annotation[] annotations,
                                                          Retrofit retrofit) {
    return (value) -> {
      StringBuilder sb = new StringBuilder();
      try (final BufferedReader reader = new BufferedReader(value.charStream());) {
        String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      }
      return JSON.parseObject(sb.toString(), type);
    };
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                        Annotation[] parameterAnnotations,
                                                        Annotation[] methodAnnotations,
                                                        Retrofit retrofit) {
    return (value) -> RequestBody.create(MEDIA_TYPE, JSON.toJSONBytes(value));
  }
}

