package org.agora.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.agora.graph.Graph;
import org.agora.graph.Node;
import org.agora.graph.Edge;
import org.agora.graph.NodeID;
import org.agora.logging.Log;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.types.BasicBSONList;

public class JAgoraComms {
  
  // Serialisation
  //public static DateFormat DATE_FORMAT = new SimpleDateFormat(); //Deprecated?
  
  // ***********************
  // **** Communication ****
  // ***********************
  public static BasicBSONObject readBSONObjectFromSocket(Socket s) {
    try {
      InputStream is = s.getInputStream();
      BSONDecoder bdec = new BasicBSONDecoder();
      return (BasicBSONObject)bdec.readObject(is);
    } catch (IOException e) {
      Log.error("Could not read BSON object from socket " + s);
      Log.error(e.getMessage());
    }
    
    return null; 
  }
  
  
  public static boolean writeBSONObjectToSocket(Socket s, BasicBSONObject bson) {
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(bson);
    
    try {
      s.getOutputStream().write(b);
      return true;
    } catch (IOException e) {
      Log.error("Could not write BSON object to socket " + s);
      Log.error(e.getMessage());
    }
    
    return false;
  }
  
  
 // ***********************
 // **** Serialisation ****
 // ***********************
  
  public static BasicBSONObject BSONiseNodeID(NodeID nodeID) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("source", nodeID.getSource());
    bson.put("id", nodeID.getLocalID());
    return bson;
  }
  
  public static BasicBSONObject BSONiseNode(Node node) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("id", BSONiseNodeID(node.getID()));
    bson.put("posterName", node.getPosterName());
    bson.put("posterID", node.getPosterID());
    bson.put("date", node.getDate().toString());
    bson.put("acceptability", node.getAcceptability());
    bson.put("threadID", node.getThreadID());
    return bson;
  }
  
  public static BasicBSONObject BSONiseEdge(Edge edge) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("origin", BSONiseNodeID(edge.getOrigin().getID()));
    bson.put("target", BSONiseNodeID(edge.getTarget().getID()));
    return bson;
  }
  
  public static BasicBSONObject BSONiseGraph(Graph graph) {
    BasicBSONObject bsonGraph = new BasicBSONObject();
    
    // Add nodes.
    BasicBSONList bsonNodeList = new BasicBSONList();
    Node[] nodes = graph.getNodes();
    for (int i = 0; i < nodes.length; i++) {
      bsonNodeList.add(BSONiseNode(nodes[i]));
    }
    
    bsonGraph.put("nodes", bsonNodeList);
    
    // Add edges.
    BasicBSONList bsonEdgeList = new BasicBSONList();
    for (Edge e: graph.edgeMap.values()) {
      bsonNodeList.add(BSONiseEdge(e));
    }
    
    bsonGraph.put("edges", bsonEdgeList);
    
    return bsonGraph;
  }
  
 /*
 *************************
 **** Deserialization ****
 *************************
 */

  public static NodeID deBSONiseNodeID(BasicBSONObject bsonNodeID) {
    return new NodeID(
        bsonNodeID.getString("source"),
        bsonNodeID.getInt("id")
    );
  }
  
  public static Node deBSONiseNode(BasicBSONObject bsonNode) {
    NodeID nodeID = deBSONiseNodeID((BasicBSONObject)bsonNode.get("id"));
    Node node = new Node(nodeID);
    
    node.setPosterID(bsonNode.getInt("id"));
    node.setPosterName(bsonNode.getString("posterName"));
    node.setDate(bsonNode.getDate("date"));
    node.setAcceptability(bsonNode.getInt("acceptability"));
    node.setThreadID(bsonNode.getInt("threadID"));

    return node;
  }
  
  public static Edge deBSONiseEdge(BasicBSONObject bsonEdge, Graph graph) {
    NodeID originID = deBSONiseNodeID((BasicBSONObject)bsonEdge.get("origin"));
    NodeID targetID = deBSONiseNodeID((BasicBSONObject)bsonEdge.get("target"));


    // Check whether the origin nodes are or are not in the graph.
    // If they are not, simply add a node containing only an ID.
    // That should be enough to ask for it in case it's interesting.
    Node originNode = null;
    Node targetNode = null;

    if (graph.isInGraph(originID)) originNode = graph.getNodeByID(originID);
    else                           originNode = new Node(originID);

    if (graph.isInGraph(targetID)) targetNode = graph.getNodeByID(targetID);
    else                           targetNode = new Node(targetID);

    return new Edge(originNode, targetNode);
  }
  
  public static Graph deBSONiseGraph(BasicBSONObject bsonGraph) {
    Graph graph = new Graph();
    BasicBSONList nodes = (BasicBSONList)bsonGraph.get("nodes");
    for (Object n: nodes)
      graph.addNode(deBSONiseNode((BasicBSONObject) n));

    BasicBSONList edges = (BasicBSONList)bsonGraph.get("edges");
    for (Object e : edges)
      graph.addEdge(deBSONiseEdge((BasicBSONObject) e, graph));

    return graph;
  }
}




