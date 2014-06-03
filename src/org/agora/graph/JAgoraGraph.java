package org.agora.graph;

import java.util.HashMap;
import java.util.Map;

public class JAgoraGraph {

	protected Map<JAgoraNodeID, JAgoraNode> nodeMap;
	
	// TODO: make this public and fix problems
	public Map<JAgoraEdgeID, JAgoraEdge> edgeMap;

	protected JAgoraNode[] nodes;

  public JAgoraGraph() {
    nodeMap = new HashMap<JAgoraNodeID, JAgoraNode>();
    edgeMap = new HashMap<JAgoraEdgeID, JAgoraEdge>();
  }

  public void addNode(JAgoraNode node) { nodeMap.put(node.getID(), node); }

	/**
	 * Adds edge to the Graph and to the respective nodes.
	 * @param edge
	 */
	public void addEdge(JAgoraEdge edge) {
		edgeMap.put(edge.getID(), edge);
		edge.getOrigin().addOutgoingEdge(edge);
		edge.getTarget().addIncomingEdge(edge);
	}


  public boolean isInGraph(JAgoraNodeID id) { return nodeMap.containsKey(id); }
	public JAgoraNode getNodeByID(JAgoraNodeID id) { return nodeMap.get(id); }
	public JAgoraNode[] getNodes() { return nodeMap.values().toArray(new JAgoraNode[0]); }

}
