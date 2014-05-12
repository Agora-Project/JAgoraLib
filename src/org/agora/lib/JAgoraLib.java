package org.agora.lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNodeID;
import org.agora.graph.JAgoraThread;
import static org.agora.lib.IJAgoraLib.GRAPH_FIELD;
import static org.agora.lib.IJAgoraLib.REASON_FIELD;
import static org.agora.lib.IJAgoraLib.RESPONSE_FIELD;
import static org.agora.lib.IJAgoraLib.SERVER_FAIL;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraLib implements IJAgoraLib {  
  
  protected int userID;
  protected String sessionID;
  protected String hostname;
  protected int port;
  
  protected BSONGraphEncoder graphEncoder;
  protected BSONGraphDecoder graphDecoder;
  protected BSONThreadListEncoder threadListEncoder;
  protected BSONThreadListDecoder threadListDecoder;
  
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
  
//LOGIN REQUEST
  
  
 /**
  * Constructs an Agora protocol register request in BSON.
  * @param user
  * @param password
  * @return
  */
 protected BasicBSONObject constructRegisterRequest(String user, String password, String email) {
   BasicBSONObject bson = new BasicBSONObject();
   bson.put(ACTION_FIELD, IJAgoraLib.REGISTER_ACTION);
   bson.put(USER_FIELD, user);
   bson.put(PASSWORD_FIELD, password);
   bson.put(EMAIL_FIELD, email);
   return bson;
 }
 
 /**
  * Tries to parse an Agora register response from BSON.
  * @param bson
  * @return
  */
 protected boolean parseRegisterResponse(BasicBSONObject bson) {
   int response = bson.getInt(RESPONSE_FIELD);
   if (response == SERVER_FAIL) {
     Log.error("[JAgoraLib] Could not register (" + bson.getString(REASON_FIELD) + ")");
     return false;
   }
   
   return true;
 }
 
 /**
  * Performs a register request with an Agora server.
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
      Log.error("[JAgoraLib] Could not logout properly.");
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
      Log.error("[JAgoraLib] Could not addArgument.");
      return false;
    }
    
    return true;
  }
  
  
  // ADD ATTACK
  
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
      Log.error("[JAgoraLib] Could not addAttack.");
      return false;
    }
    
    return true;
  }
  
  
  
  
  
//ADD ARGUMENT VOTE REQUEST
  
 protected BasicBSONObject constructaAddArgumentVoteRequest(JAgoraNodeID nodeID, int voteType) {
   BasicBSONObject bsonRequest = constructBasicRequest(); // Contains user ID already
   bsonRequest.put(ACTION_FIELD, ADD_ARGUMENT_VOTE_ACTION);
   bsonRequest.put(VOTE_TYPE_FIELD, voteType);
   bsonRequest.put(ARGUMENT_ID_FIELD, new BSONGraphEncoder().BSONiseNodeID(nodeID));
   return bsonRequest;
 }
 
 protected boolean parseAddArgumentVoteResponse(BasicBSONObject bson) {
   int response = bson.getInt(RESPONSE_FIELD);
   if (response == SERVER_FAIL) {
     Log.error("[JAgoraLib] Could not add argument vote (" + bson.getString(REASON_FIELD) + ")");
     return false;
   }
   
   return true;
 }
 
 
 @Override
 public boolean addArgumentVote(JAgoraNodeID nodeID, int voteType) {
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
   if(!success){
     Log.error("[JAgoraLib] Could not addArgumentVote.");
     return false;
   }
   
   return true;
 }
 
 
 // ADD ATTACK VOTE 
 
 protected BasicBSONObject constructaAddAttackVoteRequest(JAgoraNodeID attacker, JAgoraNodeID defender, int voteType) {
   BasicBSONObject bsonRequest = constructBasicRequest(); // Contains user ID already
   
   BSONGraphEncoder enc = new BSONGraphEncoder();
   
   bsonRequest.put(ACTION_FIELD, ADD_ATTACK_VOTE_ACTION);
   bsonRequest.put(ATTACKER_FIELD, enc.BSONiseNodeID(attacker));
   bsonRequest.put(DEFENDER_FIELD, enc.BSONiseNodeID(defender));
   bsonRequest.put(VOTE_TYPE_FIELD, voteType);
   return bsonRequest;
 }
 
 protected boolean parseAddAttackVoteResponse(BasicBSONObject bson) {
   int response = bson.getInt(RESPONSE_FIELD);
   if (response == SERVER_FAIL) {
     Log.error("[JAgoraLib] Could not add attack vote (" + bson.getString(REASON_FIELD) + ")");
     return false;
   }
   
   return true;
 }
 
 
 @Override
 public boolean addAttackVote(JAgoraNodeID attacker, JAgoraNodeID defender, int voteType) {
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
   if(!success){
     Log.error("[JAgoraLib] Could not addAttack.");
     return false;
   }
   
   return true;
 }
  
  
  
  
  
  
  
  
  //GET THREAD LIST
 
  protected BasicBSONObject constructGetThreadListRequest() {
    BasicBSONObject bsonRequest = constructBasicRequest();
    bsonRequest.put(ACTION_FIELD, QUERY_THREAD_LIST_ACTION);
    return bsonRequest;
  }
  
  protected ArrayList<JAgoraThread> parseGetThreadListResponse(BasicBSONObject bson) {
      
      int response = bson.getInt(RESPONSE_FIELD);
      if (response == SERVER_FAIL) {
         Log.error("[JAgoraLib] Could not get thread (" + bson.getString(REASON_FIELD) + ")");
      return null;
      }
      ArrayList<JAgoraThread> threads = threadListDecoder.deBSONiseThreadList((BasicBSONObject) bson.get(THREAD_LIST_FIELD));
      return threads;
  }
  
  @Override
  public ArrayList<JAgoraThread> getThreadList() {
      
      if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return null;
    }
    
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
    if(!success){
      Log.error("[JAgoraLib] Could not getThreadList.");
      return null;
    }
      return threads;
      
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
    
    JAgoraGraph g = graphDecoder.deBSONiseGraph((BasicBSONObject)bson.get(GRAPH_FIELD));
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
    
    success = graph != null;
    if(!success){
      Log.error("[JAgoraLib] Could not getThreadByID.");
      return null;
    }
    
    return graph;
  }
  
  
  public boolean isConnected() {
    return sessionID != null;
  }
}
