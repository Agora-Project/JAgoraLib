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
