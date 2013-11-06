package org.agora.lib;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.agora.graph.JAgoraEdge;
import org.agora.graph.JAgoraNodeID;
import org.agora.graph.factories.IEdgeFactory;
import org.agora.graph.factories.IGraphFactory;
import org.agora.graph.factories.INodeFactory;
import org.agora.graph.factories.JAgoraEdgeFactory;
import org.agora.graph.factories.JAgoraGraphFactory;
import org.agora.graph.factories.JAgoraNodeFactory;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class BSONGraphDecoder<G extends JAgoraGraph, N extends JAgoraNode, E extends JAgoraEdge> {
  protected IGraphFactory<G> graphFactory;
  protected INodeFactory<N> nodeFactory;
  protected IEdgeFactory<E> edgeFactory;

  public BSONGraphDecoder() {
    // TODO: How do we fix this? HACKED. Probably doesn't work.
    graphFactory = (IGraphFactory<G>) new JAgoraGraphFactory();
    nodeFactory = (INodeFactory<N>) new JAgoraNodeFactory();
    edgeFactory = (IEdgeFactory<E>) new JAgoraEdgeFactory();
  }
  
  public BSONGraphDecoder(IGraphFactory<G> gf, INodeFactory<N> nf, IEdgeFactory<E> ef) {
    graphFactory = gf;
    nodeFactory = nf;
    edgeFactory = ef;
  }

  public JAgoraNodeID deBSONiseNodeID(BasicBSONObject bsonNodeID) {
    return new JAgoraNodeID(bsonNodeID.getString("source"),
        bsonNodeID.getInt("id"));
  }

  public N deBSONiseNode(BasicBSONObject bsonNode) {
    JAgoraNodeID nodeID = deBSONiseNodeID((BasicBSONObject) bsonNode.get("id"));
    N node = nodeFactory.produce();
    node.construct(nodeID);

    node.setPosterID(bsonNode.getInt("id"));
    node.setPosterName(bsonNode.getString("posterName"));
    node.setDate(bsonNode.getDate("date"));
    node.setAcceptability(bsonNode.getInt("acceptability"));
    node.setThreadID(bsonNode.getInt("threadID"));

    return node;
  }

  @SuppressWarnings("unchecked")
  public E deBSONiseEdge(BasicBSONObject bsonEdge, G graph) {
    JAgoraNodeID originID = deBSONiseNodeID((BasicBSONObject) bsonEdge.get("origin"));
    JAgoraNodeID targetID = deBSONiseNodeID((BasicBSONObject) bsonEdge.get("target"));

    // Check whether the origin nodes are or are not in the graph.
    // If they are not, simply add a node containing only an ID.
    // That should be enough to ask for it in case it's interesting.
    N originNode = null;
    N targetNode = null;

    if (graph.isInGraph(originID))
      originNode = (N) graph.getNodeByID(originID);
    else {
      originNode = nodeFactory.produce();
      originNode.construct(originID);
    }

    if (graph.isInGraph(targetID))
      targetNode = (N) graph.getNodeByID(targetID);
    else {
      targetNode = nodeFactory.produce();
      targetNode.construct(targetID);
    }

    E e = edgeFactory.produce();
    e.construct(originNode, targetNode);

    return e;
  }

  public G deBSONiseGraph(BasicBSONObject bsonGraph) {
    G graph = graphFactory.produce();
    BasicBSONList nodes = (BasicBSONList) bsonGraph.get("nodes");
    for (Object n : nodes)
      graph.addNode(deBSONiseNode((BasicBSONObject) n));

    BasicBSONList edges = (BasicBSONList) bsonGraph.get("edges");
    for (Object e : edges)
      graph.addEdge(deBSONiseEdge((BasicBSONObject) e, graph));

    return graph;
  }

}
