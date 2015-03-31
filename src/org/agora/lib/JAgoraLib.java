package org.agora.lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraArgumentID;
import org.agora.graph.JAgoraThread;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraLib extends IJAgoraLib {

  /**
   * @param hostname The Agora server location.
   * @param port The port on which the server is listening.
   */
  public JAgoraLib(String hostname, int port) {
    userID = -1;
    sessionID = null;
    this.hostname = hostname;
    this.port = port;

    graphEncoder = new BSONGraphEncoder();
    graphDecoder = new BSONGraphDecoder();

    threadListEncoder = new BSONThreadListEncoder();
    threadListDecoder = new BSONThreadListDecoder();
  }

  protected Socket openConnection() {
    return openConnection(hostname, port);
  }

  protected Socket openConnection(String hostname, int port) {
    Socket s = null;
    try {
      s = new Socket(hostname, port);
    } catch (UnknownHostException e) {
      Log.error("[JAgoraLib] Could not identify host " + "(" + e.getMessage() + ")");
    } catch (IOException e) {
      Log.error("[JAgoraLib] Error opening connection to " + hostname + ":" + port + " (" + e.getMessage() + ")");
    }
    return s;
  }

  protected boolean closeConnection(Socket s) {
    try {
      s.close();
      return true;
    } catch (IOException e) {
      Log.error("[JAgoraLib] Could not close connection " + s + " (" + e.getMessage() + ")");
    }
    return false;
  }

  /**
   * Performs a login with an Agora server.
   *
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
   * Performs a register request with an Agora server.
   *
   * @param user
   * @param password
   * @return
   */
  public boolean register(String user, String password, String email) {
    Socket s = openConnection(hostname, port);
    if (s == null) {
      Log.error("[JAgoraLib] Could not connect because socket could not be opened.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructRegisterRequest(user, password, email));
    if (!success) {
      Log.error("[JAgoraLib] Could not send register message.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read register response.");
      return false;
    }
    success = parseRegisterResponse(response);
    if (!success) {
      Log.error("[JAgoraLib] Failed to register. Perhaps a taken username?");
      return false;
    }

    success = closeConnection(s);
    if (!success) {
      Log.error("[JAgoraLib] Problems closing registration connection.");
      return false;
    }
    Log.debug("[JAgoraLib] Successful registration for " + user);
    return true;
  }

  /**
   * Closes the current Agora session.
   *
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
    if (!success) {
      Log.error("[JAgoraLib] Could not logout properly.");
      return false;
    }

    sessionID = null;
    hostname = null;
    return true;
  }

  @Override
  public JAgoraArgumentID addArgument(BasicBSONObject content, int threadID) {
    JAgoraArgumentID ret = null;
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return null;
    }

    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructaAddArgumentRequest(content, threadID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addArgument query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read addArgument response.");
      return null;
    }

    ret = parseAddArgumentResponse(response);
    if (ret == null) {
      Log.error("[JAgoraLib] Could not addArgument.");
      return null;
    }

    return ret;
  }

  @Override
  public boolean addAttack(JAgoraArgumentID attacker, JAgoraArgumentID defender) {
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
    if (!success) {
      Log.error("[JAgoraLib] Could not addAttack.");
      return false;
    }

    return true;
  }

  //ADD ARGUMENT WITH ATTACKS
  @Override
  public JAgoraArgumentID addArgumentWithAttacks(BasicBSONObject content, int threadID, ArrayList<JAgoraArgumentID> defenders) {

    JAgoraArgumentID ref = addArgument(content, threadID);
    for (JAgoraArgumentID defender : defenders) {
      addAttack(ref, defender);
    }

    return ref;
  }

  @Override
  public boolean editArgument(BasicBSONObject content, JAgoraArgumentID nodeID) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return false;
    }

    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for edit argument query.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructEditArgumentRequest(content, nodeID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write editArgument query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read editArgument response.");
      return false;
    }

    success = parseEditArgumentResponse(response);
    if (!success) {
      Log.error("[JAgoraLib] Could not editArgument.");
      return false;
    }

    return true;
  }

  @Override
  public boolean addArgumentVote(JAgoraArgumentID nodeID, int voteType) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return false;
    }

    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for argument vote query.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructaAddArgumentVoteRequest(nodeID, voteType));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addArgumentVote query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read addArgumentVote response.");
      return false;
    }

    success = parseAddArgumentVoteResponse(response);
    if (!success) {
      Log.error("[JAgoraLib] Could not addArgumentVote.");
      return false;
    }

    return true;
  }

  @Override
  public boolean addAttackVote(JAgoraArgumentID attacker, JAgoraArgumentID defender, int voteType) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return false;
    }

    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructaAddAttackVoteRequest(attacker, defender, voteType));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addAttack query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read addAttack response.");
      return false;
    }

    success = parseAddAttackVoteResponse(response);
    if (!success) {
      Log.error("[JAgoraLib] Could not addAttack.");
      return false;
    }

    return true;
  }

  @Override
  public ArrayList<JAgoraThread> getThreadList() {

//      if (!isConnected()) {
//      Log.error("[JAgoraLib] Querying but not connected.");
//      return null;
//    }
    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructGetThreadListRequest());
    if (!success) {
      Log.error("[JAgoraLib] Could not write getThreadList query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read getThreadList response.");
      return null;
    }

    ArrayList<JAgoraThread> threads = parseGetThreadListResponse(response);

    success = threads != null;
    if (!success) {
      Log.error("[JAgoraLib] Could not getThreadList.");
      return null;
    }
    return threads;

  }

  @Override
  public JAgoraGraph getThreadByID(int threadID) {
//    if (!isConnected()) {
//      Log.error("[JAgoraLib] Querying but not connected.");
//      return null;
//    }

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

    success = graph != null;
    if (!success) {
      Log.error("[JAgoraLib] Could not getThreadByID.");
      return null;
    }

    return graph;
  }

  public boolean isConnected() {
    return sessionID != null;
  }

  @Override
  public JAgoraGraph getThreadByArgumentID(JAgoraArgumentID id) {
    Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for thread query.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructGetThreadByArgumentIDRequest(id));
    if (!success) {
      Log.error("[JAgoraLib] Could not write getThreadByID query.");
      return null;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read getThreadByID response.");
      return null;
    }

    JAgoraGraph graph = parseQueryByArgumentIDResponse(response);

    success = (graph != null);
    if (!success) {
      Log.error("[JAgoraLib] Could not getThreadByID.");
      return null;
    }

    return graph;
  }

  @Override
  boolean deleteArgument(JAgoraArgumentID id) {
     Socket s = openConnection();
    if (s == null) {
      Log.error("[JAgoraLib] Could not open connection for deleteArgument query.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToSocket(s, constructDeleteArgumentQuery(id));
    if (!success) {
      Log.error("[JAgoraLib] Could not write deleteArgument query.");
      return false;
    }

    BasicBSONObject response = JAgoraComms.readBSONObjectFromSocket(s);
    if (response == null) {
      Log.error("[JAgoraLib] Could not read deleteArgument response.");
      return false;
    }

    return parseDeleteArgumentResponse(response);
  }
}
