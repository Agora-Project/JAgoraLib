package org.agora.lib;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.bson.BasicBSONObject;

public interface IJAgoraLib{
  // Communication codes!
  // Private constants for constructing messages.
  public final static int LOGIN_ACTION = 0;
  public final static int LOGOUT_ACTION = 1;
  public final static int QUERY_BY_THREAD_ID_ACTION = 2;
  public final static int ADD_ARGUMENT_ACTION = 3;
  
  public final static String USER_ID_FIELD = "id";
  public final static String SESSION_ID_FIELD = "sid";
  public final static String ACTION_FIELD = "act";
  public final static String USER_FIELD = "usr";
  public final static String PASSWORD_FIELD = "pwd";
  public final static String RESPONSE_FIELD = "r";
  public final static String REASON_FIELD = "rs";
  public final static String THREAD_ID_FIELD = "tID";
  public final static String GRAPH_FIELD = "g";
  public final static String CONTENT_FIELD = "c";
  public final static String ARGUMENT_FIELD = "arg";
  
  // Private constants for deconstructing server messages
  public final static int SERVER_OK = 0;
  public final static int SERVER_FAIL = 1;
  
  
  /**
   * Connects to the server and logs the user in. 
   * @param user
   * @param password
   * @return
   */
  boolean login(String user, String password);
  
  /**
   * Logs the user out, if he is logged in.
   * @return
   */
  boolean logout();
  
  
  /**
   * Checks whether there is an ongoing session.
   * @return
   */
  boolean isConnected();
  
  
  
  JAgoraNode addArgument(BasicBSONObject content, int threadID);  
  
  /**
   * Connects to the database and obtains the graph associated
   * with the given thread ID. This includes all arguments
   * proposed in that thread, as well as attacks in which at least
   * one of the intervening arguments is in the thread.
   * Arguments mentioned in Edges but not part of the thread will
   * only get minor information. For more information, request their thread.
   * @param threadID The thread id.
   * @return The graph representing the thread.
   */
  JAgoraGraph getThreadByID(int threadID);
}
