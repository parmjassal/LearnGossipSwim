package org.learn.gossip.messages.handler.impl;

import org.apache.log4j.Logger;
import org.learn.gossip.GossipLocalTable;
import org.learn.gossip.MemberState;
import org.learn.gossip.messages.EventType;
import org.learn.gossip.messages.MemberInfo;
import org.learn.gossip.messages.MembershipEvent;
import org.learn.gossip.messages.Message;
import org.learn.gossip.messages.MessageType;
import org.learn.gossip.messages.handler.api.MessageHandler;
import org.learn.gossip.messages.handler.api.SourceInfo;
import org.learn.gossip.net.Client;
import org.learn.gossip.net.Server;

import com.google.flatbuffers.FlatBufferBuilder;

// Ping meesgae will piggy back the  member ship events as well.
public class PingMessageHandler implements MessageHandler<MembershipEvent> {
	private static Logger LOGGER = Logger.getLogger(PingMessageHandler.class);
	
	private final GossipLocalTable gossipLocalTable;
	
	public PingMessageHandler(GossipLocalTable gossipLocalTable) {
		super();
		this.gossipLocalTable = gossipLocalTable;
	}

	public void handlerMessage(SourceInfo sourceHost, MembershipEvent message) {
		// TODO Auto-generated method stub
		if(System.getProperty("test")!=null) {
			LOGGER.info("Error Injecting");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Exception in error Injection",e);	
			}
		}
		if (message != null) {
			MemberInfo memberInfo = message.member();
			LOGGER.info("Got request from " + memberInfo.uuid());
			if (gossipLocalTable.getCurrentNodeInfo().getUuid().equals(memberInfo.uuid()) ) {
				 if (message.eventType() == EventType.SUSPECT) {
					 // Add it to queue to make cluster about self alive.
					 LOGGER.info("Decalre self alive " + gossipLocalTable.getCurrentNodeInfo().getUuid());	
					 gossipLocalTable.declareAlive();
				} 
				sendAck(sourceHost);
				return;
			}
			org.learn.gossip.MemberInfo updateMemberInfo = new org.learn.gossip.MemberInfo(
					memberInfo.uuid(), memberInfo.hostname(), memberInfo.port(), memberInfo.incarnation());
			MemberState state = null;
			switch (message.eventType()) {
			case EventType.FAILED:
				state = MemberState.FAILED;
				break;
			case EventType.LIVE:
				state = MemberState.LIVE;
				break;
			case EventType.SUSPECT:
				state = MemberState.SUSPECT;
				break;
			}
			gossipLocalTable.updateState(state, updateMemberInfo);
		}
		sendAck(sourceHost);
	}

	private void sendAck(SourceInfo sourceHost) {
		FlatBufferBuilder builder = new FlatBufferBuilder(256);
		Message.startMessage(builder);
		Message.addType(builder, MessageType.ACK);
		Message.addServerPort(builder, gossipLocalTable.getCurrentNodeInfo().getPort());
		builder.finish(Message.endMessage(builder));
		LOGGER.info("Sending ACK from server on port "+ gossipLocalTable.getCurrentNodeInfo().getPort());
		Client.sendMessage(sourceHost.getSourceHostName(), sourceHost.getPort(), builder.dataBuffer());
	}

}
