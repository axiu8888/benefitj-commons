package com.benefitj.http;

import com.benefitj.core.IOUtils;
import com.benefitj.core.file.IWriter;
import com.benefitj.core.functions.IBiConsumer;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Body工具
 */
public class BodyUtils {

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
    MultipartBody.Builder builder = new MultipartBody.Builder();
    builder.setType(MultipartBody.FORM);
    parameters.forEach(builder::addFormDataPart);
    MediaType mediaType = MediaType.parse("application/octet-stream");
    for (File file : files) {
      builder.addFormDataPart(name, file.getName(), RequestBody.create(mediaType, file));
    }
    return new ProgressRequestBody(builder.build(), listener);
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
      throw new IllegalStateException(e);
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
    try (final IWriter writer = IWriter.newFileWriter(IOUtils.createFile(dest.getAbsolutePath()))) {
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
    if (!dest.exists()) {
      try {
        dest.getParentFile().mkdirs();
        dest.createNewFile();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    if (dest.isDirectory()) {
      throw new IllegalArgumentException("传入的File对象是文件夹，无法写入数据!");
    }
    try (final InputStream is = body.byteStream();
         final OutputStream fos = new FileOutputStream(dest);) {
      byte[] buf = new byte[1024 << 8];
      int len;
      while ((len = is.read(buf)) > 0) {
        fos.write(buf, 0, len);
      }
      fos.flush();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取 header 的值
   *
   * @param response 响应
   * @param name     header名称
   * @return 返回获取到的值或默认值
   */
  public static String getHeader(Response response, String name) {
    return getHeader(response, name, null);
  }

  /**
   * 获取 header 的值
   *
   * @param response     响应
   * @param name         header名称
   * @param defaultValue 默认值
   * @return 返回获取到的值或默认值
   */
  public static String getHeader(Response response, String name, String defaultValue) {
    return response != null ? response.header(name) : defaultValue;
  }

  /**
   * 获取 header 中子项的值，比如：Content-Disposition =>: attachment;filename=xxx.pdf
   *
   * @param response 响应
   * @param name     header名称
   * @param subName  子项的名称
   * @return 返回获取到的值或默认值
   */
  public static String getHeaderSub(Response response, String name, String subName) {
    return getHeaderSub(response, name, subName, null);
  }

  /**
   * 获取 header 中子项的值，比如：Content-Disposition =>: attachment;filename=xxx.pdf
   *
   * @param response     响应
   * @param name         header名称
   * @param subName      子项的名称
   * @param defaultValue 默认值
   * @return 返回获取到的值或默认值
   */
  public static String getHeaderSub(Response response, String name, String subName, String defaultValue) {
    String header = getHeader(response, name);
    if (StringUtils.isNotBlank(header)) {
      return Stream.of(header.split(";"))
          .filter(v -> v.startsWith(subName))
          .map(v -> v.replaceFirst((v.startsWith(subName + "=") ? subName : "") + "=", ""))
          .findFirst()
          .orElse(defaultValue);
    }
    return defaultValue;
  }

}
