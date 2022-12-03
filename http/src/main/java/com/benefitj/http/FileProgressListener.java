package com.benefitj.http;


import okhttp3.Call;
import okhttp3.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * 下载进度监听
 */
public interface FileProgressListener extends ProgressListener {

  /**
   * 开始
   *
   * @param call 请求
   */
  default void onStart(Call call) {
  }

  /**
   * 成功
   *
   * @param call     请求
   * @param response 响应
   * @param file     文件
   */
  default void onSuccess(Call call, @Nonnull Response response, @Nullable File file) {
  }

  /**
   * 上传进度
   *
   * @param totalLength 总长度
   * @param progress    读取或写入的长度
   * @param done        是否完成
   */
  @Override
  default void onProgressChange(long totalLength, long progress, boolean done) {
  }

  /**
   * 失败
   *
   * @param call 请求
   * @param e    异常
   * @param file 文件
   */
  default void onFailure(Call call, @Nonnull Exception e, @Nullable File file) {
  }

  /**
   * 结束
   *
   * @param call 请求
   */
  default void onFinish(Call call) {
  }

}
