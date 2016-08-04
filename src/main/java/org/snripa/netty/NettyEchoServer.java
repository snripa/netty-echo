package org.snripa.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * A simple HTTP ECHO application
 */
public class NettyEchoServer {

	private final int port;
	private EventLoopGroup serverWorkgroup;

	public NettyEchoServer(int port) {
		this.port = port;
	}

	public ChannelFuture start() {
		serverWorkgroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(serverWorkgroup).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HttpServerCodec());
						ch.pipeline().addLast(new HttpObjectAggregator(1048576));
						ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
							@Override
							public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
								ctx.flush();
								ctx.close();
							}

							@Override
							protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
								String uri = msg.uri().substring(1);
								ByteBuf responsePayload = ctx.alloc().buffer();
								responsePayload.writeCharSequence(uri, Charset.defaultCharset());
								DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, responsePayload);
								ctx.write(response);
							}
						});
					}
				});
		return b.bind();
	}

	public Future stop() {
		return serverWorkgroup.shutdownGracefully().awaitUninterruptibly();
	}
}