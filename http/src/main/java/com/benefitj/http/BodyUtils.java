package com.benefitj.http;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.file.IWriter;
import com.benefitj.core.functions.IBiConsumer;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Body工具
 */
public class BodyUtils {

  public static final MediaType MEDIA_OCTET_STREAM = MediaType.parse("application/octet-stream");
  public static final MediaType MEDIA_JSON = MediaType.parse("application/json;charset=UTF-8");
  public static final MediaType MEDIA_FORM_DATA = MediaType.parse("multipart/form-data;charset=UTF-8");
  public static final MediaType MEDIA_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");


  /**
   * 判断是否为相同的媒体类型
   *
   * @param require     要求的媒体类型
   * @param contentType 检查的媒体类型
   * @return 返回判断结果
   */
  public static boolean isMediaType(MediaType require, String contentType) {
    return isMediaType(require, MediaType.get(contentType));
  }

  /**
   * 判断是否为相同的媒体类型
   *
   * @param require     要求的媒体类型
   * @param contentType 检查的媒体类型
   * @return 返回判断结果
   */
  public static boolean isMediaType(MediaType require, MediaType contentType) {
    return contentType.type().equalsIgnoreCase(require.type())
        && contentType.subtype().equalsIgnoreCase(require.subtype());
  }

  /**
   * 创建JSON的请求体
   *
   * @param json JSON数据
   * @return 返回请求体
   */
  public static RequestBody jsonBody(String json) {
    return jsonBody(json.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 创建JSON的请求体
   *
   * @param json JSON数据
   * @return 返回请求体
   */
  public static RequestBody jsonBody(byte[] json) {
    return RequestBody.create(json, MEDIA_JSON);
  }

  /**
   * 表单请求体
   *
   * @param file 文件
   * @param name 文件对应的参数名
   * @return 返回请求体
   */
  public static MultipartBody formBody(File file, String name) {
    return formBody(Collections.emptyMap(), new File[]{file}, name);
  }

  /**
   * 表单请求体
   *
   * @param parameters 参数
   * @param file       文件
   * @param name       文件对应的参数名
   * @return 返回请求体
   */
  public static MultipartBody formBody(Map<String, String> parameters, File file, String name) {
    return formBody(parameters, new File[]{file}, name);
  }

  /**
   * 表单请求体
   *
   * @param parameters 参数
   * @param files      文件
   * @param name       文件对应的参数名
   * @return 返回请求体
   */
  public static MultipartBody formBody(Map<String, String> parameters, File[] files, String name) {
    return formBodyBuilder(parameters, files, name).build();
  }

  /**
   * 表单请求体
   *
   * @param file 文件
   * @param name 文件对应的参数名
   * @return 返回请求体
   */
  public static MultipartBody.Builder formBodyBuilder(File file, String name) {
    return formBodyBuilder(new File[]{file}, name);
  }

  /**
   * 表单请求体
   *
   * @param files 文件
   * @param name  文件对应的参数名
   * @return 返回请求体
   */
  public static MultipartBody.Builder formBodyBuilder(File[] files, String name) {
    return formBodyBuilder(Collections.emptyMap(), files, name);
  }

  /**
   * 表单请求体
   *
   * @param parameters 参数
   * @return 返回请求体
   */
  public static MultipartBody.Builder formBodyBuilder(Map<String, String> parameters) {
    return formBodyBuilder(parameters, new File[0], "");
  }

  /**
   * 表单请求体
   *
   * @param parameters 参数
   * @param files      文件
   * @param name       文件对应的参数名
   * @return 返回请求体
   */
  public static MultipartBody.Builder formBodyBuilder(Map<String, String> parameters, File[] files, String name) {
    MultipartBody.Builder builder = new MultipartBody.Builder();
    builder.setType(MultipartBody.FORM);
    parameters.forEach(builder::addFormDataPart);
    for (File file : files) {
      builder.addFormDataPart(name, file.getName(), RequestBody.create(file, MEDIA_OCTET_STREAM));
    }
    return builder;
  }

  /**
   * 具有进度的请求体
   *
   * @param file     文件
   * @param name     请求参数名称
   * @param listener 监听
   * @return 返回请求体
   */
  public static RequestBody progressRequestBody(File file, String name, ProgressListener listener) {
    return progressRequestBody(Collections.emptyMap(), file, name, listener);
  }

  /**
   * 具有进度的请求体
   *
   * @param parameters 参数
   * @param file       文件
   * @param name       请求参数名称
   * @param listener   监听
   * @return 返回请求体
   */
  public static RequestBody progressRequestBody(Map<String, String> parameters, File file, String name, ProgressListener listener) {
    return progressRequestBody(parameters, new File[]{file}, name, listener);
  }

  /**
   * 具有进度的请求体
   *
   * @param parameters 参数
   * @param files      文件
   * @param name       请求参数名称
   * @param listener   监听
   * @return 返回请求体
   */
  public static RequestBody progressRequestBody(Map<String, String> parameters, File[] files, String name, ProgressListener listener) {
    return new ProgressRequestBody(formBody(parameters, files, name), listener);
  }

  /**
   * 处理响应体
   *
   * @param body     响应体
   * @param consumer 处理数据
   * @param listener 进度监听
   */
  public static void progressResponseBody(ResponseBody body, IBiConsumer<byte[], Integer> consumer, ProgressListener listener) {
    ProgressResponseBody prb = new ProgressResponseBody(body, listener);
    try (final InputStream is = prb.byteStream();) {
      byte[] buf = new byte[1024 << 8];
      int len;
      while ((len = is.read(buf)) > 0) {
        consumer.accept(buf, len);
      }
    } catch (Exception e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 处理响应体
   *
   * @param body     响应体
   * @param dest     目标文件
   * @param listener 进度监听
   */
  public static void progressResponseBody(ResponseBody body, File dest, ProgressListener listener) {
    try (final IWriter writer = IWriter.createWriter(IOUtils.createFile(dest), false)) {
      progressResponseBody(body, (buf, len) -> writer.write(buf, 0, len), listener);
    }
  }

  /**
   * 将响应体写入到文件中
   *
   * @param body 响应体
   * @param dest 目标文件
   */
  public static void transferTo(ResponseBody body, File dest) {
    transferTo(body, dest, (total, progress) -> {/* ignore */});
  }

  /**
   * 将响应体写入到文件中
   *
   * @param body 响应体
   * @param dest 目标文件
   */
  public static void transferTo(ResponseBody body, File dest, BiConsumer<Long, Long> progressConsumer) {
    transferTo(body, dest, 0, -1, progressConsumer);
  }

  /**
   * 将响应体写入到文件中
   *
   * @param body 响应体
   * @param dest 目标文件
   */
  public static void transferTo(ResponseBody body, File dest, long startPos, long endPos, BiConsumer<Long, Long> progressConsumer) {
    if (!dest.exists()) {
      throw new IllegalStateException("文件不存在: " + dest.getAbsolutePath());
    }
    if (dest.isDirectory()) {
      throw new IllegalArgumentException("传入的File对象是文件夹，无法写入数据!");
    }
    try (final InputStream in = body.byteStream();
         final RandomAccessFile rw = new RandomAccessFile(dest, "rw");) {
      long contentLength = body.contentLength();
      long total = contentLength > 0 ? contentLength : (Math.max(endPos, startPos) - startPos);
      AtomicLong progress = new AtomicLong();
      rw.seek(startPos);
      byte[] buf = new byte[1024 << 10];
      int len;
      while ((len = in.read(buf)) > 0) {
        progress.addAndGet(len);
        rw.write(buf, 0, len);
        progressConsumer.accept(total, progress.get());
      }
      progressConsumer.accept(total, progress.get());
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 获取 header 的值
   *
   * @param headers 响应
   * @param name    header名称
   * @return 返回获取到的值或默认值
   */
  public static String getHeader(Headers headers, String name) {
    return getHeader(headers, name, null);
  }

  /**
   * 获取 header 的值
   *
   * @param headers      HTTP请求或响应头
   * @param name         header名称
   * @param defaultValue 默认值
   * @return 返回获取到的值或默认值
   */
  public static String getHeader(Headers headers, String name, String defaultValue) {
    return headers != null ? headers.get(name) : defaultValue;
  }

  /**
   * 获取 header 中子项的值，比如：Content-Disposition =>: attachment;filename=xxx.pdf
   *
   * @param headers HTTP请求或响应头
   * @param name    header名称
   * @param subName 子项的名称
   * @return 返回获取到的值或默认值
   */
  public static String getHeaderSub(Headers headers, String name, String subName) {
    return getHeaderSub(headers, name, subName, null);
  }

  /**
   * 获取 header 中子项的值，比如：Content-Disposition =>: attachment;filename=xxx.pdf
   *
   * @param headers      HTTP请求或响应头
   * @param name         header名称
   * @param subName      子项的名称
   * @param defaultValue 默认值
   * @return 返回获取到的值或默认值
   */
  public static String getHeaderSub(Headers headers, String name, String subName, String defaultValue) {
    String header = getHeader(headers, name);
    if (StringUtils.isNotBlank(header)) {
      return Stream.of(header.split(";"))
          .map(String::trim)
          .filter(v -> v.startsWith(subName))
          .map(v -> v.replaceFirst((v.startsWith(subName + "=") ? subName : "") + "=", ""))
          .findFirst()
          .orElse(defaultValue);
    }
    return defaultValue;
  }

  /**
   * 获取Content-Type值
   *
   * @param headers HTTP请求或响应头
   * @return 返回Content-Type
   */
  public static String getContentType(Headers headers) {
    return getHeader(headers, "Content-Type");
  }

  /**
   * 获取Content-Length值
   *
   * @param headers HTTP请求或响应头
   * @return 返回Content-Length
   */
  public static long getContentLength(Headers headers) {
    String length = getHeader(headers, "Content-Length");
    return StringUtils.isNotBlank(length) ? Long.parseLong(length) : 0L;
  }

  /**
   * 获取文件名
   *
   * @param headers HTTP请求或响应头
   * @return 返回文件名
   */
  public static String getFilename(Headers headers) {
    return getFilename(headers, null);
  }

  /**
   * 获取文件名
   *
   * @param headers      HTTP请求或响应头
   * @param defaultValue 默认文件名
   * @return 返回文件名
   */
  public static String getFilename(Headers headers, String defaultValue) {
    return getHeaderSub(headers, "Content-Disposition", "filename", defaultValue);
  }

}
