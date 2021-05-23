package org.learn.gossip.messages.handler.api;

public interface MessageHandler<T> {
	
	public void handlerMessage(SourceInfo sourceHost, T message);
	
}
