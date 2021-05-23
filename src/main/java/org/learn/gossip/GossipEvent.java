package org.learn.gossip;

import java.util.concurrent.atomic.AtomicInteger;

import org.learn.gossip.config.GlobalConfig;

public class GossipEvent implements Comparable<GossipEvent> {

	private final GlobalConfig config;
	private final String localUuid;
	private final MemberInfo info;
	private long timestamp;
	private final MemberState memberState;
	private AtomicInteger numTransmissions;

	public GossipEvent(MemberInfo info, MemberState memberState, String localUuid,GlobalConfig config) {
		super();
		this.localUuid = localUuid;
		this.info = info;
		this.timestamp = System.currentTimeMillis();
		this.memberState = memberState;
		this.numTransmissions = new AtomicInteger(0);
		this.config = config;
	}

	@Override
	public int compareTo(GossipEvent o) {
		// TODO Auto-generated method stub
		double score1 = 1.0/(1+numTransmissions.get()) + (1.0*(System.currentTimeMillis()-timestamp))/config.getProtocolTime();
		double score2 = 1.0/(1+o.numTransmissions.get()) + (1.0*(System.currentTimeMillis()-o.timestamp))/config.getProtocolTime();
		return Double.compare(score2,score1);
	}

	public int incrementAndGetTransmissions() {
		return numTransmissions.incrementAndGet();
	}

	public MemberInfo getInfo() {
		return info;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public MemberState getMemberState() {
		return memberState;
	}
	
	public void updateTimestamp() {
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "GossipEvent [Uuid=" + info.getUuid() + ", memberState=" + memberState + ", numTransmissions="
				+ numTransmissions + " incarnationId="+ info.getIncarnationId() +"]";
	}

	
}
