package org.agora.graph;


public class JAgoraNodeID {
	protected String source;
	protected Integer localID;
	
	public JAgoraNodeID() {
	  source = null;
	  localID = null;
	}
	
	public JAgoraNodeID(String source, Integer localID) {
	  this.source = source;
	  this.localID = localID;
	}

  public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }
	public Integer getLocalID() { return localID; }
	public void setLocalID(int digit) { this.localID = digit; }
	

	public String toString() { return "Node("+source+"["+localID+"])"; }
	
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((localID == null) ? 0 : localID.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
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
    JAgoraNodeID other = (JAgoraNodeID) obj;
    if (localID == null) {
      if (other.localID != null)
        return false;
    } else if (!localID.equals(other.localID))
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    return true;
  }
}
