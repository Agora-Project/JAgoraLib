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

import java.util.HashMap;
import java.util.Map;
import org.agora.logging.Log;

public class JAgoraGraph {

	protected Map<JAgoraArgumentID, JAgoraArgument> nodeMap;
	
	// TODO: make this public and fix problems
	public Map<JAgoraAttackID, JAgoraAttack> edgeMap;

	protected JAgoraArgument[] nodes;

  public JAgoraGraph() {
    nodeMap = new HashMap<JAgoraArgumentID, JAgoraArgument>();
    edgeMap = new HashMap<JAgoraAttackID, JAgoraAttack>();
  }

  public void addNode(JAgoraArgument node) { nodeMap.put(node.getID(), node); }

	/**
	 * Adds edge to the Graph and to the respective nodes.
	 * @param edge
	 */
	public void addEdge(JAgoraAttack edge) {
		edgeMap.put(edge.getID(), edge);
		edge.getOrigin().addOutgoingEdge(edge);
		edge.getTarget().addIncomingEdge(edge);
	}
        
        public void removeNode(JAgoraArgumentID id) {
            for (JAgoraArgumentID localID : nodeMap.keySet()) {
                if (localID.equals(id)) nodeMap.remove(localID);
            }
            
        }
        
        public void merge(JAgoraGraph graph) {
            for (JAgoraArgument arg : graph.getNodes()) {
                boolean alreadyPresent = false;
                for (JAgoraArgument localArg : getNodes()) {
                    if (arg.getID().equals(localArg.getID())) {
                        alreadyPresent = true;
                        break;
                    }
                }
                if (!alreadyPresent) addNode(arg);
            }
            
            for (JAgoraAttack attack : graph.edgeMap.values().toArray(new JAgoraAttack[0])) {
                boolean addAttack = true;
                for (JAgoraAttack localAttack : edgeMap.values().toArray(new JAgoraAttack[0])) {
                    if (attack.id.equals(localAttack.id)) {
                        addAttack = false;
                        break;
                    }
                }
                
                if (addAttack) edgeMap.put(attack.getID(), attack);
            }
            
        }


  public boolean isInGraph(JAgoraArgumentID id) { return nodeMap.containsKey(id); }
	public JAgoraArgument getNodeByID(JAgoraArgumentID id) { return nodeMap.get(id); }
	public JAgoraArgument[] getNodes() { return nodeMap.values().toArray(new JAgoraArgument[0]); }
        public JAgoraAttack[] getAttacks() { return edgeMap.values().toArray(new JAgoraAttack[0]); }

}
