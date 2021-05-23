package org.learn.gossip;

import org.apache.log4j.Logger;
import org.learn.gossip.messages.handler.api.MessageHandler;
import org.learn.gossip.messages.handler.api.SourceInfo;
import org.learn.gossip.messages.handler.impl.MessageRouteHandler;

public class PendingResponseHandler implements MessageHandler<Boolean> {
	
	private static Logger LOGGER = Logger.getLogger(PendingResponseHandler.class);

	private GossipLocalTable localTable;
	
	
	
	public PendingResponseHandler(GossipLocalTable localTable) {
		super();
		this.localTable = localTable;
	}



	@Override
	public void handlerMessage(SourceInfo sourceHost, Boolean message) {
		// TODO Auto-generated method stub
		if(!localTable.responseHandlerMap.containsKey(sourceHost)) {
			return;
		}
		ResponseHandler handler = localTable.responseHandlerMap.get(sourceHost).second;
		if(handler == null)
			return;
		LOGGER.info("Got ack message from "+ sourceHost);
		if(message) {
			handler.handle();
			localTable.responseHandlerMap.remove(sourceHost);
		}
	}
	
}
