package org.agora.lib;

import java.math.BigDecimal;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.agora.graph.JAgoraEdge;
import org.agora.graph.JAgoraNodeID;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class BSONGraphDecoder {

  public JAgoraNodeID deBSONiseNodeID(BasicBSONObject bsonNodeID) {
    return new JAgoraNodeID(bsonNodeID.getString("source"),
        bsonNodeID.getInt("id"));
  }

  public JAgoraNode deBSONiseNode(BasicBSONObject bsonNode) {
    JAgoraNodeID nodeID = deBSONiseNodeID((BasicBSONObject) bsonNode.get("id"));
    JAgoraNode node = new JAgoraNode(nodeID);

    node.setPosterID(bsonNode.getInt("id"));
    node.setPosterName(bsonNode.getString("posterName"));
    node.setDate(bsonNode.getDate("date"));
    node.setAcceptability(new BigDecimal(bsonNode.getDouble("acceptability")));
    node.setThreadID(bsonNode.getInt("threadID"));
    node.setContent((BasicBSONObject)bsonNode.get("content"));
    
    return node;
  }

  public JAgoraEdge deBSONiseEdge(BasicBSONObject bsonEdge, JAgoraGraph graph) {
    JAgoraNodeID originID = deBSONiseNodeID((BasicBSONObject) bsonEdge.get("origin"));
    JAgoraNodeID targetID = deBSONiseNodeID((BasicBSONObject) bsonEdge.get("target"));

    // Check whether the origin nodes are or are not in the graph.
    // If they are not, simply add a node containing only an ID.
    // That should be enough to ask for it in case it's interesting.
    JAgoraNode originNode = null;
    JAgoraNode targetNode = null;

    if (graph.isInGraph(originID))
      originNode = graph.getNodeByID(originID);
    else
      originNode = new JAgoraNode(originID);

    if (graph.isInGraph(targetID))
      targetNode = graph.getNodeByID(targetID);
    else
      targetNode = new JAgoraNode(targetID);

    JAgoraEdge e = new JAgoraEdge();
    e.construct(originNode, targetNode);

    return e;
  }

  public JAgoraGraph deBSONiseGraph(BasicBSONObject bsonGraph) {
    JAgoraGraph graph = new JAgoraGraph();
    BasicBSONList nodes = (BasicBSONList) bsonGraph.get("nodes");
    for (Object n : nodes)
      graph.addNode(deBSONiseNode((BasicBSONObject) n));

    BasicBSONList edges = (BasicBSONList) bsonGraph.get("edges");
    for (Object e : edges)
      graph.addEdge(deBSONiseEdge((BasicBSONObject) e, graph));

    return graph;
  }

}
