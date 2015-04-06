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
