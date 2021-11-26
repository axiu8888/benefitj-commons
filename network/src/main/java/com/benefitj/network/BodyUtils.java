package com.benefitj.network;

import com.benefitj.core.functions.IBiConsumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.*;

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
    return progressRequestBody(new File[]{file}, name, listener);
  }

  /**
   * 具有进度的请求体
   *
   * @param files    文件
   * @param name     请求参数名称
   * @param listener 监听
   * @return 返回请求体
   */
  public static RequestBody progressRequestBody(File[] files, String name, ProgressListener listener) {
    MultipartBody.Builder builder = new MultipartBody.Builder();
    builder.setType(MultipartBody.FORM);
    for (File file : files) {
      MediaType mediaType = MediaType.parse("application/octet-stream");
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
  public static void process(ResponseBody body, IBiConsumer<byte[], Integer> consumer, ProgressListener listener) {
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

}
