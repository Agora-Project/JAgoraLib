package org.agora.lib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.agora.graph.JAgoraArgumentID;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraThread;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

/**
 *
 * @author angle
 */
public class JAgoraHTTPLib extends IJAgoraLib {

  private URL target;

  public JAgoraHTTPLib(URL target) {
    super();
    this.target = target;
  }

  public HttpURLConnection openConnection() {
    try {
      HttpURLConnection connection = (HttpURLConnection) target.openConnection();
      connection.setDoOutput(true);
      return connection;
    } catch (IOException ex) {
      Log.error("[JAgoraHTTPLib] HTTP connection failed: " + ex.getMessage());
    }
    return null;
  }

  @Override
  boolean login(String user, String password) {
    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because socket could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection,
            constructLoginRequest(user, password));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not send login message.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read login response.");
      return false;
    }
    success = parseLoginResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Wrong login information.");
      return false;
    }

//    success = closeConnection(s);
//    if (!success) {
//      Log.error("[JAgoraLib] Problems closing login connection.");
//      return false;
//    }
    Log.debug("[JAgoraHTTPLib] Successful login for " + user);
    return true;
  }
  
  /**
   * Performs a register request with an Agora server.
   *
   * @param user
   * @param password
   * @return
   */
  public boolean register(String user, String password, String email) {
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Registering but isn't connected.");
      return false;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because socket could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, 
            constructRegisterRequest(user, password, email));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write register query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read register response.");
      return false;
    }
    
    success = parseRegisterResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Failed to register. Perhaps a taken username?");
      return false;
    }
    
    Log.debug("[JAgoraHTTPLib] Successful registration for " + user);
    return true;
  }

  @Override
  boolean logout() {
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Logging out but isn't connected.");
      return false;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because socket could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructLogoutRequest());
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write logout query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read logout response.");
      return false;
    }

    success = parseLogoutResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not logout properly.");
      return false;
    }

    sessionID = null;
    hostname = null;
    return true;
  }

  @Override
  boolean isConnected() {
    return sessionID != null;
  }

  @Override
  JAgoraArgumentID addArgument(BasicBSONObject content, int threadID) {
    JAgoraArgumentID ret = null;
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Querying but not connected.");
      return null;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructaAddArgumentRequest(content, threadID));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write addArgument query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read addArgument response.");
      return null;
    }

    ret = parseAddArgumentResponse(response);
    if (ret == null) {
      Log.error("[JAgoraHTTPLib] Could not addArgument.");
      return null;
    }

    return ret;
  }

  @Override
  boolean addAttack(JAgoraArgumentID attacker, JAgoraArgumentID defender) {
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Querying but not connected.");
      return false;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructaAddAttackRequest(attacker, defender));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write addAttack query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read addAttack response.");
      return false;
    }

    success = parseAddAttackResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not addAttack.");
      return false;
    }

    return true;
  }

  @Override
  boolean addArgumentWithAttacks(BasicBSONObject content, int threadID, ArrayList<JAgoraArgumentID> defenders) {
    JAgoraArgumentID ref = addArgument(content, threadID);
    for (JAgoraArgumentID defender : defenders) {
      addAttack(ref, defender);
    }

    return (ref != null);
  }

  @Override
  boolean editArgument(BasicBSONObject content, JAgoraArgumentID id) {
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Querying but not connected.");
      return false;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructEditArgumentRequest(content, id));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write editArgument query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read editArgument response.");
      return false;
    }

    success = parseEditArgumentResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not editArgument.");
      return false;
    }

    return true;
  }

  @Override
  boolean addArgumentVote(JAgoraArgumentID nodeID, int voteType) {
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Querying but not connected.");
      return false;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructaAddArgumentVoteRequest(nodeID, voteType));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write addArgumentVote query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read addArgumentVote response.");
      return false;
    }

    success = parseAddArgumentVoteResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not addArgumentVote.");
      return false;
    }

    return true;
  }

  @Override
  boolean addAttackVote(JAgoraArgumentID attacker, JAgoraArgumentID defender, int voteType) {
    if (!isConnected()) {
      Log.error("[JAgoraHTTPLib] Querying but not connected.");
      return false;
    }

    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructaAddAttackVoteRequest(attacker, defender, voteType));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write addAttack query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read addAttack response.");
      return false;
    }

    success = parseAddAttackVoteResponse(response);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not addAttack.");
      return false;
    }

    return true;
  }

  @Override
  JAgoraGraph getThreadByID(int threadID) {
    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructGetThreadByIDRequest(threadID));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write getThreadByID query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read getThreadByID response.");
      return null;
    }

    JAgoraGraph graph = parseQueryByThreadIDResponse(response);

    success = graph != null;
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not getThreadByID.");
      return null;
    }

    return graph;
  }

  @Override
  ArrayList<JAgoraThread> getThreadList() {
    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructGetThreadListRequest());
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write getThreadList query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read getThreadList response.");
      return null;
    }

    ArrayList<JAgoraThread> threads = parseGetThreadListResponse(response);

    success = threads != null;
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not getThreadList.");
      return null;
    }
    return threads;
  }

  @Override
  public JAgoraGraph getThreadByArgumentID(JAgoraArgumentID id) {
    HttpURLConnection connection = openConnection();
    if (connection == null) {
      Log.error("[JAgoraHTTPLib] Could not connect because connection could not be opened.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToHTTPConnection(connection, constructGetThreadByArgumentIDRequest(id));
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not write getThreadByArgumentID query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromHTTPConnection(connection);
    if (response == null) {
      Log.error("[JAgoraHTTPLib] Could not read getThreadByArgumentID response.");
      return null;
    }

    JAgoraGraph graph = parseQueryByArgumentIDResponse(response);

    success = (graph != null);
    if (!success) {
      Log.error("[JAgoraHTTPLib] Could not getThreadByArgumentID.");
      return null;
    }

    return graph;
  }

}
