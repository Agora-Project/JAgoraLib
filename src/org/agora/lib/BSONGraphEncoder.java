package org.agora.lib;

import org.agora.graph.JAgoraEdge;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.agora.graph.JAgoraNodeID;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class BSONGraphEncoder {
  
  public BasicBSONObject BSONiseNodeID(JAgoraNodeID nodeID) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("source", nodeID.getSource());
    bson.put("id", nodeID.getLocalID());
    return bson;
  }

  public BasicBSONObject BSONiseNode(JAgoraNode node) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("id", BSONiseNodeID(node.getID()));
    bson.put("posterName", node.getPosterName());
    bson.put("posterID", node.getPosterID());
    bson.put("date", node.getDate().toString());
    bson.put("acceptability", node.getAcceptability().doubleValue());
    bson.put("threadID", node.getThreadID());
    bson.put("content", node.getContent());
    return bson;
  }

  public BasicBSONObject BSONiseEdge(JAgoraEdge edge) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("origin", BSONiseNodeID(edge.getOrigin().getID()));
    bson.put("target", BSONiseNodeID(edge.getTarget().getID()));
    return bson;
  }

  public BasicBSONObject BSONiseGraph(JAgoraGraph graph) {
    BasicBSONObject bsonGraph = new BasicBSONObject();

    // Add nodes.
    BasicBSONList bsonNodeList = new BasicBSONList();
    JAgoraNode[] nodes = graph.getNodes();
    for (int i = 0; i < nodes.length; i++) {
      bsonNodeList.add(BSONiseNode(nodes[i]));
    }

    bsonGraph.put("nodes", bsonNodeList);

    // Add edges.
    BasicBSONList bsonEdgeList = new BasicBSONList();
    for (JAgoraEdge e : graph.edgeMap.values()) {
      bsonNodeList.add(BSONiseEdge(e));
    }

    bsonGraph.put("edges", bsonEdgeList);

    return bsonGraph;
  }
}
