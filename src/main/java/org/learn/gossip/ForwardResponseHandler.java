package org.learn.gossip;

import org.learn.gossip.messages.Message;
import org.learn.gossip.messages.MessageType;
import org.learn.gossip.messages.handler.api.SourceInfo;
import org.learn.gossip.net.Client;

import com.google.flatbuffers.FlatBufferBuilder;

public class ForwardResponseHandler implements ResponseHandler {
	
	private final SourceInfo sourceInfo;
	private final GossipLocalTable localTable;
	

	public ForwardResponseHandler(SourceInfo sourceInfo, GossipLocalTable localTable) {
		super();
		this.sourceInfo = sourceInfo;
		this.localTable = localTable;
	}

	@Override
	public void handle() {
		FlatBufferBuilder builder =  new FlatBufferBuilder(1024);
		Message.startMessage(builder);
		Message.addType(builder, MessageType.ACK);
		Message.addServerPort(builder, localTable.getCurrentNodeInfo().getPort());
		builder.finish(Message.endMessage(builder));
		Client.sendMessage(sourceInfo.getSourceHostName(),sourceInfo.getPort(), builder.dataBuffer());
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		FlatBufferBuilder builder =  new FlatBufferBuilder(1024);
		Message.startMessage(builder);
		Message.addType(builder, MessageType.NACK);
		Message.addServerPort(builder, localTable.getCurrentNodeInfo().getPort());
		builder.finish(Message.endMessage(builder));
		Client.sendMessage(sourceInfo.getSourceHostName(),sourceInfo.getPort(), builder.dataBuffer());
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
		cleanup();
	}
	
	

}
