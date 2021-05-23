package org.learn.gossip;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.learn.gossip.config.GlobalConfig;
import org.learn.gossip.messages.handler.api.SourceInfo;

public class GossipLocalTable {

	protected static class Pair<R, T> {
		R first;
		T second;
		public Pair(R first, T second) {
			super();
			this.first = first;
			this.second = second;
		}
		@Override
		public String toString() {
			return "Pair [first=" + first + ", second=" + second + "]";
		}
	}
	ReentrantLock lock;
	// Local Info.
	protected final MemberInfo currentNodeInfo; 
	// Seggregation of events based on paper.
	protected final PriorityBlockingQueue<GossipEvent> liveEvents, failedMembers;
	// Members to traverse in roundRobin fashion.
	protected final CopyOnWriteArrayList<Pair<MemberState,MemberInfo>> liveMemberInfos ;
	// Uuid to memberInfo map.
	protected final ConcurrentHashMap<String, Pair<MemberState,MemberInfo>> uuidToMemberInfo;
	// Handler to pending response. 
	// PING  pending request should be one in one protocol time.
	// PING_REQ can be many.
	protected final ConcurrentHashMap<SourceInfo, Pair<Long,ResponseHandler>> responseHandlerMap;

	protected final ConcurrentHashMap<String, GossipEvent> uuidToEvent;
	
	protected final GlobalConfig config;
	
	public GossipLocalTable(MemberInfo currentNodeInfo, final List<MemberInfo> seedMembers, GlobalConfig config) {
		super();
		this.liveEvents = new PriorityBlockingQueue<GossipEvent>();
		this.failedMembers = new PriorityBlockingQueue<GossipEvent>();
		this.liveMemberInfos = new CopyOnWriteArrayList<Pair<MemberState, MemberInfo>>();
		this.uuidToMemberInfo = new ConcurrentHashMap<String, Pair<MemberState,MemberInfo>>();
		this.responseHandlerMap = new ConcurrentHashMap<SourceInfo, Pair<Long,ResponseHandler>>();
		this.uuidToEvent = new ConcurrentHashMap<String, GossipEvent>();
		this.currentNodeInfo = currentNodeInfo;
		this.config = config;
		this.lock = new ReentrantLock();
		init(seedMembers);
	}
	
	private void init(final List<MemberInfo> seedMembers) {
		for(MemberInfo info: seedMembers) {
			Pair<MemberState, MemberInfo> pair = new Pair<MemberState, MemberInfo>(MemberState.LIVE,info);
			this.liveMemberInfos.add(pair);
			this.uuidToMemberInfo.put(info.getUuid(), pair);
		}
		GossipEvent event = new GossipEvent(currentNodeInfo, MemberState.LIVE, currentNodeInfo.getUuid(), config);
		this.liveEvents.add(event);
		
	}
	
	// According section 4.2 for overriding states based on incoming message.
	public void updateState(MemberState state, MemberInfo info) {
		if(state != MemberState.FAILED && !uuidToMemberInfo.containsKey(info.getUuid())) {
			// Dummy JOIN state when we haven't recorded the member already
			Pair<MemberState, MemberInfo> memberPair = new Pair<MemberState, MemberInfo>(MemberState.JOIN, info);
			uuidToMemberInfo.putIfAbsent(info.getUuid(), memberPair);
			liveMemberInfos.add(memberPair);
		}
		if(state == MemberState.FAILED) {
			Pair<MemberState, MemberInfo> memberPair = uuidToMemberInfo.remove(info.getUuid());
			if(memberPair == null) return;
			liveMemberInfos.remove(memberPair);
			addEvent(info.getUuid(), new GossipEvent(info, state, currentNodeInfo.getUuid(), config));
		} else {
			Pair<MemberState, MemberInfo> memberPair = uuidToMemberInfo.get(info.getUuid());
			// Only change if incoming incarnation id is bigger than previous.
			if(state == MemberState.LIVE) {
				if(memberPair.first == MemberState.JOIN || memberPair.second.getIncarnationId() < info.getIncarnationId()) {
					memberPair.first = state;
					memberPair.second = info;
					addEvent(info.getUuid(), new GossipEvent(info, state, currentNodeInfo.getUuid(), config));
				}
			} else if(state == MemberState.SUSPECT) {
				if(memberPair.first == MemberState.SUSPECT && memberPair.second.getIncarnationId() < info.getIncarnationId()) {
					memberPair.first = state;
					memberPair.second = info;
					addEvent(info.getUuid(), new GossipEvent(info, state, currentNodeInfo.getUuid(), config));
				} else if(memberPair.first == MemberState.LIVE && memberPair.second.getIncarnationId() <= info.getIncarnationId()) {
					memberPair.first = state;
					memberPair.second = info;
					addEvent(info.getUuid(), new GossipEvent(info, state, currentNodeInfo.getUuid(), config));
				}
			}
		}
	}
	
	public void updateStateOnResponse(MemberState state, MemberInfo info) {
		Pair<MemberState, MemberInfo> memberPair = uuidToMemberInfo.get(info.getUuid());
		if(memberPair.first == MemberState.SUSPECT && state == MemberState.LIVE ) {
			memberPair.first = state;
			memberPair.second = info;
			addEvent(info.getUuid(), new GossipEvent(info, state, currentNodeInfo.getUuid(), config));
		} else if(memberPair.first == MemberState.LIVE && state == MemberState.SUSPECT) {
			memberPair.first = state;
			memberPair.second = info;
			addEvent(info.getUuid(), new GossipEvent(info, state, currentNodeInfo.getUuid(), config));
		}
	}

	public void declareAlive() {
		currentNodeInfo.incrementAndGetIncarnationId();
		addEvent(currentNodeInfo.getUuid(), new GossipEvent(currentNodeInfo, MemberState.LIVE, currentNodeInfo.getUuid(), config));
	}
	
	public MemberInfo getCurrentNodeInfo() {
		return currentNodeInfo;
	} 
	
	public void addPendingRequest(SourceInfo  sourceHoInfo, ResponseHandler responseHandler) {
		Pair<Long, ResponseHandler> pair = new Pair<Long, ResponseHandler>(System.currentTimeMillis(), responseHandler);
		this.responseHandlerMap.put(sourceHoInfo, pair);
	}
	
	private void addEvent(String uuid, GossipEvent event) {
		GossipEvent toRemove = uuidToEvent.remove(uuid);
		liveEvents.remove(toRemove);
		liveEvents.add(event);
		uuidToEvent.put(uuid, event);
	}
	
}
