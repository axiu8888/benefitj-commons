package com.benefitj.influxdb.write;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 写入文件的分排器
 */
public interface WriterDispatcher {

  /**
   * 分派
   */
  LineFileWriter dispatch(List<LineFileWriter> writers);


  /**
   * 轮训
   */
  static WriterDispatcher newRoundWriterDispatcher() {
    return new RoundWriterDispatcher();
  }

  /**
   * 本地线程的软引用缓存
   */
  static WriterDispatcher newLocalSoftReferenceWriterDispatcher() {
    return new LocalSoftReferenceWriterDispatcher();
  }

  /**
   * 轮训
   */
  class RoundWriterDispatcher implements WriterDispatcher {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public LineFileWriter dispatch(List<LineFileWriter> writers) {
      int index = counter.getAndIncrement();
      if (index >= writers.size()) {
        counter.compareAndSet(index, 0);
      }
      return writers.get(index % writers.size());
    }

  }

  /**
   * 本地线程的软引用缓存
   */
  class LocalSoftReferenceWriterDispatcher implements WriterDispatcher {

    private final ThreadLocal<SoftReference<LineFileWriter>> writerLocal = ThreadLocal.withInitial(() -> new SoftReference<>(null));
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public LineFileWriter dispatch(List<LineFileWriter> writers) {
      LineFileWriter writer = writerLocal.get().get();
      if (writer == null) {
        int index = counter.getAndIncrement();
        writer = writers.get(index % writers.size());
        writerLocal.set(new SoftReference<>(writer));
      }
      return writer;
    }


  }

}
