package org.agora.lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNodeID;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraLib implements IJAgoraLib {  
  
  protected int userID;
  protected String sessionID;
  protected String hostname;
  protected int port;
  
  protected BSONGraphEncoder encoder;
  protected BSONGraphDecoder decoder;
  
  /**
   * @param hostname The Agora server location.
   * @param port The port on which the server is listening.
   */
  public JAgoraLib(String hostname, int port) {
    userID = -1;
    sessionID = null;
    this.hostname = hostname;
    this.port = port;
    
    encoder = new BSONGraphEncoder();
    decoder = new BSONGraphDecoder();
  }
  
  protected Socket openConnection() {
    return openConnection(hostname, port);
  }
  
  protected Socket openConnection(String hostname, int port) {
    Socket s = null;
    try {
      s = new Socket(hostname, port);
    } catch (UnknownHostException e) {
      Log.error("[JAgoraLib] Could not identify host " +"("+e.getMessage()+")");
    } catch (IOException e) {
      Log.error("[JAgoraLib] Error opening connection to " + hostname + ":"+port +" ("+e.getMessage()+")");
    }
    return s;
  }
  
  protected boolean closeConnection(Socket s) {
    try {
      s.close();
      return true;
    } catch (IOException e) {
      Log.error("[JAgoraLib] Could not close connection " + s + " ("+e.getMessage()+")");
    }
    return false;
  }
  
  
  // LOGIN REQUEST
  
  
  /**
   * Constructs an Agora protocol login request in BSON.
   * @param user
   * @param password
   * @return
   */
  protected BasicBSONObject constructLoginRequest(String user, String password) {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put(ACTION_FIELD, IJAgoraLib.LOGIN_ACTION);
    bson.put(USER_FIELD, user);
    bson.put(PASSWORD_FIELD, password);
    return bson;
  }
  
  /**
   * Tries to parse an Agora login response from BSON. Stores a session ID
   * that should be used in all further communications.
   * @param bson
   * @return
   */
  protected boolean parseLoginResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not login (" + bson.getString(REASON_FIELD) + ")");
      return false;
    }
    
    // Success!
    sessionID = bson.getString(SESSION_ID_FIELD);
    userID = bson.getInt(USER_ID_FIELD);
    return true;
  }
  
  /**
   * Performs a login with an Agora server.
   * @param user
   * @param password
   * @return
   */
  public boolean login(String user, String password) {
    // TODO: Can't differentiate between what happened. Make return int?
    Socket s = openConnection(hostname, port);
    if (s == null) {
      Log.error("[JAgoraLib] Could not connect because socket could not be opened.");
      return false;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s,
        constructLoginRequest(user, password));
    if (!success) {
      Log.error("[JAgoraLib] Could not send login message.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read login response.");
      return false;
    }
    success = parseLoginResponse(response);
    if (!success) {
      Log.error("[JAgoraLib] Wrong login information.");
      return false;
    }

    success = closeConnection(s);
    if (!success) {
      Log.error("[JAgoraLib] Problems closing login connection.");
      return false;
    }
    Log.debug("[JAgoraLib] Successful login for " + user);
    return true;
  }
  
  
  /**
   * Constructs an empty response based on this Lib's user ID and session ID.
   * @return Basic, empty response BSON object.
   */
  protected BasicBSONObject constructBasicRequest() {
    if (!isConnected())
      return null;
    BasicBSONObject request = new BasicBSONObject();
    request.put(SESSION_ID_FIELD, sessionID);
    request.put(USER_ID_FIELD, userID);
    return request;
  }
  
  
  // LOGOUT
  
  protected BasicBSONObject constructLogoutRequest() {
    BasicBSONObject bsonRequest = constructBasicRequest();
    bsonRequest.put(ACTION_FIELD, LOGOUT_ACTION);
    return bsonRequest;
  }
  
  protected boolean parseLogoutResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not logout (" + bson.getString(REASON_FIELD) + ")");
      return false;
    }
    
    userID = -1;
    sessionID = null;
    return true;
  }
  
  /**
   * Closes the current Agora session.
   * @return
   */
  public boolean logout() {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Logging out but isn't connected.");
      return false;
    }
    
    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for logout.");
      return false;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructLogoutRequest());
    if (!success) {
      Log.error("[JAgoraLib] Could not write logout query.");
      return false;
    }
    
    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read logout response.");
      return false;
    }
    
    success = parseLogoutResponse(response);
    if(!success){
      Log.error("[JAgoraLib] Could not parse logout response.");
      return false;
    }
    
    sessionID = null;
    hostname = null;
    return true;
  }
  
 

  
  
  // ADD ARGUMENT REQUEST
  
  
  protected BasicBSONObject constructaAddArgumentRequest(BasicBSONObject content, int threadID) {
    BasicBSONObject bsonRequest = constructBasicRequest(); // Contains user ID already
    bsonRequest.put(ACTION_FIELD, ADD_ARGUMENT_ACTION);
    bsonRequest.put(CONTENT_FIELD, content);
    bsonRequest.put(THREAD_ID_FIELD, threadID);
    return bsonRequest;
  }
  
  protected boolean parseAddArgumentResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not add argument (" + bson.getString(REASON_FIELD) + ")");
      return false;
    }
    
    return true;
  }
  
  
  @Override
  public boolean addArgument(BasicBSONObject content, int threadID) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return false;
    }
    
    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return false;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructaAddArgumentRequest(content, threadID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addArgument query.");
      return false;
    }
    
    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read addArgument response.");
      return false;
    }
    
    success = parseAddArgumentResponse(response);
    if(!success){
      Log.error("[JAgoraLib] Could not parse addArgument response.");
      return false;
    }
    
    return true;
  }
  
  
  protected BasicBSONObject constructaAddAttackRequest(JAgoraNodeID attacker, JAgoraNodeID defender) {
    BasicBSONObject bsonRequest = constructBasicRequest(); // Contains user ID already
    
    BSONGraphEncoder enc = new BSONGraphEncoder();
    
    bsonRequest.put(ACTION_FIELD, ADD_ATTACK_ACTION);
    bsonRequest.put(ATTACKER_FIELD, enc.BSONiseNodeID(attacker));
    bsonRequest.put(DEFENDER_FIELD, enc.BSONiseNodeID(defender));
    return bsonRequest;
  }
  
  protected boolean parseAddAttackResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not add attack (" + bson.getString(REASON_FIELD) + ")");
      return false;
    }
    
    return true;
  }
  
  
  @Override
  public boolean addAttack(JAgoraNodeID attacker, JAgoraNodeID defender) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return false;
    }
    
    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return false;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructaAddAttackRequest(attacker, defender));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addAttack query.");
      return false;
    }
    
    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read addAttack response.");
      return false;
    }
    
    success = parseAddAttackResponse(response);
    if(!success){
      Log.error("[JAgoraLib] Could not parse addAttack response.");
      return false;
    }
    
    return true;
  }
  
  
  
  // GET THREAD BY ID REQUEST
  
  protected BasicBSONObject constructGetThreadByIDRequest(int threadID) {
    BasicBSONObject bsonRequest = constructBasicRequest();
    bsonRequest.put(ACTION_FIELD, QUERY_BY_THREAD_ID_ACTION);
    bsonRequest.put(THREAD_ID_FIELD, threadID);
    return bsonRequest;
  }
  
  protected JAgoraGraph parseQueryByThreadIDResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not get thread (" + bson.getString(REASON_FIELD) + ")");
      return null;
    }
    
    JAgoraGraph g = decoder.deBSONiseGraph((BasicBSONObject)bson.get(GRAPH_FIELD));
    return g;
  }
  
  
  @Override
  public JAgoraGraph getThreadByID(int threadID) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return null;
    }
    
    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return null;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructGetThreadByIDRequest(threadID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write getThreadByID query.");
      return null;
    }
    
    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read getThreadByID response.");
      return null;
    }
    
    JAgoraGraph graph = parseQueryByThreadIDResponse(response);
    
    success = graph == null;
    if(!success){
      Log.error("[JAgoraLib] Could not parse getThreadByID response.");
      return null;
    }
    
    return graph;
  }
  
  
  public boolean isConnected() {
    return sessionID != null;
  }
}
