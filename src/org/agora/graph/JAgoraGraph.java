package org.agora.graph;

import java.util.HashMap;
import java.util.Map;

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


  public boolean isInGraph(JAgoraArgumentID id) { return nodeMap.containsKey(id); }
	public JAgoraArgument getNodeByID(JAgoraArgumentID id) { return nodeMap.get(id); }
	public JAgoraArgument[] getNodes() { return nodeMap.values().toArray(new JAgoraArgument[0]); }

}
