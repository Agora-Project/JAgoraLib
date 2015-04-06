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

package org.agora.lib;

import java.math.BigDecimal;
import java.util.Date;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraArgument;
import org.agora.graph.JAgoraAttack;
import org.agora.graph.JAgoraArgumentID;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class BSONGraphDecoder {

  public JAgoraArgumentID deBSONiseNodeID(BasicBSONObject bsonNodeID) {
    return new JAgoraArgumentID(bsonNodeID.getString("source"),
        bsonNodeID.getInt("id"));
  }

  public JAgoraArgument deBSONiseNode(BasicBSONObject bsonNode) {
    JAgoraArgumentID nodeID = deBSONiseNodeID((BasicBSONObject) bsonNode.get("id"));
    JAgoraArgument node = new JAgoraArgument(nodeID);

    node.setPosterID(bsonNode.getInt("posterID"));
    node.setPosterName(bsonNode.getString("posterName"));
    node.setDate(new Date(bsonNode.getLong("date")));
    node.setAcceptability(new BigDecimal(bsonNode.getDouble("acceptability")));
    node.setThreadID(bsonNode.getInt("threadID"));
    node.setContent((BasicBSONObject)bsonNode.get("content"));
    
    return node;
  }

  public JAgoraAttack deBSONiseEdge(BasicBSONObject bsonEdge, JAgoraGraph graph) {
    JAgoraArgumentID originID = deBSONiseNodeID((BasicBSONObject) bsonEdge.get("origin"));
    JAgoraArgumentID targetID = deBSONiseNodeID((BasicBSONObject) bsonEdge.get("target"));

    // Check whether the origin nodes are or are not in the graph.
    // If they are not, simply add a node containing only an ID.
    // That should be enough to ask for it in case it's interesting.
    JAgoraArgument originNode = null;
    JAgoraArgument targetNode = null;

    if (graph.isInGraph(originID))
      originNode = graph.getNodeByID(originID);
    else
      originNode = new JAgoraArgument(originID);

    if (graph.isInGraph(targetID))
      targetNode = graph.getNodeByID(targetID);
    else
      targetNode = new JAgoraArgument(targetID);

    JAgoraAttack e = new JAgoraAttack();
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
