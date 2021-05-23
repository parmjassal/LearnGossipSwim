package org.learn.gossip;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.learn.gossip.GossipLocalTable.Pair;
import org.learn.gossip.config.GlobalConfig;
import org.learn.gossip.messages.handler.impl.MessageRouteHandler;
import org.learn.gossip.messages.handler.impl.PingMessageHandler;
import org.learn.gossip.messages.handler.impl.PingRequestMessageHandler;
import org.learn.gossip.net.Server;

public class GossipMain {
	private static Logger LOGGER = Logger.getLogger(GossipMain.class);
	
	
	private final GlobalConfig globalConfig;
	private final MemberInfo localNode;
	private final GossipLocalTable localTable;
	private final Server server;
	
	public GossipMain(GlobalConfig globalConfig, MemberInfo localNode, List<MemberInfo> seedNodes) {
		super();
		this.globalConfig = globalConfig;
		this.localNode = localNode;
		this.localTable = new GossipLocalTable(localNode, seedNodes,globalConfig);
		PingMessageHandler pingMessageHandler = new PingMessageHandler(localTable);
		PingRequestMessageHandler pingRequestMessageHandler = new PingRequestMessageHandler(localTable);
		PendingResponseHandler pendingResponseHandler = new PendingResponseHandler(localTable);
		MessageRouteHandler handler = new MessageRouteHandler(pingMessageHandler, pingRequestMessageHandler, pendingResponseHandler);
		this.server = new Server(localNode.getPort(),handler, globalConfig);
	}

	public int getMembers() {
		return localTable.liveMemberInfos.size();
	}
	
	public int getLiveMembers() {
		int count = 0;
		for(Pair<MemberState, MemberInfo> pair:localTable.liveMemberInfos) {
			if(pair.first == MemberState.LIVE) {
				count++;
			}
		}
		return count;
	}
	
	public void PrintMembers() {
		LOGGER.info("Members in "+ localTable.currentNodeInfo.getUuid() +" are :- "+ localTable.liveMemberInfos);
	}
	
	public void start() {
		globalConfig.getScheduledExecutorService().schedule(new GossipTask(localTable, globalConfig), globalConfig.getProtocolTime(), TimeUnit.MILLISECONDS);
		this.server.start();
	}
	
	

}
