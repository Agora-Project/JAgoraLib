package org.agora.graph;

public class JAgoraEdge {
  protected JAgoraNode origin;
  protected JAgoraNode target;

	protected JAgoraEdgeID id;
	
	protected String posterName;
	protected int posterID;

	protected VoteInformation votes;
	
	public JAgoraEdge() {}
	
	public JAgoraEdge(JAgoraNode origin, JAgoraNode target) {
	  construct(origin, target);
	}
	
	public void construct(JAgoraNode origin, JAgoraNode target) {
    this.origin = origin;
    this.target = target;
    
    id = new JAgoraEdgeID(origin.getID(), target.getID());
  }

	public JAgoraEdgeID getID() { return id; }
	public JAgoraNode getOrigin() { return origin; }
	public JAgoraNode getTarget() { return target; }
	public VoteInformation getVotes() { return votes; }

	public void setVotes(VoteInformation votes) { this.votes = votes; }
	
	public String toString() { return id.toString(); }
}
