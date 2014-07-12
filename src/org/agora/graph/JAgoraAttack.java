package org.agora.graph;

public class JAgoraAttack {
  protected JAgoraArgument origin;
  protected JAgoraArgument target;

	protected JAgoraAttackID id;
	
	protected String posterName;
	protected int posterID;

	protected VoteInformation votes;
	
	public JAgoraAttack() {}
	
	public JAgoraAttack(JAgoraArgument origin, JAgoraArgument target) {
	  construct(origin, target);
	}
	
	public void construct(JAgoraArgument origin, JAgoraArgument target) {
    this.origin = origin;
    this.target = target;
    
    id = new JAgoraAttackID(origin.getID(), target.getID());
  }

	public JAgoraAttackID getID() { return id; }
	public JAgoraArgument getOrigin() { return origin; }
	public JAgoraArgument getTarget() { return target; }
	public VoteInformation getVotes() { return votes; }

	public void setVotes(VoteInformation votes) { this.votes = votes; }
	
	public String toString() { return id.toString(); }
}
