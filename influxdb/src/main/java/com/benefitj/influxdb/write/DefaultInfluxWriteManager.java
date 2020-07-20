package com.benefitj.influxdb.write;

import com.benefitj.influxdb.file.FileWriterPair;
import com.benefitj.influxdb.file.LineFileFactory;
import com.benefitj.influxdb.file.LineFileListener;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的写入实现
 */
public class DefaultInfluxWriteManager implements InfluxWriteManager {

  public static final long MB = 1024 << 10;
  /**
   * executor
   */
  private volatile ExecutorService executor;
  /**
   * 缓存文件的引用
   */
  private final List<LineFileWriter> writers = new CopyOnWriteArrayList<>();
  /**
   * 配置
   */
  private InfluxDBWriteProperty property;
  /**
   * 写入分派器
   */
  private WriterDispatcher writerDispatcher = WriterDispatcher.newDispatcher();

  /**
   * 创建line文件的工厂
   */
  private LineFileFactory lineFileFactory = new DefaultLineFileFactory();
  /**
   * 处理line文件的监听
   */
  private LineFileListener lineFileListener = EMPTY_LINE_FILE_LISTENER;
  /**
   * 初始化状态
   */
  private volatile boolean initialized = false;
  /**
   * 线程的写入统计
   */
  private final ThreadLocal<AtomicInteger> writeCounter = ThreadLocal.withInitial(() -> new AtomicInteger(0));

  public DefaultInfluxWriteManager() {
  }

  public DefaultInfluxWriteManager(InfluxDBWriteProperty property) {
    this.property = property;
  }

  /**
   * 注册销毁时的回调钩子
   */
  protected void checkAndInit() {
    if (initialized) {
      return;
    }
    synchronized (this) {
      if(initialized) {
        return;
      }
      InfluxDBWriteProperty p = getProperty();
      int lineFileCount = p.getLineFileCount();
      for (int i = 0; i < lineFileCount; i++) {
        LineFileWriter writer = newWriter(p);
        getWriters().add(writer);
      }
      Runtime.getRuntime().addShutdownHook(new Thread(() -> getWriters().forEach(LineFileWriter::close)));
      initialized = true;
    }
  }

  protected LineFileWriter newWriter(InfluxDBWriteProperty property) {
    LineFileWriter writer = new LineFileWriter();
    writer.setDelay(property.getDelay() * 1000);
    writer.setMaxSize(property.getCacheSize() * MB);
    writer.setCacheDir(new File(property.getCacheDir()));
    writer.setLineFileFactory(getLineFileFactory());
    writer.setLineFileListener(getLineFileListener());
    return writer;
  }

  public void offer(Runnable r) {
    getExecutor().execute(r);
  }

  public List<LineFileWriter> getWriters() {
    return this.writers;
  }

  /**
   * 写入
   *
   * @param lines
   */
  protected void put0(List<String> lines) {
    if (isNotEmpty(lines)) {
      checkAndInit();
      List<LineFileWriter> writers = this.getWriters();
      LineFileWriter writer = getWriterDispatcher().dispatch(writers);
      final AtomicInteger counter = writeCounter.get();
      try {
        counter.incrementAndGet();
        writer.write(lines);
      } finally {
        counter.decrementAndGet();
      }
    }
  }

  /**
   * 异步保存
   *
   * @param lines 行协议数据
   */
  @Override
  public void putAsync(List<String> lines) {
    if (isNotEmpty(lines)) {
      offer(() -> put0(lines));
    }
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  @Override
  public void putSync(List<String> lines) {
    put0(lines);
  }

  /**
   * 立刻保存
   */
  @Override
  public void flushNow() {
    for (LineFileWriter writer : getWriters()) {
      checkFlush(writer, true);
    }
  }

  /**
   * 调度器
   */
  @Override
  public ExecutorService getExecutor() {
    ExecutorService e = this.executor;
    if (e == null) {
      synchronized (this) {
        e = this.executor;
        if (e == null) {
          InfluxDBWriteProperty property = getProperty();
          ThreadFactory factory = new DefaultThreadFactory("io-", "-influxdb-");
          e = Executors.newScheduledThreadPool(property.getThreadCount(), factory);
          this.executor = e;
        }
      }
    }
    return e;
  }

  /**
   * 检查是否可上传数据
   */
  @Override
  public void checkFlush() {
    for (LineFileWriter writer : getWriters()) {
      checkFlush(writer, false);
    }
  }

  /**
   * 检查并上传数据
   *
   * @param writer
   * @param force  是否强制上传
   */
  public void checkFlush(LineFileWriter writer, boolean force) {
    if (writer.isWritable(force)) {
      writer.refresh();
    }
  }

  @Override
  public int currentWriteCount() {
    AtomicInteger counter = this.writeCounter.get();
    return counter.get();
  }

  @Override
  public InfluxDBWriteProperty getProperty() {
    return property;
  }

  @Override
  public void setLineFileFactory(LineFileFactory factory) {
    this.lineFileFactory = factory;
  }

  @Override
  public LineFileFactory getLineFileFactory() {
    return this.lineFileFactory;
  }

  @Override
  public void setLineFileListener(LineFileListener listener) {
    this.lineFileListener = listener;
  }

  @Override
  public LineFileListener getLineFileListener() {
    return this.lineFileListener;
  }

  @Override
  public WriterDispatcher getWriterDispatcher() {
    return writerDispatcher;
  }

  @Override
  public void setWriterDispatcher(WriterDispatcher writerDispatcher) {
    this.writerDispatcher = writerDispatcher;
  }

  protected static boolean isNotEmpty(Collection<?> c) {
    return c != null && !c.isEmpty();
  }

  /**
   * The default thread factory
   */
  public static class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public DefaultThreadFactory() {
      this("pool-", "-thread-");
    }

    public DefaultThreadFactory(String prefix, String suffix) {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() :
          Thread.currentThread().getThreadGroup();
      namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r,
          namePrefix + threadNumber.getAndIncrement(), 0);
      if (t.isDaemon()) {
        t.setDaemon(false);
      }
      if (t.getPriority() != Thread.NORM_PRIORITY) {
        t.setPriority(Thread.NORM_PRIORITY);
      }
      return t;
    }
  }


  private static final LineFileListener EMPTY_LINE_FILE_LISTENER = new LineFileListener() {
    @Override
    public void onHandleLineFile(FileWriterPair pair, File file) {
      // ~
    }
  };

}
