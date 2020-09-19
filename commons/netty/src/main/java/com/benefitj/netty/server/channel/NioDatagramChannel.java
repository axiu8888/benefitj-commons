package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An NIO datagram {@link Channel} that sends and receives an
 * {@link AddressedEnvelope AddressedEnvelope<ByteBuf, SocketAddress>}.
 *
 * @see AddressedEnvelope
 * @see DatagramPacket
 */
class NioDatagramChannel extends AbstractChannel {

  private static final ChannelMetadata METADATA = new ChannelMetadata(false);
  private final DefaultChannelConfig config = new DefaultChannelConfig(this);
  /**
   * 远程地址
   */
  private volatile InetSocketAddress remoteAddress;
  /**
   * 是否为 active
   */
  private volatile boolean open = true;
  /**
   * 读取状态
   */
  private final AtomicBoolean reading = new AtomicBoolean(false);
  /**
   * 缓冲队列
   */
  private final Queue<ByteBuf> bufQueue = new ConcurrentLinkedQueue<>();

  /**
   * 最新接收数据包的时间
   */
  private volatile long lastReaderTime = System.currentTimeMillis();
  private volatile long lastWriterTime = System.currentTimeMillis();

  private final AtomicReference<ScheduledFuture<?>> timeoutFuture = new AtomicReference<>();
  /**
   * Creates a new instance.
   *
   * @param parent the parent of this channel. {@code null} if there's no parent.
   */
  public NioDatagramChannel(NioDatagramServerChannel parent, InetSocketAddress remoteAddress) {
    super(parent);
    this.remoteAddress = remoteAddress;
  }


  @Override
  public NioDatagramServerChannel parent() {
    return (NioDatagramServerChannel) super.parent();
  }

  /**
   * Returns the configuration of this channel.
   */
  @Override
  public ChannelConfig config() {
    return config;
  }

  /**
   * Returns {@code true} if the {@link Channel} is open and may get active later
   */
  @Override
  public boolean isOpen() {
    return open && parent().isActive();
  }

  /**
   * Return {@code true} if the {@link Channel} is active and so connected.
   */
  @Override
  public boolean isActive() {
    return isOpen();
  }

  /**
   * Return the {@link ChannelMetadata} of the {@link Channel} which describe the nature of the {@link Channel}.
   */
  @Override
  public ChannelMetadata metadata() {
    return METADATA;
  }

  /**
   * Create a new {@link AbstractUnsafe} instance which will be used for the life-time of the {@link Channel}
   */
  @Override
  protected AbstractUnsafe newUnsafe() {
    return new UdpChannelUnsafe();
  }

  /**
   * Return {@code true} if the given {@link EventLoop} is compatible with this instance.
   *
   * @param loop
   */
  @Override
  protected boolean isCompatible(EventLoop loop) {
    //return loop instanceof DefaultEventLoop;
    //return loop instanceof NioEventLoop;
    return true;
  }

  /**
   * Returns the {@link SocketAddress} which is bound locally.
   */
  @Override
  protected InetSocketAddress localAddress0() {
    return parent().localAddress();
  }

  @Override
  public SocketAddress localAddress() {
    return localAddress0();
  }

  /**
   * Return the {@link SocketAddress} which the {@link Channel} is connected to.
   */
  @Override
  protected InetSocketAddress remoteAddress0() {
    return remoteAddress;
  }

  @Override
  public InetSocketAddress remoteAddress() {
    return remoteAddress0();
  }

  @Override
  protected void doRegister() throws Exception {
    scheduleTimeout();
  }

  /**
   * Bind the {@link Channel} to the {@link SocketAddress}
   *
   * @param localAddress
   */
  @Deprecated
  @Override
  protected void doBind(SocketAddress localAddress) throws Exception {
  }

  /**
   * Disconnect this {@link Channel} from its remote peer
   */
  @Override
  protected void doDisconnect() throws Exception {
    doClose();
  }

  /**
   * Close the {@link Channel}
   */
  @Override
  protected void doClose() throws Exception {
    this.open = false;
    parent().closeChannel(this);
  }

  /**
   * Schedule a read operation.
   */
  @Override
  protected void doBeginRead() throws Exception {
    //is reading check, because the pipeline head context will call read again
    if (this.reading.compareAndSet(false, true)) {
      try {
        ByteBuf buf;
        while ((buf = this.bufQueue.poll()) != null) {
          this.lastReaderTime = System.currentTimeMillis();
          pipeline().fireChannelRead(buf);
        }
        pipeline().fireChannelReadComplete();
      } finally {
        this.reading.set(false);
      }
    }
  }

  /**
   * Flush the content of the given buffer to the remote peer.
   *
   * @param in
   */
  @Override
  protected void doWrite(ChannelOutboundBuffer in) throws Exception {
    //transfer all messages that are ready to be written to list
    final RecyclableArrayList list = RecyclableArrayList.newInstance();
    boolean freeFlag = true;
    try {
      Object current;
      while ((current = in.current()) != null) {
        if (current instanceof DatagramPacket) {
          list.add(((DatagramPacket) current).retain());
        } else if (current instanceof byte[]) {
          list.add(new DatagramPacket(Unpooled.wrappedBuffer((byte[]) current), remoteAddress()));
        } else {
          list.add(new DatagramPacket(((ByteBuf) current).retain(), remoteAddress()));
        }
        in.remove();
      }
      freeFlag = false;
    } finally {
      if (freeFlag) {
        for (Object obj : list) {
          ReferenceCountUtil.safeRelease(obj);
        }
        list.recycle();
      }
    }

    if (list.isEmpty()) {
      list.recycle();
      return;
    }

    this.doWrite0(list);
  }

  private void doWrite0(RecyclableArrayList list) {
    NioEventLoop loop = parent().eventLoop();
    if (loop.inEventLoop()) {
      try {
        this.lastWriterTime = System.currentTimeMillis();
        Unsafe unsafe = parent().unsafe();
        for (Object buf : list) {
          unsafe.write(buf, voidPromise());
        }
        unsafe.flush();
      } finally {
        list.recycle();
      }
    } else {
      //schedule a task that will write those entries
      parent().eventLoop().execute(() -> doWrite0(list));
    }
  }

  public NioDatagramChannel addByteBuf(ByteBuf msg) {
    this.bufQueue.add(msg);
    return this;
  }

  /**
   * 超时调度
   */
  protected void scheduleTimeout() {
    if (timeoutFuture.get() != null) return;

    ScheduledFuture<?> timeoutFuture = this.eventLoop().scheduleAtFixedRate(() -> {
      if (parent().isReaderTimeout(this.lastReaderTime)) {
        ScheduledFuture<?> future = this.timeoutFuture.get();
        if (future == null || future.isCancelled()) {
          return;
        }
        if (!isActive()) {
          this.timeoutFuture.getAndSet(null).cancel(true);
          return;
        }
        // 读取超时
        try {
          doClose();
        } catch (Throwable e) {
          PlatformDependent.throwException(e);
        } finally {
          this.timeoutFuture.getAndSet(null).cancel(true);
        }
      } else if (parent().isWriterTimeout(this.lastWriterTime)) {
        // 写入超时
        try {
          doClose();
        } catch (Throwable e) {
          PlatformDependent.throwException(e);
        } finally {
          this.timeoutFuture.getAndSet(null).cancel(true);
        }
      }
    }, 1, 1, TimeUnit.SECONDS);
    this.timeoutFuture.set(timeoutFuture);
  }

  final class UdpChannelUnsafe extends AbstractUnsafe {

    @Override
    public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      throw new UnsupportedOperationException();
    }
  }

}
