package org.agora.lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.agora.graph.JAgoraEdge;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraLib<G extends JAgoraGraph, N extends JAgoraNode, E extends JAgoraEdge> implements IJAgoraLib<G, N, E> {  
  
  protected int userID;
  protected String sessionID;
  protected String hostname;
  protected int port;
  
  protected BSONGraphEncoder encoder;
  protected BSONGraphDecoder<G, N, E> decoder;
  
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
    decoder = new BSONGraphDecoder<G, N, E>();
  }
  
  public void setEncoder(BSONGraphEncoder encoder) { this.encoder = encoder; }
  public void setDecoder(BSONGraphDecoder<G, N, E> decoder) { this.decoder = decoder; }
  
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
  
  
  public boolean isConnected() {
    return sessionID != null;
  }

  
  
  
  
  
  protected BasicBSONObject constructGetThreadByIDRequest(int threadID) {
    BasicBSONObject bsonRequest = constructBasicRequest();
    bsonRequest.put(ACTION_FIELD, QUERY_BY_THREAD_ID_ACTION);
    bsonRequest.put(QUERY_ID_FIELD, threadID);
    return bsonRequest;
  }
  
  protected G parseQueryByThreadIDResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not get thread (" + bson.getString(REASON_FIELD) + ")");
      return null;
    }
    
    G g = decoder.deBSONiseGraph((BasicBSONObject)bson.get(GRAPH_FIELD));
    return g;
  }
  
  
  @Override
  public G getThreadByID(int threadID) {
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
    
    G graph = parseQueryByThreadIDResponse(response);
    
    success = graph == null;
    if(!success){
      Log.error("[JAgoraLib] Could not parse getThreadByID response.");
      return null;
    }
    
    return graph;
  }
}
