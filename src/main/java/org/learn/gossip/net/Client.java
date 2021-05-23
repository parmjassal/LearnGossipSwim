package org.learn.gossip.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.apache.log4j.Logger;
import org.learn.gossip.messages.Message;

public class Client {
	private static Logger LOGGER = Logger.getLogger(Client.class);
	// Not keeping the client to DatagramChannel Open.
	public static boolean sendMessage(String hostname, int port, Message message) {
		SocketAddress address = new InetSocketAddress(hostname, port);
		try {
			DatagramChannel channel = DatagramChannel.open().bind(null);
			channel.send(message.getByteBuffer(), address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Failed to send message to" + address.toString(), e);
			return false;
		}
		return true;
	}

	public static boolean sendMessage(String hostname, int port, ByteBuffer message) {
		SocketAddress address = new InetSocketAddress(hostname, port);
		LOGGER.info("Sending message to " + hostname+":"+port);
		try {
			DatagramChannel channel = DatagramChannel.open().bind(null);
			channel.send(message, address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Failed to send message to" + address.toString(), e);
			return false;
		}
		return true;
	}
	
	
}
