package org.learn.gossip.messages.handler.impl;

import org.learn.gossip.ForwardResponseHandler;
import org.learn.gossip.GossipLocalTable;
import org.learn.gossip.messages.MemberInfo;
import org.learn.gossip.messages.Message;
import org.learn.gossip.messages.MessageType;
import org.learn.gossip.messages.handler.api.MessageHandler;
import org.learn.gossip.messages.handler.api.SourceInfo;
import org.learn.gossip.net.Client;

import com.google.flatbuffers.FlatBufferBuilder;

// Ping request is going to work on MemberInfo
public class PingRequestMessageHandler implements MessageHandler<MemberInfo> {

	private final GossipLocalTable gossipLocalTable;

	public PingRequestMessageHandler(GossipLocalTable gossipLocalTable) {
		super();
		this.gossipLocalTable = gossipLocalTable;
	}

	public void handlerMessage(SourceInfo sourceHost, MemberInfo message) {
		
		ForwardResponseHandler responseHandler = new ForwardResponseHandler(sourceHost, gossipLocalTable);
		FlatBufferBuilder builder = new FlatBufferBuilder(256);
		Message.startMessage(builder);
		Message.addType(builder, MessageType.PING);
		Message.addServerPort(builder, gossipLocalTable.getCurrentNodeInfo().getPort());
		builder.finish(Message.endMessage(builder));
		Client.sendMessage(sourceHost.getSourceHostName(), sourceHost.getPort(), message.getByteBuffer());
		gossipLocalTable.addPendingRequest(sourceHost, responseHandler);		
		
	}

}
