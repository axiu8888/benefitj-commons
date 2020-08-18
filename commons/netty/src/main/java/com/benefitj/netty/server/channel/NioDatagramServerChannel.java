package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.*;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class NioDatagramServerChannel extends AbstractNioMessageChannel
    implements io.netty.channel.socket.DatagramChannel, ServerChannel {

  private static final ChannelMetadata METADATA = new ChannelMetadata(true);
  private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
  private static final String EXPECTED_TYPES =
      " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " +
          StringUtil.simpleClassName(AddressedEnvelope.class) + '<' +
          StringUtil.simpleClassName(ByteBuf.class) + ", " +
          StringUtil.simpleClassName(SocketAddress.class) + ">, " +
          StringUtil.simpleClassName(ByteBuf.class) + ')';

  private static DatagramChannel newSocket(SelectorProvider provider) {
    try {
      /**
       *  Use the {@link SelectorProvider} to open {@link SocketChannel} and so remove condition in
       *  {@link SelectorProvider#provider()} which is called by each DatagramChannel.open() otherwise.
       *
       *  See <a href="https://github.com/netty/netty/issues/2308">#2308</a>.
       */
      return provider.openDatagramChannel();
    } catch (IOException e) {
      throw new ChannelException("Failed to open a socket.", e);
    }
  }

  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  private static DatagramChannel newSocket(SelectorProvider provider, InternetProtocolFamily ipFamily) {
    if (ipFamily == null) {
      return newSocket(provider);
    }

    checkJavaVersion();

    try {
      return provider.openDatagramChannel(ProtocolFamilyConverter.convert(ipFamily));
    } catch (IOException e) {
      throw new ChannelException("Failed to open a socket.", e);
    }
  }

  private static void checkJavaVersion() {
    if (PlatformDependent.javaVersion() < 7) {
      throw new UnsupportedOperationException("Only supported on java 7+.");
    }
  }

  private Map<InetAddress, List<MembershipKey>> memberships;

  /**
   * 配置
   */
  private final DatagramServerChannelConfig config = new DatagramServerChannelConfig(this);
  /**
   * clients
   */
  private final Map<InetSocketAddress, NioDatagramChannel> children = new ConcurrentHashMap<>();

  /**
   * Create a new instance which will use the Operation Systems default {@link InternetProtocolFamily}.
   */
  public NioDatagramServerChannel() {
    this(newSocket(DEFAULT_SELECTOR_PROVIDER));
  }

  /**
   * Create a new instance using the given {@link SelectorProvider}
   * which will use the Operation Systems default {@link InternetProtocolFamily}.
   */
  public NioDatagramServerChannel(SelectorProvider provider) {
    this(newSocket(provider));
  }

  /**
   * Create a new instance using the given {@link InternetProtocolFamily}. If {@code null} is used it will depend
   * on the Operation Systems default which will be chosen.
   */
  public NioDatagramServerChannel(InternetProtocolFamily ipFamily) {
    this(newSocket(DEFAULT_SELECTOR_PROVIDER, ipFamily));
  }

  /**
   * Create a new instance using the given {@link SelectorProvider} and {@link InternetProtocolFamily}.
   * If {@link InternetProtocolFamily} is {@code null} it will depend on the Operation Systems default
   * which will be chosen.
   */
  public NioDatagramServerChannel(SelectorProvider provider, InternetProtocolFamily ipFamily) {
    this(newSocket(provider, ipFamily));
  }

  /**
   * Create a new instance from the given {@link DatagramChannel}.
   */
  public NioDatagramServerChannel(DatagramChannel socket) {
    super(null, socket, SelectionKey.OP_READ);
  }


  @Override
  protected DatagramChannel javaChannel() {
    return (DatagramChannel) super.javaChannel();
  }

  public DatagramSocket javaSocket() {
    return javaChannel().socket();
  }

  public Map<InetSocketAddress, NioDatagramChannel> children() {
    return children;
  }

  public NioDatagramChannel newChannel(InetSocketAddress remote) {
    return new NioDatagramChannel(this, remote);
  }

  /**
   * Read messages into the given array and return the amount which was read.
   *
   * @param buf
   */
  @Override
  protected int doReadMessages(List<Object> buf) throws Exception {
    RecvByteBufAllocator.Handle allocatorHandle = unsafe().recvBufAllocHandle();
    ByteBuf msg = allocatorHandle.allocate(config().getAllocator());
    allocatorHandle.attemptedBytesRead(msg.writableBytes());
    boolean free = true;
    try {
      //read message
      ByteBuffer nioBuf = msg.internalNioBuffer(msg.writerIndex(), msg.writableBytes());
      int nioPos = nioBuf.position();
      InetSocketAddress remote = (InetSocketAddress) javaChannel().receive(nioBuf);
      if (remote == null) {
        return 0;
      }
      allocatorHandle.lastBytesRead(nioBuf.position() - nioPos);
      msg.writerIndex(msg.writerIndex() + allocatorHandle.lastBytesRead());
      //allocate new channel or use existing one and push message to it
      NioDatagramChannel child = children().get(remote);
      if ((child == null) || !child.isOpen()) {
        child = newChannel(remote);
        NioDatagramChannel old = children().put(remote, child);
        buf.add(child);
        child.addByteBuf(msg);
        free = false;
        if (old != null) {
          old.doClose();
        }
        return 1;
      } else {
        child.addByteBuf(msg);
        free = false;
        if (child.isRegistered()) {
          child.read();
        }
        return 0;
      }
    } catch (Throwable t) {
      PlatformDependent.throwException(t);
      return -1;
    } finally {
      if (free) {
        msg.release();
      }
    }
  }

  /**
   * Write a message to the underlying {@link Channel}.
   *
   * @param msg
   * @param in
   * @return {@code true} if and only if the message has been written
   */
  @Override
  protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
    DatagramPacket packet = (DatagramPacket) msg;
    ByteBuf buf = packet.content();
    if (buf.readableBytes() <= 0) {
      return true;
    }
    ByteBuffer internalNioBuffer = buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes());
    return javaChannel().send(internalNioBuffer, packet.recipient()) > 0;
  }


  /**
   * Returns the {@link SocketAddress} which is bound locally.
   */
  @Override
  protected SocketAddress localAddress0() {
    try {
      return javaChannel().getLocalAddress();
    } catch (IOException e) {
      throw new ChannelException(e);
    }
  }

  /**
   * Return the {@link SocketAddress} which the {@link Channel} is connected to.
   */
  @Override
  protected InetSocketAddress remoteAddress0() {
    return null;
  }

  /**
   * Bind the {@link Channel} to the {@link SocketAddress}
   *
   * @param localAddress
   */
  @Override
  protected void doBind(SocketAddress localAddress) throws Exception {
    if (PlatformDependent.javaVersion() >= 7) {
      SocketUtils.bind(javaChannel(), localAddress);
    } else {
      javaChannel().socket().bind(localAddress);
    }
  }

  @Override
  protected void doClose() throws Exception {
    try {
      javaChannel().close();
    } finally {
      if (!children().isEmpty()) {
        for (NioDatagramChannel channel : children().values()) {
          try {
            channel.doClose();
          } catch (Exception ignore) {}
        }
      }
    }
  }

  /**
   * Returns the configuration of this channel.
   */
  @Override
  public DatagramServerChannelConfig config() {
    return config;
  }

  /**
   * Return {@code true} if the {@link Channel} is active and so connected.
   */
  @Override
  public boolean isActive() {
    DatagramChannel ch = javaChannel();
    return ch.isOpen() && (
        config().getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && isRegistered()
            || ch.socket().isBound());
  }

  /**
   * Return the {@link ChannelMetadata} of the {@link Channel} which describe the nature of the {@link Channel}.
   */
  @Override
  public ChannelMetadata metadata() {
    return METADATA;
  }

  /**
   * Connect to the remote peer
   *
   * @param remoteAddress
   * @param localAddress
   */
  @Deprecated
  @Override
  protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
    throw new UnsupportedOperationException();
  }

  /**
   * Finish the connect
   */
  @Deprecated
  @Override
  protected void doFinishConnect() throws Exception {
    throw new UnsupportedOperationException();
  }

  /**
   * Disconnect this {@link Channel} from its remote peer
   */
  @Deprecated
  @Override
  protected void doDisconnect() throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public InetSocketAddress localAddress() {
    return (InetSocketAddress) super.localAddress();
  }

  @Override
  public InetSocketAddress remoteAddress() {
    return (InetSocketAddress) super.remoteAddress();
  }

  /**
   * Return {@code true} if the {@link DatagramChannel} is connected to the remote peer.
   */
  @Override
  public boolean isConnected() {
    return false;
  }

  @Override
  protected Object filterOutboundMessage(Object msg) {
    if (msg instanceof DatagramPacket) {
      DatagramPacket p = (DatagramPacket) msg;
      ByteBuf content = p.content();
      if (isSingleDirectBuffer(content)) {
        return p;
      }
      return new DatagramPacket(newDirectBuffer(p, content), p.recipient());
    }

    if (msg instanceof ByteBuf) {
      ByteBuf buf = (ByteBuf) msg;
      if (isSingleDirectBuffer(buf)) {
        return buf;
      }
      return newDirectBuffer(buf);
    }

    if (msg instanceof AddressedEnvelope) {
      @SuppressWarnings("unchecked")
      AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>) msg;
      if (e.content() instanceof ByteBuf) {
        ByteBuf content = (ByteBuf) e.content();
        if (isSingleDirectBuffer(content)) {
          return e;
        }
        return new DefaultAddressedEnvelope<ByteBuf, SocketAddress>(newDirectBuffer(e, content), e.recipient());
      }
    }

    throw new UnsupportedOperationException(
        "unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
  }

  /**
   * Checks if the specified buffer is a direct buffer and is composed of a single NIO buffer.
   * (We check this because otherwise we need to make it a non-composite buffer.)
   */
  private static boolean isSingleDirectBuffer(ByteBuf buf) {
    return buf.isDirect() && buf.nioBufferCount() == 1;
  }

  /**
   * 判断是否读取超时
   *
   * @param time 时间
   * @return 返回是否超时
   */
  public boolean isReadTimeout(long time) {
    long timeout = config().readMillisTimeout();
    return timeout > 0 && System.currentTimeMillis() - time >= timeout;
  }

  /**
   * 判断是否写入超时
   *
   * @param time 时间
   * @return 返回是否超时
   */
  public boolean isWriteTimeout(long time) {
    long timeout = config().writeMillisTimeout();
    return timeout > 0 && System.currentTimeMillis() - time >= timeout;
  }

  public long readTimeout() {
    return this.config().readTimeout();
  }

  public NioDatagramServerChannel readTimeout(long readTimeout) {
    this.config().readTimeout(readTimeout);
    return this;
  }

  public long writeTimeout() {
    return this.config().writeTimeout();
  }

  public NioDatagramServerChannel writeTimeout(long writeTimeout) {
    this.config().writeTimeout(writeTimeout);
    return this;
  }

  public TimeUnit timeoutUnit() {
    return this.config().timeoutUnit();
  }

  public NioDatagramServerChannel timeoutUnit(TimeUnit timeoutUnit) {
    this.config().timeoutUnit(timeoutUnit);
    return this;
  }

  public NioDatagramServerChannel idle(long read, long write, TimeUnit unit) {
    this.readTimeout(read);
    this.writeTimeout(write);
    this.timeoutUnit(unit);
    return this;
  }


  @Override
  public ChannelFuture joinGroup(InetAddress multicastAddress) {
    return joinGroup(multicastAddress, newPromise());
  }

  @Override
  public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
    try {
      return joinGroup(
          multicastAddress,
          NetworkInterface.getByInetAddress(localAddress().getAddress()),
          null, promise);
    } catch (SocketException e) {
      promise.setFailure(e);
    }
    return promise;
  }

  @Override
  public ChannelFuture joinGroup(
      InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
    return joinGroup(multicastAddress, networkInterface, newPromise());
  }

  @Override
  public ChannelFuture joinGroup(
      InetSocketAddress multicastAddress, NetworkInterface networkInterface,
      ChannelPromise promise) {
    return joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
  }

  @Override
  public ChannelFuture joinGroup(
      InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
    return joinGroup(multicastAddress, networkInterface, source, newPromise());
  }

  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  @Override
  public ChannelFuture joinGroup(
      InetAddress multicastAddress, NetworkInterface networkInterface,
      InetAddress source, ChannelPromise promise) {

    checkJavaVersion();

    ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
    ObjectUtil.checkNotNull(networkInterface, "networkInterface");

    try {
      MembershipKey key;
      if (source == null) {
        key = javaChannel().join(multicastAddress, networkInterface);
      } else {
        key = javaChannel().join(multicastAddress, networkInterface, source);
      }

      synchronized (this) {
        List<MembershipKey> keys = null;
        if (memberships == null) {
          memberships = new HashMap<InetAddress, List<MembershipKey>>();
        } else {
          keys = memberships.get(multicastAddress);
        }
        if (keys == null) {
          keys = new ArrayList<MembershipKey>();
          memberships.put(multicastAddress, keys);
        }
        keys.add(key);
      }

      promise.setSuccess();
    } catch (Throwable e) {
      promise.setFailure(e);
    }

    return promise;
  }

  @Override
  public ChannelFuture leaveGroup(InetAddress multicastAddress) {
    return leaveGroup(multicastAddress, newPromise());
  }

  @Override
  public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
    try {
      return leaveGroup(
          multicastAddress, NetworkInterface.getByInetAddress(localAddress().getAddress()), null, promise);
    } catch (SocketException e) {
      promise.setFailure(e);
    }
    return promise;
  }

  @Override
  public ChannelFuture leaveGroup(
      InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
    return leaveGroup(multicastAddress, networkInterface, newPromise());
  }

  @Override
  public ChannelFuture leaveGroup(
      InetSocketAddress multicastAddress,
      NetworkInterface networkInterface, ChannelPromise promise) {
    return leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
  }

  @Override
  public ChannelFuture leaveGroup(
      InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
    return leaveGroup(multicastAddress, networkInterface, source, newPromise());
  }

  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  @Override
  public ChannelFuture leaveGroup(
      InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source,
      ChannelPromise promise) {
    checkJavaVersion();

    ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
    ObjectUtil.checkNotNull(networkInterface, "networkInterface");

    synchronized (this) {
      if (memberships != null) {
        List<MembershipKey> keys = memberships.get(multicastAddress);
        if (keys != null) {
          Iterator<MembershipKey> keyIt = keys.iterator();

          while (keyIt.hasNext()) {
            MembershipKey key = keyIt.next();
            if (networkInterface.equals(key.networkInterface())) {
              if (source == null && key.sourceAddress() == null ||
                  source != null && source.equals(key.sourceAddress())) {
                key.drop();
                keyIt.remove();
              }
            }
          }
          if (keys.isEmpty()) {
            memberships.remove(multicastAddress);
          }
        }
      }
    }

    promise.setSuccess();
    return promise;
  }

  /**
   * Block the given sourceToBlock address for the given multicastAddress on the given networkInterface
   */
  @Override
  public ChannelFuture block(
      InetAddress multicastAddress, NetworkInterface networkInterface,
      InetAddress sourceToBlock) {
    return block(multicastAddress, networkInterface, sourceToBlock, newPromise());
  }

  /**
   * Block the given sourceToBlock address for the given multicastAddress on the given networkInterface
   */
  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  @Override
  public ChannelFuture block(
      InetAddress multicastAddress, NetworkInterface networkInterface,
      InetAddress sourceToBlock, ChannelPromise promise) {
    checkJavaVersion();

    ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
    ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
    ObjectUtil.checkNotNull(networkInterface, "networkInterface");

    synchronized (this) {
      if (memberships != null) {
        List<MembershipKey> keys = memberships.get(multicastAddress);
        for (MembershipKey key : keys) {
          if (networkInterface.equals(key.networkInterface())) {
            try {
              key.block(sourceToBlock);
            } catch (IOException e) {
              promise.setFailure(e);
            }
          }
        }
      }
    }
    promise.setSuccess();
    return promise;
  }

  /**
   * Block the given sourceToBlock address for the given multicastAddress
   */
  @Override
  public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
    return block(multicastAddress, sourceToBlock, newPromise());
  }

  /**
   * Block the given sourceToBlock address for the given multicastAddress
   */
  @Override
  public ChannelFuture block(
      InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
    try {
      return block(
          multicastAddress,
          NetworkInterface.getByInetAddress(localAddress().getAddress()),
          sourceToBlock, promise);
    } catch (SocketException e) {
      promise.setFailure(e);
    }
    return promise;
  }

  @Override
  @Deprecated
  protected void setReadPending(boolean readPending) {
    super.setReadPending(readPending);
  }

  void clearReadPending0() {
    clearReadPending();
  }

  @Override
  protected boolean closeOnReadError(Throwable cause) {
    // We do not want to close on SocketException when using DatagramChannel as we usually can continue receiving.
    // See https://github.com/netty/netty/issues/5893
    if (cause instanceof SocketException) {
      return false;
    }
    return super.closeOnReadError(cause);
  }

  public void closeChannel(NioDatagramChannel channel) {
    EventLoop loop = channel.eventLoop();
    if (loop.inEventLoop()) {
      if (children().get(channel.remoteAddress0()) == channel) {
        children().remove(channel.remoteAddress0());
        channel.pipeline().fireChannelInactive();
        channel.close();
      }
    } else {
      loop.execute(() -> closeChannel(channel));
    }
  }
}
