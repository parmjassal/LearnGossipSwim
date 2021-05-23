package org.learn.gossip;

public class DummyMoveToFailHandler implements ResponseHandler {

	private final MemberInfo memberInfo;
	private final GossipLocalTable localTable;
	
	public DummyMoveToFailHandler(MemberInfo memberInfo, GossipLocalTable localTable) {
		super();
		this.memberInfo = memberInfo;
		this.localTable = localTable;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
		if(localTable.uuidToMemberInfo.get(memberInfo.getUuid()).second.getIncarnationId() <= memberInfo.getIncarnationId()) {
			assert(localTable.uuidToMemberInfo.get(memberInfo.getUuid()).first == MemberState.SUSPECT);
			localTable.updateState(MemberState.FAILED, memberInfo);
		}
	}
	
	

}
