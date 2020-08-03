package com.benefitj.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.RecyclableArrayList;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class NioDatagramChannel extends AbstractChannel {

	private final ChannelMetadata metadata = new ChannelMetadata(false);
	private final DefaultChannelConfig config = new DefaultChannelConfig(this);
	private final InetSocketAddress remoteAddress;

	private volatile boolean open = true;
	private final AtomicReference<Boolean> reading = new AtomicReference<>(false);
	private final ConcurrentLinkedQueue<ByteBuf> buffers = new ConcurrentLinkedQueue<>();

	private volatile ScheduledFuture<?> idleTask;

	protected NioDatagramChannel(NioDatagramServerChannel serverChannel, InetSocketAddress remoteAddress) {
		super(serverChannel);
		this.remoteAddress = remoteAddress;
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
		open = false;
		((NioDatagramServerChannel)parent()).removeChannel(this);
	}

	@Override
	protected void doDisconnect() throws Exception {
		doClose();
	}

	protected void addBuffer(ByteBuf buffer) {
		buffers.add(buffer);
	}

	@Override
	protected void doBeginRead() throws Exception {
		//is reading check, because the pipeline head context will call read again
		if (reading.compareAndSet(false, true)) {
			try {
				ByteBuf buffer;
				while ((buffer = buffers.poll()) != null) {
					pipeline().fireChannelRead(buffer);
				}
				pipeline().fireChannelReadComplete();
			} finally {
				reading.set(false);
			}
		}
	}

	@Override
	protected void doWrite(ChannelOutboundBuffer buffer) throws Exception {
		//transfer all messages that are ready to be written to list
		final RecyclableArrayList list = RecyclableArrayList.newInstance();
		boolean freeList = true;
		try {
			Object current;
			while ((current = buffer.current()) != null) {
				if (current instanceof DatagramPacket) {
					list.add(((DatagramPacket)current).retain());
				} else {
					list.add(new DatagramPacket(((ByteBuf)current).retain(), remoteAddress()));
				}
				buffer.remove();
			}
			freeList = false;
		} finally {
			if (freeList) {
				for (Object obj : list) {
					ReferenceCountUtil.safeRelease(obj);
				}
				list.recycle();
			}
		}
		//schedule a task that will write those entries
		parent().eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				try {
					for (Object buf : list) {
						parent().unsafe().write(buf, voidPromise());
					}
					parent().unsafe().flush();
				} finally {
					list.recycle();
				}
			}
		});
	}

	@Override
	protected boolean isCompatible(EventLoop eventloop) {
		return eventloop instanceof DefaultEventLoop;
	}

	@Override
	protected AbstractUnsafe newUnsafe() {
		return new UdpChannelUnsafe();
	}

	@Override
	protected SocketAddress localAddress0() {
		return ((NioDatagramServerChannel)parent()).localAddress0();
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
