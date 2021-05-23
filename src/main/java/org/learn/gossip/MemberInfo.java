package org.learn.gossip;

import java.util.concurrent.atomic.AtomicInteger;

public class MemberInfo {
	
	private String uuid;
	private String hostname;
	private int port;
	private AtomicInteger incarnationId;
	
	public MemberInfo(String uuid, String hostname, int port, int incarnationId) {
		super();
		this.uuid = uuid;
		this.hostname = hostname;
		this.port = port;
		this.incarnationId = new AtomicInteger(incarnationId);
	}

	public String getUuid() {
		return uuid;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public int getIncarnationId() {
		return incarnationId.get();
	}
	
	public int incrementAndGetIncarnationId() {
		return incarnationId.incrementAndGet();
	}

	@Override
	public String toString() {
		return "MemberInfo [uuid=" + uuid + ", hostname=" + hostname + ", port=" + port + ", incarnationId="
				+ incarnationId.get() + "]";
	}

	
	
	
}
