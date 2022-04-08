package com.benefitj.http;


/**
 * 进度监听
 */
public interface ProgressListener {

  /**
   * 进度改变
   *
   * @param totalLength 总长度
   * @param progress    读取或写入的长度
   * @param done        是否完成
   */
  void onProgressChange(long totalLength, long progress, boolean done);

}
