package org.agora.graph;

public class JAgoraAttackID {
  protected JAgoraArgumentID originID;
	protected JAgoraArgumentID targetID;
	
	public JAgoraAttackID(JAgoraArgumentID originID, JAgoraArgumentID targetID) {
		this.originID = originID;
		this.targetID = targetID;
	}
	
	public JAgoraArgumentID getOriginID() { return originID; }
  public void setOriginID(JAgoraArgumentID originID) { this.originID = originID; }
  public JAgoraArgumentID getTargetID() { return targetID; }
  public void setTargetID(JAgoraArgumentID targetID) { this.targetID = targetID; }






  public String toString() { return "Edge("+originID.toString()+", "+targetID.toString()+")"; }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((originID == null) ? 0 : originID.hashCode());
    result = prime * result + ((targetID == null) ? 0 : targetID.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JAgoraAttackID other = (JAgoraAttackID) obj;
    if (originID == null) {
      if (other.originID != null)
        return false;
    } else if (!originID.equals(other.originID))
      return false;
    if (targetID == null) {
      if (other.targetID != null)
        return false;
    } else if (!targetID.equals(other.targetID))
      return false;
    return true;
  }

}
