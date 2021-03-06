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

/**
 * This class contains vote information: the number of pro and con votes.
 *
 */
public class VoteInformation {
  protected int proVotes;
  protected int conVotes;
  
  public VoteInformation() {
    proVotes = 0;
    conVotes = 0;
  }
  
  public VoteInformation(int proVotes, int conVotes) {
    this.proVotes = proVotes;
    this.conVotes = conVotes;
  }

  public int getProVotes() { return proVotes; }
  public int getConVotes() { return conVotes; }
}
