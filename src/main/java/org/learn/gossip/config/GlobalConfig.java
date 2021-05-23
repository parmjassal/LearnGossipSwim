package org.learn.gossip.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class GlobalConfig {
	
	private final ScheduledExecutorService executorService;
	private final long protocolTime;
	// responseWaitTimeOut < protocolTime;
	private final long responseWaitTimeOut;
	private final long failureWaitTimeOut;
	private final int maxTransmissions;
	
	public GlobalConfig(ScheduledExecutorService executorService, long protocolTime, long responseWaitTimeOut,
			long failureWaitTimeOut, int maxTransmissions) {
		super();
		this.executorService = executorService;
		this.protocolTime = protocolTime;
		this.responseWaitTimeOut = responseWaitTimeOut;
		this.failureWaitTimeOut = failureWaitTimeOut;
		this.maxTransmissions = maxTransmissions;
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return executorService;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}	

	public long getProtocolTime() {
		return protocolTime;
	}

	public long getResponseWaitTimeOut() {
		return responseWaitTimeOut;
	}

	public long getFailureWaitTimeOut() {
		return failureWaitTimeOut;
	}

	public int getMaxTransmissions() {
		return maxTransmissions;
	}
	
	
	

}
