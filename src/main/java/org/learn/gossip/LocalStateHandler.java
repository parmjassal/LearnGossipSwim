package org.learn.gossip;

import org.apache.log4j.Logger;
import org.learn.gossip.messages.handler.impl.PingMessageHandler;

public class LocalStateHandler implements ResponseHandler {

	private static Logger LOGGER = Logger.getLogger(LocalStateHandler.class);
	// Whether we are expecting PING or PING_REQ.
	private final boolean singlePending;
	private final MemberInfo memberInfo;
	private final GossipLocalTable localTable;
	private volatile boolean done;

	public LocalStateHandler(MemberInfo memberInfo, GossipLocalTable localTable) {
		super();
		this.memberInfo = memberInfo;
		this.localTable = localTable;
		this.singlePending = true;
		this.done = false;
	}
	
	public LocalStateHandler(MemberInfo memberInfo, GossipLocalTable localTable, boolean singlePending) {
		super();
		this.memberInfo = memberInfo;
		this.localTable = localTable;
		this.singlePending = false;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		synchronized (this) {
			if(!this.done) {
				this.done = true;
				LOGGER.info("Uodating "+ memberInfo.getUuid() + "as Live in "+ localTable.getCurrentNodeInfo().getUuid());
				this.localTable.updateStateOnResponse(MemberState.LIVE, memberInfo);
			}
		}
		
	}

	@Override
	public void cleanup() {
		// Not going to handle the NACK as it is going to race with ACK which it will recieve
		// Because of out of order.
		// If neither ACK or NACK comes, time out will move the member to SUSPECT.
	}

	@Override
	public void timeout() {
		synchronized (this) {
			if(!this.done) {
				this.done = true;
				LOGGER.info("Uodating "+ memberInfo.getUuid() + "as Suspect in "+ localTable.getCurrentNodeInfo().getUuid());
				this.localTable.updateStateOnResponse(MemberState.SUSPECT, memberInfo);
			} 
		}
		
	}
	
	

}
