package com.benefitj.netty.adapter;

import com.benefitj.netty.log.INettyLogger;
import com.benefitj.netty.log.NettyLogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 发送UDP数据
 */
public class UdpSender implements Runnable {

  private static final INettyLogger logger = NettyLogger.INSTANCE;

  private static final Map<Runnable, CountDownLatch> AWAITS = new ConcurrentHashMap<>();
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

  /**
   * 通道
   */
  private Channel channel;
  /**
   * 待发送的消息队列
   */
  private final BlockingQueue<Consumer<Channel>> taskQueue = new ArrayBlockingQueue<>(Integer.MAX_VALUE / 100);
  /**
   * 执行状态
   */
  private final AtomicBoolean runState = new AtomicBoolean(false);
  /**
   * executor
   */
  private Executor executor;
  /**
   * thread
   */
  private final Thread udpSendLooper = new Thread(this, "UdpSendLooper-" + ID_GENERATOR.getAndIncrement());

  public UdpSender() {
  }

  public UdpSender(Channel channel) {
    this(channel, newDefaultExecutor("udpSender-", "-thread-"));
  }

  public UdpSender(Channel channel, Executor executor) {
    this.setChannel(channel);
    this.setExecutor(executor);
  }

  @Override
  public final void run() {
    if (Thread.currentThread() != udpSendLooper) {
      return;
    }


    final CountDownLatch latch = AWAITS.remove(this);
    if (latch != null) {
      latch.countDown();
    }
    // 注册结束时的回调钩子
    Runtime.getRuntime().addShutdownHook(new Thread(this::stopNow));
    final BlockingQueue<Consumer<Channel>> q = this.getTaskQueue();
    final AtomicBoolean state = this.runState;
    state.set(true);
    while (state.get()) {
      try {
        final Consumer<Channel> consumer = q.take();
        final Channel ch = getChannel();
        if (ch != null) {
          final Executor e = getExecutor();
          if (e != null) {
            e.execute(() -> consumer.accept(ch));
          } else {
            consumer.accept(ch);
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
    q.clear();
  }

  public Executor getExecutor() {
    return executor;
  }

  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  /**
   * 开启
   *
   * @param channel 通道
   */
  public void startNow(Channel channel) {
    this.setChannel(channel);
    this.startNow();
  }

  /**
   * 开启
   */
  public void startNow() {
    final Channel ch = this.getChannel();
    if (ch == null) {
      throw new IllegalStateException("channel == null");
    }

    synchronized (this) {
      if (AWAITS.containsKey(this)) {
        return;
      }

      final Thread t = this.udpSendLooper;

      if (t.getState() != Thread.State.NEW) {
        return;
      }

      try {
        final CountDownLatch latch;
        AWAITS.put(this, latch = new CountDownLatch(1));
        t.start();
        latch.await();
      } catch (InterruptedException ignore) {/* ignore */}
    }
  }

  public void stopNow() {
    synchronized (this) {
      this.runState.set(false);
      this.udpSendLooper.interrupt();
      this.setChannel(null);
    }
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public Channel getChannel() {
    return channel;
  }

  /**
   * @return 是否可写入
   */
  public boolean isWritable() {
    return getChannel() != null && isActive();
  }

  /**
   * @return 获取阻塞消息队列
   */
  protected BlockingQueue<Consumer<Channel>> getTaskQueue() {
    return taskQueue;
  }

  /**
   * @return 返回是否正在执行
   */
  public boolean getRunState() {
    Thread.State state = getState();
    return runState.get()
        && state != Thread.State.NEW
        && state != Thread.State.TERMINATED;
  }

  public Thread.State getState() {
    return udpSendLooper.getState();
  }

  /**
   * 发送数据包
   *
   * @param packet 数据包
   * @return 返回是否发送
   */
  public boolean sendNow(DatagramPacket packet) {
    final Channel ch = getChannel();
    if (ch != null && ch.isActive()) {
      final AtomicBoolean status = new AtomicBoolean(false);
      ch.writeAndFlush(packet)
          .addListener(f -> status.set(f.isSuccess()))
          .syncUninterruptibly();
      return status.get();
    }
    return false;
  }

  /**
   * 发送数据包
   *
   * @param data    数据
   * @param address 目的地址
   * @return 返回是否发送
   */
  public boolean sendNow(ByteBuf data, InetSocketAddress address) {
    return sendNow(new DatagramPacket(data, address, address));
  }

  /**
   * 发送数据包
   *
   * @param data    数据
   * @param address 目的地址
   * @return 返回是否发送
   */
  public boolean sendNow(byte[] data, InetSocketAddress address) {
    return sendNow(Unpooled.wrappedBuffer(data), address);
  }

  /**
   * 发送数据包
   *
   * @param dataList 数据
   * @param address  目的地址
   * @return 返回是否发送
   */
  public boolean sendNow(List<byte[]> dataList, InetSocketAddress address) {
    boolean send = false;
    for (byte[] data : dataList) {
      send |= sendNow(data, address);
    }
    return send;
  }

  /**
   * 发送消息
   *
   * @param channel 通道
   * @param msg     消息
   */
  protected void send(Channel channel, DatagramPacket msg) {
    channel.writeAndFlush(msg);
  }

  /**
   * 是否可添加消息
   */
  public boolean isActive() {
    return isWritable() && !getRunState();
  }

  /**
   * 添加消息
   *
   * @param packet UDP数据包
   * @return 返回是否添加
   */
  public boolean addMsg(DatagramPacket packet) {
    checkState();
    return getTaskQueue().offer(ch -> send(ch, packet));
  }

  /**
   * 添加消息
   *
   * @param packets UDP数据包
   */
  public void addMsgAll(List<DatagramPacket> packets) {
    for (DatagramPacket packet : packets) {
      addMsg(packet);
    }
  }

  /**
   * 添加任务
   *
   * @param task 任务
   * @return 返回是否添加
   */
  public boolean addTask(Consumer<Channel> task) {
    checkState();
    return getTaskQueue().offer(task);
  }

  /**
   * 添加任务
   *
   * @param tasks 多个任务
   */
  public void addTaskAll(List<Consumer<Channel>> tasks) {
    for (Consumer<Channel> task : tasks) {
      addTask(task);
    }
  }

  /**
   * 清空消息
   */
  public void clear() {
    getTaskQueue().clear();
  }

  private void checkState() {
    if (!isActive()) {
      throw new IllegalStateException("Stopped !");
    }
  }

  public static Executor newDefaultExecutor(String prefix, String suffix) {
    return Executors.newSingleThreadExecutor(new DefaultThreadFactory(prefix, suffix));
  }


  /**
   * The default thread factory
   */
  protected static class DefaultThreadFactory implements ThreadFactory {

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
