package org.agora.lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraLib implements IJAgoraLib {  
  
  protected String sessionID;
  protected String hostname;
  protected int port;

  public JAgoraLib() {
    sessionID = null;
  }
  
  
  
  private Socket openConnection() {
    return openConnection(hostname, port);
  }
  
  private Socket openConnection(String hostname, int port) {
    Socket s = null;
    try {
      s = new Socket(hostname, port);
    } catch (UnknownHostException e) {
      Log.error("[JAgoraLib] Could not identify host.");
      Log.error(e.getMessage());
    } catch (IOException e) {
      Log.error("[JAgoraLib] Error opening connection to " + hostname + ":"+port);
      Log.error(e.getMessage());
    }
    return s;
  }
  
  private boolean closeConnection(Socket s) {
    try {
      s.close();
      return true;
    } catch (IOException e) {
      Log.error("[JAgoraLib] Could not close connection " + s);
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
    bson.put("action", JAgoraComms.LOGIN_ACTION);
    bson.put("user", user);
    bson.put("pass", password);
    return bson;
  }
  
  /**
   * Tries to parse an Agora login response from BSON. Stores a session ID
   * that should be used in all further communications.
   * @param bson
   * @return
   */
  private boolean parseLoginResponse(BasicBSONObject bson) {
    boolean success = (Boolean) bson.get("success");
    if (!success)
      return false;
    // Success!
    sessionID = (String)bson.get("sessionID");
    return sessionID != null;
  }
  
  /**
   * Performs a login with an Agora server.
   * @param hostname The Agora server.
   * @param port The port on which the server is listening.
   * @param user
   * @param password
   * @return
   */
  public boolean connect(String hostname, int port, String user, String password) {
    // TODO: Can't differentiate between what happened. Make return int?
    Socket s = openConnection(hostname, port);
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
  
  
  
  
  private BasicBSONObject constructLogoffRequest() {
    BasicBSONObject bson = new BasicBSONObject();
    bson.put("action", JAgoraComms.LOGOUT_ACTION);
    bson.put("session", sessionID);
    return bson;
  }
  
  private boolean parseLogoffResponse(BasicBSONObject bson) {
    return (Integer)bson.get("response") == JAgoraComms.SERVER_OK;
  }
  
  /**
   * Closes the current Agora session.
   * @return
   */
  public boolean close() {
    if (!isConnected()) {
      return false;
    }
    
    Socket s = openConnection();
    if (s != null) {
      Log.error("... in JAgoraLib.parseLogoffResponse.");
      return false;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructLogoffRequest());
    if (!success) {
      Log.error("... in JAgoraLib.close().");
      return false;
    }
    
    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response != null) {
      Log.error("... in JAgoraLib.close().");
      return false;
    }
    
    success = parseLogoffResponse(response);
    if(!success){
      Log.error("... in JAgoraLib.close().");
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
