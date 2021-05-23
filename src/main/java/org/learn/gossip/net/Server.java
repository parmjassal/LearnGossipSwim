package org.learn.gossip.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.learn.gossip.config.GlobalConfig;
import org.learn.gossip.messages.Message;
import org.learn.gossip.messages.handler.api.MessageHandler;
import org.learn.gossip.messages.handler.api.SourceInfo;

public class Server {
	
	private static Logger LOGGER = Logger.getLogger(Server.class);
	private int port;
	private MessageHandler<Message> handler;
	private GlobalConfig config;
	
	
	
	public Server(int port, MessageHandler<Message> handler, GlobalConfig config) {
		super();
		this.port = port;
		this.handler = handler;
		this.config = config;
	}

	// Will block the thread for incoming connections
	public void start() {
		try {
			SocketAddress bindAddress = new InetSocketAddress(port);
			DatagramChannel channel = DatagramChannel.open();
			channel = channel.bind(bindAddress);
			for(;;) {
				ByteBuffer src = ByteBuffer.allocate(1024);
				InetSocketAddress remoteAddress = (InetSocketAddress) channel.receive(src);
				handleMessage(this, remoteAddress, src);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Schedule the handling on the executor service.
	private static void handleMessage(final Server server, final InetSocketAddress remoteAddress, final ByteBuffer src) {
		final MessageHandler<Message> handler = server.handler;
		
		server.config.getExecutorService().submit(()->{
			src.flip();
			Message message = Message.getRootAsMessage(src);
			SourceInfo ourceHost = new SourceInfo(remoteAddress.getHostName(), message.serverPort());
			LOGGER.info("Recieve message from "+ remoteAddress + "server address port:- " + message.serverPort());
			handler.handlerMessage(ourceHost, message);
		});
	}
	
}
