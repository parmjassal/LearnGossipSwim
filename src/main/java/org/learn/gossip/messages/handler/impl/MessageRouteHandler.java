package org.learn.gossip.messages.handler.impl;

import org.apache.log4j.Logger;
import org.learn.gossip.PendingResponseHandler;
import org.learn.gossip.messages.MemberInfo;
import org.learn.gossip.messages.MembershipEvent;
import org.learn.gossip.messages.Message;
import org.learn.gossip.messages.MessageType;
import org.learn.gossip.messages.handler.api.MessageHandler;
import org.learn.gossip.messages.handler.api.SourceInfo;

public class MessageRouteHandler implements MessageHandler<Message> {

	private static Logger LOGGER = Logger.getLogger(MessageRouteHandler.class);

	private final PingMessageHandler pingMessageHandler;
	private final PingRequestMessageHandler pingRequestMessageHandler;
	private final PendingResponseHandler pendingResponseHandler;

	public MessageRouteHandler(PingMessageHandler pingMessageHandler,
			PingRequestMessageHandler pingRequestMessageHandler, PendingResponseHandler pendingResponseHandler) {
		super();
		this.pingMessageHandler = pingMessageHandler;
		this.pingRequestMessageHandler = pingRequestMessageHandler;
		this.pendingResponseHandler = pendingResponseHandler;
	}

	public void handlerMessage(SourceInfo sourceHost, Message message) {
		// TODO Auto-generated method stub
		try {
			boolean empty = message.empty();
			switch (message.type()) {
			case MessageType.PING:
				MembershipEvent membershipEvent = new MembershipEvent();
				message.message(membershipEvent);
				pingMessageHandler.handlerMessage(sourceHost, !empty? membershipEvent:null);
				break;
			case MessageType.PING_RES:
				MemberInfo memberInfo = new MemberInfo();
				message.message(memberInfo);
				pingRequestMessageHandler.handlerMessage(sourceHost, !empty? memberInfo :null);
				break;
			case MessageType.ACK:
				pendingResponseHandler.handlerMessage(sourceHost, true);
				break;
			case MessageType.NACK:
				pendingResponseHandler.handlerMessage(sourceHost, false);
				break;
			default:
				LOGGER.error("Unknown Request");
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Got Error while processing request",e);
		}
	}

}
