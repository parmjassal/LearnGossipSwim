package org.learn.gossip.messages.handler.api;

public class SourceInfo {
	
	private final String sourceHostName;
	private final int port;
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (sourceHostName+":"+port).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SourceInfo)) {
			return false;
		}
		SourceInfo o = (SourceInfo)obj;
		// TODO Auto-generated method stub
		if(sourceHostName.equals(o.sourceHostName)  && o.port == port) {
			return true;
		}
		return false;
	}
	
	public SourceInfo(String sourceHostName, int port) {
		super();
		this.sourceHostName = sourceHostName;
		this.port = port;
	}

	public String getSourceHostName() {
		return sourceHostName;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "SourceInfo [sourceHostName=" + sourceHostName + ", port=" + port + "]";
	}
	
	
	

}
