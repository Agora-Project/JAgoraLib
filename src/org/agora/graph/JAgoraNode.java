package org.agora.graph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.BSONObject;

public class JAgoraNode {
	protected JAgoraNodeID id;
	
	protected String posterName;
	protected int posterID;
	
	protected Date date;
	
	protected BigDecimal acceptability;
	
	protected BSONObject content;
	
	protected int threadID;

	protected List<JAgoraEdge> incomingEdges;
	protected List<JAgoraEdge> outgoingEdges;
	
	protected VoteInformation votes; 

	public JAgoraNode() {
	  incomingEdges = new LinkedList<JAgoraEdge>();
    outgoingEdges = new LinkedList<JAgoraEdge>();
	}
	
	public void construct(JAgoraNodeID nodeID) {
	  this.id = nodeID;
	}
	
	public JAgoraNode(JAgoraNodeID nodeID) {
	  this();
	  construct(nodeID);
	}
	
	public JAgoraNode(String source, Integer ID) {
	  this();
	  construct(new JAgoraNodeID(source, ID));
	}

	/**
	 * Adds an edge to the node
	 * @param att
	 */
	public void addIncomingEdge(JAgoraEdge arg) { incomingEdges.add(arg); }
	public void addOutgoingEdge(JAgoraEdge arg) { outgoingEdges.add(arg); }
	
	public int getNumber() { return id.getLocalID(); }
	public String getSource() { return id.getSource(); }


	public Iterator<JAgoraEdge> getIncomingEdges() { return incomingEdges.iterator(); }
	public Iterator<JAgoraEdge> getOutgoingEdges() { return outgoingEdges.iterator(); }
	
        public ArrayList<JAgoraEdge> getIncomingEdgeList() {return new ArrayList<>(incomingEdges);}
        public ArrayList<JAgoraEdge> getOutgoingEdgeList() {return new ArrayList<>(outgoingEdges);}
	
	public JAgoraNodeID getID() { return id; }
  public String getPosterName() { return posterName; }
  public int getPosterID() { return posterID; }
  public Date getDate() { return date; }
  public BigDecimal getAcceptability() { return acceptability; }
  public int getThreadID() { return threadID; }
  public BSONObject getContent() { return content; }
  
  public void setID(JAgoraNodeID id) { this.id = id; }
  public void setPosterName(String posterName) { this.posterName = posterName; }
  public void setPosterID(int id) { this.posterID = id ; }
  public void setDate(Date date) { this.date = date; }
  public void setAcceptability(BigDecimal a) { this.acceptability = a; }
  public void setThreadID(int id) { this.threadID = id ; }
  public void setContent(BSONObject content) { this.content = content; }

  @Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(obj);
	}

	public VoteInformation getVotes() { return votes; }

  public void setVotes(VoteInformation votes) { this.votes = votes; }
  
  /**
   * Returns whether this Node is a placeholder. Placeholder nodes are used for
   * representing attacks in which only one of the attacker and defender is
   * within the thread that was asked for. They do not contain any information
   * except for the node ID and threadID, so that the client can request more
   * information about it or its thread afterwards.
   * @return
   */
  public boolean isPlaceholder() {
    // Should be enough ;)
    return content == null;
  }
  
  public String toString() { return id.toString(); }
}
