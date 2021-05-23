package org.learn.gossip;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.learn.gossip.GossipLocalTable.Pair;
import org.learn.gossip.config.GlobalConfig;
import org.learn.gossip.messages.EventType;
import org.learn.gossip.messages.MembershipEvent;
import org.learn.gossip.messages.Message;
import org.learn.gossip.messages.MessageType;
import org.learn.gossip.messages.handler.api.SourceInfo;
import org.learn.gossip.net.Client;

import com.google.flatbuffers.FlatBufferBuilder;

public class GossipTask implements Runnable {

	private static Logger LOGGER = Logger.getLogger(GossipTask.class);

	private GossipLocalTable table;
	private GlobalConfig config;
	private Iterator<Pair<MemberState, MemberInfo>> iterator;

	public GossipTask(GossipLocalTable table, GlobalConfig config) {
		super();
		this.table = table;
		this.config = config;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if (iterator == null || !iterator.hasNext()) {
				if (iterator != null) // Hack around node detected as failed;
					table.currentNodeInfo.incrementAndGetIncarnationId();
				iterator = table.liveMemberInfos.iterator();
				LOGGER.info("Created iterator in " + table.currentNodeInfo.getUuid());
			}
			if (iterator.hasNext()) {
				LOGGER.info("Got Element for ping in " + table.currentNodeInfo.getUuid());
				sendPing();
			} else {
				scheduleNextRound(table, config, this);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Got Error", e);
		}
	}

	private void sendPing() {
		MemberInfo memberInfo = iterator.next().second;
		LOGGER.info("Got Element for ping in " + table.currentNodeInfo.getUuid() + " is " + memberInfo.getUuid());
		GossipEvent event = table.liveEvents.poll();
		if(event != null && event.getMemberState() != MemberState.SUSPECT && event.getInfo().getUuid().equals(memberInfo.getUuid())) {
			GossipEvent temp = table.liveEvents.poll();
			table.liveEvents.add(event);
			event = temp;
		}
		
		// Send Ping message with piggy backed events.
		FlatBufferBuilder builder = new FlatBufferBuilder(1024);
		int index = -1;
		if (event != null) {
			LOGGER.info("Event to publish in " + table.currentNodeInfo.getUuid() + " is " + event);
			int uuidIndex = builder.createString(event.getInfo().getUuid());
			int hostnameIndex = builder.createString(event.getInfo().getHostname());
			index = org.learn.gossip.messages.MemberInfo.createMemberInfo(builder, uuidIndex, hostnameIndex,
					event.getInfo().getPort(), event.getInfo().getIncarnationId());
		}
		if (index != -1) {
			MembershipEvent.startMembershipEvent(builder);
			MembershipEvent.addEventType(builder, event.getMemberState() == MemberState.LIVE ? EventType.LIVE
					: event.getMemberState() == MemberState.SUSPECT ? EventType.SUSPECT : EventType.FAILED);
			MembershipEvent.addMember(builder, index);
			index = MembershipEvent.endMembershipEvent(builder);
		}
		Message.startMessage(builder);
		Message.addType(builder, MessageType.PING);
		Message.addServerPort(builder, table.getCurrentNodeInfo().getPort());
		if (index != -1) {
			Message.addMessage(builder, index);
		} else {
			Message.addEmpty(builder, true);
		}
		builder.finish(Message.endMessage(builder));
		Client.sendMessage(memberInfo.getHostname(), memberInfo.getPort(), builder.dataBuffer());
		SourceInfo info = new SourceInfo(memberInfo.getHostname(), memberInfo.getPort());
		table.addPendingRequest(info, new LocalStateHandler(memberInfo, table));
		handleTimeout(info, memberInfo, table, config);
		if (event != null  && event.incrementAndGetTransmissions() < config.getMaxTransmissions())
			table.liveEvents.add(event);
		scheduleNextRound(table, config, this);
	}

	private static void scheduleNextRound(final GossipLocalTable table, final GlobalConfig config,
			final GossipTask task) {
		config.getScheduledExecutorService().schedule(task, config.getProtocolTime(), TimeUnit.MILLISECONDS);
	}

	// Not Doing k way ping for time.
	private static void handleTimeout(final SourceInfo info, MemberInfo memberInfo, final GossipLocalTable table,
			final GlobalConfig config) {
		config.getScheduledExecutorService().schedule(() -> {
			Pair<Long, ResponseHandler> handler = table.responseHandlerMap.get(info);
			handler.second.timeout();
			table.responseHandlerMap.remove(info);
			table.addPendingRequest(info, new DummyMoveToFailHandler(memberInfo, table));
			// Wait for failure time out.
			config.getScheduledExecutorService().schedule(() -> {
				Pair<Long, ResponseHandler> handler1 = table.responseHandlerMap.get(info);
				handler.second.timeout();
				table.responseHandlerMap.remove(info);
			}, config.getFailureWaitTimeOut(), TimeUnit.MILLISECONDS);
		}, config.getResponseWaitTimeOut(), TimeUnit.MILLISECONDS);
	}

}
