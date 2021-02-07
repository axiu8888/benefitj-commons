package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An NIO datagram {@link Channel} that sends and receives an
 * {@link AddressedEnvelope AddressedEnvelope<ByteBuf, SocketAddress>}.
 *
 * @see AddressedEnvelope
 * @see DatagramPacket
 */
public class NioDatagramChannel extends AbstractChannel {

  private static final ChannelMetadata METADATA = new ChannelMetadata(false);
  private final DefaultChannelConfig config = new DefaultChannelConfig(this);

  /**
   * 通道对应的Key
   */
  private final Serializable channelKey;

  /**
   * Channel
   */
  private volatile boolean open = true;
  /**
   * 远程地址
   */
  private InetSocketAddress remoteAddress;
  /**
   * 读取状态
   */
  private final AtomicBoolean reading = new AtomicBoolean(false);
  /**
   * 缓冲队列
   */
  private final Queue<DatagramPacket> queue = new ConcurrentLinkedQueue<>();

  /**
   * Creates a new instance.
   *
   * @param parent the parent of this channel. {@code null} if there's no parent.
   */
  public NioDatagramChannel(Serializable channelKey, NioDatagramServerChannel parent, InetSocketAddress remoteAddress) {
    super(parent);
    this.channelKey = channelKey;
    this.remoteAddress = remoteAddress;
  }

  public Serializable channelKey() {
    return channelKey;
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

  public Channel setOpen(boolean open) {
    this.open = open;
    return this;
  }

  /**
   * Returns {@code true} if the {@link Channel} is open and may get active later
   */
  @Override
  public boolean isOpen() {
    return this.open && parent().isOpen();
  }

  /**
   * Return {@code true} if the {@link Channel} is active and so connected.
   */
  @Override
  public boolean isActive() {
    return isOpen() && parent().isActive();
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
    // ~
  }

  /**
   * Bind the {@link Channel} to the {@link SocketAddress}
   *
   * @param localAddress
   */
  @Deprecated
  @Override
  protected void doBind(SocketAddress localAddress) throws Exception {
    throw new UnsupportedOperationException();
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
    this.setOpen(false);
    // 移除通道
    parent().closeChild(this);
  }

  /**
   * Schedule a read operation.
   */
  @Override
  protected void doBeginRead() throws Exception {
    //is reading check, because the pipeline head context will call read again
    if (this.reading.compareAndSet(false, true)) {
      try {
        DatagramPacket buf;
        while ((buf = this.queue.poll()) != null) {
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

  public NioDatagramChannel addMessage(DatagramPacket msg) {
    this.queue.offer(msg);
    return this;
  }

  final class UdpChannelUnsafe extends AbstractUnsafe {

    @Override
    public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      throw new UnsupportedOperationException();
    }
  }

  private static long now() {
    return System.currentTimeMillis();
  }

}
