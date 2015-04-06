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


public class JAgoraArgumentID {
	protected String source;
	protected Integer localID;
	
	public JAgoraArgumentID() {
	  source = null;
	  localID = null;
	}
	
	public JAgoraArgumentID(String source, Integer localID) {
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
    JAgoraArgumentID other = (JAgoraArgumentID) obj;
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
