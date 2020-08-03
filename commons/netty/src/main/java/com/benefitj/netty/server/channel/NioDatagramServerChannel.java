package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.internal.PlatformDependent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UDP 服务端通道
 */
public class NioDatagramServerChannel extends AbstractNioMessageChannel implements ServerSocketChannel {

  private final ChannelMetadata metadata = new ChannelMetadata(true);
  private final DatagramServerChannelConfig config;

  protected final Map<InetSocketAddress, NioDatagramChannel> channels = new ConcurrentHashMap<>();

  public NioDatagramServerChannel() throws IOException {
    this(SelectorProvider.provider().openDatagramChannel(StandardProtocolFamily.INET));
  }

  protected NioDatagramServerChannel(DatagramChannel datagramChannel) {
    super(null, datagramChannel, SelectionKey.OP_READ);
    this.config = new DatagramServerChannelConfig(this, datagramChannel);
  }

  @Override
  public ServerSocketChannelConfig config() {
    return config;
  }

  @Override
  public ChannelMetadata metadata() {
    return metadata;
  }

  @Override
  protected DatagramChannel javaChannel() {
    return (DatagramChannel) super.javaChannel();
  }

  @Override
  public boolean isActive() {
    return this.javaChannel().isOpen() && this.javaChannel().socket().isBound();
  }

  @Override
  protected SocketAddress localAddress0() {
    return this.javaChannel().socket().getLocalSocketAddress();
  }

  @Override
  public InetSocketAddress localAddress() {
    return (InetSocketAddress) super.localAddress();
  }

  @Override
  protected SocketAddress remoteAddress0() {
    return null;
  }

  @Override
  public InetSocketAddress remoteAddress() {
    return null;
  }

  @Override
  protected void doBind(SocketAddress addr) throws Exception {
    javaChannel().socket().bind(addr);
  }

  @Override
  protected void doClose() throws Exception {
    try {
			for (InetSocketAddress remote : channels.keySet()) {
				channels.remove(remote).close();
			}
    } finally {
      javaChannel().close();
    }
  }

  public void removeChannel(final Channel channel) {
    eventLoop().submit(new Runnable() {
      @Override
      public void run() {
        InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (channels.get(remote) == channel) {
          channels.remove(remote);
        }
      }
    });
  }

  @SuppressWarnings("deprecation")
  @Override
  protected int doReadMessages(List<Object> list) throws Exception {
    RecvByteBufAllocator.Handle allocatorHandle = unsafe().recvBufAllocHandle();
    ByteBuf buffer = allocatorHandle.allocate(config.getAllocator());
    allocatorHandle.attemptedBytesRead(buffer.writableBytes());
    boolean freeBuffer = true;
    try {
      //read message
      ByteBuffer nioBuffer = buffer.internalNioBuffer(buffer.writerIndex(), buffer.writableBytes());
      int nioPos = nioBuffer.position();
      InetSocketAddress remote = (InetSocketAddress) javaChannel().receive(nioBuffer);
      if (remote == null) {
        return 0;
      }
      allocatorHandle.lastBytesRead(nioBuffer.position() - nioPos);
      buffer.writerIndex(buffer.writerIndex() + allocatorHandle.lastBytesRead());
      //allocate new channel or use existing one and push message to it
      NioDatagramChannel child = channels.get(remote);
      if ((child == null) || !child.isOpen()) {
        child = new NioDatagramChannel(this, remote);
        NioDatagramChannel oldChannel = channels.put(remote, child);
        list.add(child);
        child.addBuffer(buffer);
        freeBuffer = false;
        if (oldChannel != null) {
          oldChannel.close();
        }
        return 1;
      } else {
        child.addBuffer(buffer);
        freeBuffer = false;
        if (child.isRegistered()) {
          child.read();
        }
        return 0;
      }
    } catch (Throwable t) {
      PlatformDependent.throwException(t);
      return -1;
    } finally {
      if (freeBuffer) {
        buffer.release();
      }
    }
  }

  @Override
  protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer buffer) throws Exception {
    DatagramPacket packet = (DatagramPacket) msg;
    ByteBuf buf = packet.content();
    int readableBytes = buf.readableBytes();
    if (readableBytes == 0) {
      return true;
    }
    ByteBuffer internalNioBuffer = buf.internalNioBuffer(buf.readerIndex(), readableBytes);
    return javaChannel().send(internalNioBuffer, packet.recipient()) > 0;
  }

  @Override
  protected boolean doConnect(SocketAddress addr1, SocketAddress addr2) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doFinishConnect() throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doDisconnect() throws Exception {
    throw new UnsupportedOperationException();
  }

}
