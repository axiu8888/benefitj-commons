package com.benefitj.influxdb.write;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.benefitj.influxdb.file.FileWriterPair;
import com.benefitj.influxdb.file.LineFileFactory;
import com.benefitj.influxdb.file.LineFileListener;
import com.benefitj.influxdb.file.LineFileSlicer;
import com.benefitj.influxdb.template.InfluxDBTemplate;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的写入实现
 */
public class SimpleInfluxWriteManager implements InfluxWriteManager, LineFileFactory, LineFileListener {

  private static final Logger logger = LoggerFactory.getLogger(InfluxWriteManager.class);

  public static final long MB = 1024 << 10;
  /**
   * 默认配置
   */
  private static final InfluxDBWriteProperty DEFAULT_PROPERTY;

  static {
    String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
    File lineFile = new File(tmpDir, "/influxdb/lines");
    InfluxDBWriteProperty p = new InfluxDBWriteProperty();
    p.setCacheDir(lineFile.getAbsolutePath());
    p.setCacheSize(50);
    p.setDelay(10);
    p.setLineFileCount(1);
    p.setThreadCount(4);
    DEFAULT_PROPERTY = p;
  }

  /**
   * executor
   */
  private volatile Executor executor;
  /**
   * 缓存文件的引用
   */
  private final List<LineFileWriter> writers = new CopyOnWriteArrayList<>();
  private final AtomicInteger writerDispatcher = new AtomicInteger(0);
  /**
   * InfluxDBTemplate
   */
  private InfluxDBTemplate template;
  /**
   * 配置
   */
  private InfluxDBWriteProperty property;

  public SimpleInfluxWriteManager(InfluxDBTemplate template,
                                  InfluxDBWriteProperty property) {
    this.template = template;
    this.property = property;

    init();
  }

  /**
   * 注册销毁时的回调钩子
   */
  protected void init() {
    InfluxDBWriteProperty p = getProperty();
    int lineFileCount = p.getLineFileCount();
    for (int i = 0; i < lineFileCount; i++) {
      LineFileWriter writer = newWriter(p);
      getWriters().add(writer);
    }
    Runtime.getRuntime().addShutdownHook(new Thread(() -> getWriters().forEach(LineFileWriter::close)));
  }

  protected LineFileWriter newWriter(InfluxDBWriteProperty property) {
    LineFileWriter writer = new LineFileWriter();
    writer.setDelay(property.getDelay() * 1000);
    writer.setMaxSize(property.getCacheSize() * MB);
    writer.setCacheDir(new File(property.getCacheDir()));
    writer.setLineFileFactory(this);
    writer.setLineFileListener(this);
    return writer;
  }

  protected void offer(Runnable r) {
    getExecutor().execute(r);
  }

  protected List<LineFileWriter> getWriters() {
    return this.writers;
  }

  @Override
  public FileWriterPair create(File dir) {
    return LineFileFactory.newFile(dir);
  }

  @Override
  public void onHandleLineFile(FileWriterPair pair, File file) {
    try {
      if (file.length() > 0) {
        getTemplate().write(file);
      }
    } finally {
      file.delete();
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
      offer(() -> putSync(lines));
    }
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  @Override
  public void putSync(List<String> lines) {
    if (isNotEmpty(lines)) {
      List<LineFileWriter> writers = this.getWriters();
      int index = writerDispatcher.getAndIncrement();
      try {
        LineFileWriter writer = writers.get(index % writers.size());
        writer.write(lines);
      } finally {
        if (index > writers.size()) {
          writerDispatcher.compareAndSet(index, 0);
        }
      }
    }
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
  public Executor getExecutor() {
    Executor e = this.executor;
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
  public InfluxDBTemplate getTemplate() {
    return template;
  }

  @Override
  public InfluxDBWriteProperty getProperty() {
    return property;
  }

  public static class LineFileWriter extends LineFileSlicer {
    /**
     * 延迟上传的时间
     */
    private long delay;
    /**
     * 初始化时间
     */
    private long initializedTime = now();

    public LineFileWriter() {
    }

    public LineFileWriter(File cacheDir, long maxSize) {
      super(cacheDir, maxSize);
    }

    public LineFileWriter(File cacheDir, long maxSize, long delay) {
      super(cacheDir, maxSize);
      this.delay = delay;
    }

    public String getName() {
      return getPair(true).getName();
    }

    public long getInitializedTime() {
      return initializedTime;
    }

    public boolean isWritable(boolean force) {
      long length = length();
      if (length <= 0) {
        return false;
      }
      if (force) {
        return true;
      }
      if (length >= getMaxSize()) {
        return true;
      }
      long now = now();
      long delay = getDelay();
      return (now - getInitializedTime() >= delay) || (now - getLastWriteTime() >= delay);
    }

    public long getDelay() {
      return delay;
    }

    public void setDelay(long delay) {
      this.delay = delay;
    }

    public void close() {
      FileWriterPair pair = getPair();
      if (pair != null) {
        pair.close();
      }
    }
  }

  protected static boolean isNotEmpty(Collection<?> c) {
    return c != null && !c.isEmpty();
  }

  protected static long now() {
    return System.currentTimeMillis();
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

}
