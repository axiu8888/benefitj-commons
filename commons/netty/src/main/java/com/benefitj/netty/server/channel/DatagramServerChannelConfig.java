package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannelConfig;

import java.net.SocketException;
import java.nio.channels.DatagramChannel;

/**
 * UDP服务端配置
 */
public class DatagramServerChannelConfig extends DefaultChannelConfig implements ServerSocketChannelConfig {

	private static final RecvByteBufAllocator recv_allocator = new FixedRecvByteBufAllocator(2048);

	private final DatagramChannel datagramChannel;

	public DatagramServerChannelConfig(Channel channel, DatagramChannel datagramChannel) {
		super(channel);
		this.datagramChannel = datagramChannel;
		setRecvByteBufAllocator(recv_allocator);
	}

	@Override
	public int getBacklog() {
		return 1;
	}

	@Override
	public ServerSocketChannelConfig setBacklog(int backlog) {
		return this;
	}

	@Override
	public ServerSocketChannelConfig setConnectTimeoutMillis(int timeout) {
		return this;
	}

	@Override
	public ServerSocketChannelConfig setPerformancePreferences(int arg0, int arg1, int arg2) {
		return this;
	}

	@Override
	public ServerSocketChannelConfig setAllocator(ByteBufAllocator alloc) {
		super.setAllocator(alloc);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator alloc) {
		super.setRecvByteBufAllocator(alloc);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setAutoRead(boolean autoread) {
		super.setAutoRead(true);
		return this;
	}

	@Override
	@Deprecated
	public ServerSocketChannelConfig setMaxMessagesPerRead(int n) {
		super.setMaxMessagesPerRead(n);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator est) {
		super.setMessageSizeEstimator(est);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setWriteSpinCount(int spincount) {
		super.setWriteSpinCount(spincount);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
		return (ServerSocketChannelConfig) super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
	}

	@Override
	public ServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
		return (ServerSocketChannelConfig) super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
	}

	@Override
	public ServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
		return (ServerSocketChannelConfig) super.setWriteBufferWaterMark(writeBufferWaterMark);
	}

	@Override
	public int getReceiveBufferSize() {
		try {
			return datagramChannel.socket().getReceiveBufferSize();
		} catch (SocketException ex) {
			throw new ChannelException(ex);
		}
	}

	@Override
	public ServerSocketChannelConfig setReceiveBufferSize(int size) {
		try {
			datagramChannel.socket().setReceiveBufferSize(size);
		} catch (SocketException ex) {
			throw new ChannelException(ex);
		}
		return this;
	}

	@Override
	public boolean isReuseAddress() {
		try {
			return datagramChannel.socket().getReuseAddress();
		} catch (SocketException ex) {
			throw new ChannelException(ex);
		}
	}

	@Override
	public ServerSocketChannelConfig setReuseAddress(boolean reuseaddr) {
		try {
			datagramChannel.socket().setReuseAddress(true);
		} catch (SocketException ex) {
			throw new ChannelException(ex);
		}
		return this;
	}

}
