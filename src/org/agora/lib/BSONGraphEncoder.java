package org.agora.lib;

import org.agora.graph.JAgoraAttack;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraArgument;
import org.agora.graph.JAgoraArgumentID;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class BSONGraphEncoder {
  
  public BasicBSONObject BSONiseNodeID(JAgoraArgumentID nodeID) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("source", nodeID.getSource());
    bson.put("id", nodeID.getLocalID());
    return bson;
  }

  public BasicBSONObject BSONiseNode(JAgoraArgument node) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("id", BSONiseNodeID(node.getID()));
    bson.put("posterName", node.getPosterName());
    bson.put("posterID", node.getPosterID());
    bson.put("date", node.getDate().getTime());
    bson.put("acceptability", node.getAcceptability().doubleValue());
    bson.put("threadID", node.getThreadID());
    bson.put("content", node.getContent());
    return bson;
  }

  public BasicBSONObject BSONiseEdge(JAgoraAttack edge) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("origin", BSONiseNodeID(edge.getOrigin().getID()));
    bson.put("target", BSONiseNodeID(edge.getTarget().getID()));
    return bson;
  }

  public BasicBSONObject BSONiseGraph(JAgoraGraph graph) {
    BasicBSONObject bsonGraph = new BasicBSONObject();

    // Add nodes.
    BasicBSONList bsonNodeList = new BasicBSONList();
    JAgoraArgument[] nodes = graph.getNodes();
    for (int i = 0; i < nodes.length; i++) {
      bsonNodeList.add(BSONiseNode(nodes[i]));
    }

    bsonGraph.put("nodes", bsonNodeList);

    // Add edges.
    BasicBSONList bsonEdgeList = new BasicBSONList();
    for (JAgoraAttack e : graph.edgeMap.values()) {
      bsonEdgeList.add(BSONiseEdge(e));
    }

    bsonGraph.put("edges", bsonEdgeList);

    return bsonGraph;
  }
}
