package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * UDP客户端通道
 */
public class NioDatagramChannel extends AbstractChannel {

  private final ChannelMetadata metadata = new ChannelMetadata(false);
  private final DefaultChannelConfig config = new DefaultChannelConfig(this);
  private final InetSocketAddress remoteAddress;

  private volatile boolean open = true;
  private final AtomicReference<Boolean> reading = new AtomicReference<>(false);
  private final Queue<ByteBuf> byteBufQueue = new ConcurrentLinkedQueue<>();
  /**
   * 最新接收数据包的时间
   */
  private volatile long lastReadTime = System.currentTimeMillis();
  private volatile long lastWriteTime = System.currentTimeMillis();

  private final AtomicReference<ScheduledFuture<?>> timeoutFutureRef = new AtomicReference<>();

  protected NioDatagramChannel(NioDatagramServerChannel serverChannel, InetSocketAddress remoteAddress) {
    super(serverChannel);
    this.remoteAddress = remoteAddress;
  }

  /**
   * 超时调度
   *
   * @param serverChannel
   */
  protected void scheduleTimeout(NioDatagramServerChannel serverChannel) {
    ScheduledFuture<?> timeoutFuture = this.eventLoop().scheduleAtFixedRate(() -> {
      if (serverChannel.isReadTimeout(this.lastReadTime)) {
        ScheduledFuture<?> future = this.timeoutFutureRef.get();
        if (future == null || future.isCancelled()) {
          return;
        }
        if (!isActive()) {
          this.timeoutFutureRef.getAndSet(null).cancel(true);
          return;
        }
        // 读取超时
        try {
          NioDatagramChannel.this.doClose();
        } catch (Throwable e) {
          PlatformDependent.throwException(e);
        } finally {
          this.timeoutFutureRef.getAndSet(null).cancel(true);
        }
      } else if (serverChannel.isWriteTimeout(this.lastWriteTime)) {
        // 写入超时
        try {
          NioDatagramChannel.this.doClose();
        } catch (Throwable e) {
          PlatformDependent.throwException(e);
        } finally {
          this.timeoutFutureRef.getAndSet(null).cancel(true);
        }
      }
    }, 1, 1, TimeUnit.SECONDS);
    this.timeoutFutureRef.set(timeoutFuture);
  }

  @Override
  public ChannelMetadata metadata() {
    return metadata;
  }

  @Override
  public ChannelConfig config() {
    return config;
  }

  @Override
  public boolean isActive() {
    return open;
  }

  @Override
  public boolean isOpen() {
    return isActive();
  }

  @Override
  protected void doClose() throws Exception {
    this.open = false;
    ((NioDatagramServerChannel) parent()).removeChannel(this);
    this.deregister();
    this.close();
  }

  @Override
  protected void doDisconnect() throws Exception {
    this.doClose();
  }

  protected void addBuffer(ByteBuf buffer) {
    this.byteBufQueue.add(buffer);
  }

  @Override
  protected void doBeginRead() throws Exception {
    //is reading check, because the pipeline head context will call read again
    if (this.reading.compareAndSet(false, true)) {
      try {
        ByteBuf buf;
        while ((buf = this.byteBufQueue.poll()) != null) {
          this.lastReadTime = System.currentTimeMillis();
          pipeline().fireChannelRead(buf);
        }
        pipeline().fireChannelReadComplete();
      } finally {
        this.reading.set(false);
      }
    }
  }

  @Override
  protected void doWrite(ChannelOutboundBuffer buffer) throws Exception {
    //transfer all messages that are ready to be written to list
    final RecyclableArrayList list = RecyclableArrayList.newInstance();
    boolean freeFlag = true;
    try {
      Object current;
      while ((current = buffer.current()) != null) {
        if (current instanceof DatagramPacket) {
          list.add(((DatagramPacket) current).retain());
        } else {
          list.add(new DatagramPacket(((ByteBuf) current).retain(), remoteAddress()));
        }
        buffer.remove();
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
    //schedule a task that will write those entries
    parent().eventLoop().execute(() -> {
      try {
        this.lastWriteTime = System.currentTimeMillis();
        Unsafe unsafe = parent().unsafe();
        for (Object buf : list) {
          unsafe.write(buf, voidPromise());
        }
        unsafe.flush();
      } finally {
        list.recycle();
      }
    });
  }

  @Override
  protected boolean isCompatible(EventLoop eventloop) {
    //return eventloop instanceof DefaultEventLoop;
    //return eventloop instanceof NioEventLoop;
    return true;
  }

  @Override
  protected AbstractUnsafe newUnsafe() {
    return new UdpChannelUnsafe();
  }

  @Override
  protected SocketAddress localAddress0() {
    return ((NioDatagramServerChannel) parent()).localAddress0();
  }

  @Override
  protected InetSocketAddress remoteAddress0() {
    return remoteAddress;
  }

  @Override
  public InetSocketAddress remoteAddress() {
    //return super.remoteAddress();
    return remoteAddress0();
  }

  @Override
  protected void doBind(SocketAddress addr) throws Exception {
    throw new UnsupportedOperationException();
  }

  private class UdpChannelUnsafe extends AbstractUnsafe {

    @Override
    public void connect(SocketAddress addr1, SocketAddress addr2, ChannelPromise pr) {
      throw new UnsupportedOperationException();
    }

  }

}
