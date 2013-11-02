package org.agora.lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraLib implements IJAgoraLib {  
  
  protected int userID;
  protected String sessionID;
  protected String hostname;
  protected int port;
  /**
   * @param hostname The Agora server location.
   * @param port The port on which the server is listening.
   */
  public JAgoraLib(String hostname, int port) {
    userID = -1;
    sessionID = null;
    this.hostname = hostname;
    this.port = port;
  }
  
  private Socket openConnection() {
    return openConnection(hostname, port);
  }
  
  private Socket openConnection(String hostname, int port) {
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
  
  private boolean closeConnection(Socket s) {
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
  private BasicBSONObject constructLoginRequest(String user, String password) {
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
  private boolean parseLoginResponse(BasicBSONObject bson) {
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
  private BasicBSONObject constructBasicResponse() {
    BasicBSONObject response = new BasicBSONObject();
    response.put(SESSION_ID_FIELD, sessionID);
    response.put(USER_ID_FIELD, userID);
    return response;
  }
  
  private BasicBSONObject constructLogoutRequest() {
    BasicBSONObject bsonResponse = constructBasicResponse();
    bsonResponse.put(ACTION_FIELD, LOGOUT_ACTION);
    return bsonResponse;
  }
  
  private boolean parseLogoutResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not logout (" + bson.getString("reason") + ")");
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
    // TODO: fix the error messages.
    if (!isConnected()) {
      return false;
    }
    
    Socket s = openConnection();
    if (s == null) {
      Log.error("... in JAgoraLib.parseLogoffResponse.");
      return false;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructLogoutRequest());
    if (!success) {
      Log.error("... in writing ");
      return false;
    }
    
    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("... in reading");
      return false;
    }
    
    success = parseLogoutResponse(response);
    if(!success){
      Log.error("... in JAgoraLib.things");
      return false;
    }
    
    sessionID = null;
    hostname = null;
    return true;
  }
  
  
  public boolean isConnected() {
    return sessionID != null;
  }
}
