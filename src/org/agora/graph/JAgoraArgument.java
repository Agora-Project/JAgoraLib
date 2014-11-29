package org.agora.graph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.BSONObject;

public class JAgoraArgument {
	protected JAgoraArgumentID id;
	
	protected String posterName;
	protected int posterID;
	
	protected Date date;
	
	protected BigDecimal acceptability;
	
	protected BSONObject content;
	
	protected int threadID;

	protected List<JAgoraAttack> incomingEdges;
	protected List<JAgoraAttack> outgoingEdges;
	
	protected VoteInformation votes; 

	public JAgoraArgument() {
	  incomingEdges = new LinkedList<JAgoraAttack>();
    outgoingEdges = new LinkedList<JAgoraAttack>();
	}
	
	public void construct(JAgoraArgumentID nodeID) {
	  this.id = nodeID;
	}
	
	public JAgoraArgument(JAgoraArgumentID nodeID) {
	  this();
	  construct(nodeID);
	}
	
	public JAgoraArgument(String source, Integer ID) {
	  this();
	  construct(new JAgoraArgumentID(source, ID));
	}
        
        public String getText() {
            if (content.containsField("Text")) return (String) content.get("Text");
            if (content.containsField("txt")) return (String) content.get("txt");
            return "Error: No Text";
        }

	/**
	 * Adds an edge to the node
	 * @param att
	 */
	public void addIncomingEdge(JAgoraAttack arg) { incomingEdges.add(arg); }
	public void addOutgoingEdge(JAgoraAttack arg) { outgoingEdges.add(arg); }
	
	public int getNumber() { return id.getLocalID(); }
	public String getSource() { return id.getSource(); }


	public Iterator<JAgoraAttack> getIncomingEdges() { return incomingEdges.iterator(); }
	public Iterator<JAgoraAttack> getOutgoingEdges() { return outgoingEdges.iterator(); }
	
  public ArrayList<JAgoraAttack> getIncomingEdgeList() {return new ArrayList<>(incomingEdges);}
  public ArrayList<JAgoraAttack> getOutgoingEdgeList() {return new ArrayList<>(outgoingEdges);}
	
	public JAgoraArgumentID getID() { return id; }
  public String getPosterName() { return posterName; }
  public int getPosterID() { return posterID; }
  public Date getDate() { return date; }
  public BigDecimal getAcceptability() { return acceptability; }
  public int getThreadID() { return threadID; }
  public BSONObject getContent() { return content; }
  
  public void setID(JAgoraArgumentID id) { this.id = id; }
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
