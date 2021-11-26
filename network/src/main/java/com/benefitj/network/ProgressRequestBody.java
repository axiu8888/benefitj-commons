package com.benefitj.network;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 具有进度监听的请求体
 */
public class ProgressRequestBody extends RequestBody {

  /**
   * 请求体
   */
  private final RequestBody rawBody;
  /**
   * 进度监听
   */
  private ProgressListener progressListener;

  private BufferedSink delegateSink;

  public ProgressRequestBody(RequestBody rawBody) {
    this.rawBody = rawBody;
  }

  public ProgressRequestBody(RequestBody rawBody, ProgressListener listener) {
    this.rawBody = rawBody;
    this.progressListener = listener;
  }

  @Override
  public long contentLength() throws IOException {
    return getRawBody().contentLength();
  }

  @Nullable
  @Override
  public MediaType contentType() {
    return getRawBody().contentType();
  }

  @Override
  public void writeTo(@NotNull BufferedSink sink) throws IOException {
    BufferedSink delegate = this.delegateSink;
    if (delegate == null) {
      delegate = (this.delegateSink = Okio.buffer(new ProgressForwardingSink(sink)));
    }
    getRawBody().writeTo(delegate);
    delegate.flush();
  }

  public RequestBody getRawBody() {
    return rawBody;
  }

  public ProgressListener getProgressListener() {
    return progressListener;
  }

  public ProgressRequestBody setProgressListener(ProgressListener progressListener) {
    this.progressListener = progressListener;
    return this;
  }

  class ProgressForwardingSink extends ForwardingSink {
    /**
     * 进度
     */
    private AtomicLong progress = new AtomicLong(0L);

    public ProgressForwardingSink(@NotNull Sink delegate) {
      super(delegate);
    }

    @Override
    public void write(@NotNull Buffer source, long byteCount) throws IOException {
      super.write(source, byteCount);
      progress.addAndGet(byteCount);
      long totalLength = contentLength();
      long currentProgress = progress.get();
      getProgressListener().onProgressChange(totalLength, currentProgress, totalLength == currentProgress);
    }
  }
}
