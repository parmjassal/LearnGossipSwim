package org.learn.gossip;

public interface ResponseHandler {
	
	public void handle();
	
	public void cleanup();
	
	public void timeout();
}
