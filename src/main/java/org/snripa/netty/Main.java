package org.snripa.netty;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Main {
	private static final int SERVER_PORT = 8888;

	private static Logger LOGGER = LoggerFactory.getLogger("Netty Echo Server");

	public static void main(String[] args) throws Exception {
		NettyEchoServer app = new NettyEchoServer(SERVER_PORT);
		Channel serverChannel = app.start().sync().channel();
		int PORT = ((InetSocketAddress) serverChannel.localAddress()).getPort();
		LOGGER.info("Open your web browser and navigate to " + "://127.0.0.1:" + PORT + '/');
		LOGGER.info("Echo back any uri requested");
		serverChannel.closeFuture().sync();
	}
}
