package org.agora.lib.server;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.agora.graph.JAgoraEdge;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.agora.graph.JAgoraNodeID;

/**
 * Decodes graphs from ResultSets given by the DB.
 */
public class DBGraphDecoder {
  protected JAgoraGraph graph;
  
  public DBGraphDecoder() { init(); }
  
  /**
   * Resets the internal graph.
   */
  public void init() {
    graph = new JAgoraGraph();
  }
  
  
  public JAgoraNode loadNodeFromResultSet(ResultSet rs) throws SQLException {
    JAgoraNode node = new JAgoraNode(rs.getString("source_ID"), rs.getInt("arg_ID"));
    node.setPosterName(rs.getString("username"));
    node.setPosterID(rs.getInt("user_ID"));
    node.setDate(rs.getDate("date"));
    node.setAcceptability(rs.getDouble("acceptability"));
    node.setThreadID(rs.getInt("thread_ID"));
    
    // TODO: get content
    
    return node;
  }
  
  /**
   * This takes in a ResultSet and makes the following assumptions: 1) first
   * column is argument source; 2) second column is argument ID.
   * @param rs Where the database results come from.
   */
  public void loadNodesFromResultSet(ResultSet rs) throws SQLException {
    while (rs.next()) {
      JAgoraNode node = new JAgoraNode(rs);
      graph.addNode(node);
    }
  }
  
  /**
   * Assumes the column ordering is sourceArg.source, sourceArg.localID,
   * targetArg.source, targetArg.localID
   * @param rs Where the database results come from.
   * @throws SQLException
   */
  public void loadEdgesFromResultSet(ResultSet rs) throws SQLException  {
    while(rs.next()) {
      JAgoraNodeID originID = new JAgoraNodeID(
          rs.getString("source_ID_attacker"),
          rs.getInt("arg_ID_attacker"));
      JAgoraNodeID targetID = new JAgoraNodeID(
          rs.getString("source_ID_attacker"),
          rs.getInt("arg_ID_attacker"));
      JAgoraEdge edge = new JAgoraEdge(graph.getNodeByID(originID),
                                       graph.getNodeByID(targetID));
      graph.addEdge(edge);
    }
  }
}
