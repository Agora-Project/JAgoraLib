/*

Copyright (C) 2015 Agora Communication Corporation

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

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
