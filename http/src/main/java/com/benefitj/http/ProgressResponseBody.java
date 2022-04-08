package com.benefitj.http;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 具有进度监听的响应体
 */
public class ProgressResponseBody extends ResponseBody {
  /**
   * 实际的待包装响应体
   */
  private final ResponseBody rawBody;
  /**
   * 进度回调
   */
  private ProgressListener progressListener;
  /**
   * 包装完成的BufferedSource
   */
  private BufferedSource delegate;

  public ProgressResponseBody(ResponseBody rawBody) {
    this.rawBody = rawBody;
  }

  public ProgressResponseBody(ResponseBody rawBody, ProgressListener progressListener) {
    this.rawBody = rawBody;
    this.progressListener = progressListener;
  }

  /**
   * 重写调用实际的响应体的contentType
   *
   * @return MediaType
   */
  @Override
  public MediaType contentType() {
    return getRawBody().contentType();
  }

  /**
   * 重写调用实际的响应体的contentLength
   *
   * @return contentLength
   */
  @Override
  public long contentLength() {
    return getRawBody().contentLength();
  }

  @Override
  public BufferedSource source() {
    BufferedSource delegate = this.delegate;
    if (delegate == null) {
      delegate = (this.delegate = Okio.buffer(new ProgressForwardingSource(getRawBody().source())));
    }
    return delegate;
  }

  public ResponseBody getRawBody() {
    return rawBody;
  }

  public ProgressListener getProgressListener() {
    return progressListener;
  }

  public ProgressResponseBody setProgressListener(ProgressListener progressListener) {
    this.progressListener = progressListener;
    return this;
  }

  class ProgressForwardingSource extends ForwardingSource {
    /**
     * 进度
     */
    private AtomicLong progress = new AtomicLong(0L);

    public ProgressForwardingSource(@NotNull Source delegate) {
      super(delegate);
    }

    @Override
    public long read(Buffer sink, long byteCount) throws IOException {
      long len = super.read(sink, byteCount);
      progress.addAndGet(len > 0 ? len : 0);
      getProgressListener().onProgressChange(contentLength(), progress.get(), len == -1);
      return len;
    }
  }

}
