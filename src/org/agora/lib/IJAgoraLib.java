package org.agora.lib;

import java.util.ArrayList;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraArgumentID;
import org.agora.graph.JAgoraThread;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public abstract class IJAgoraLib {

  // Communication codes!
  // Private constants for constructing messages.

  public final static int LOGIN_ACTION = 0;
  public final static int LOGOUT_ACTION = 1;
  public final static int QUERY_BY_THREAD_ID_ACTION = 2;
  public final static int ADD_ARGUMENT_ACTION = 3;
  public final static int ADD_ATTACK_ACTION = 4;
  public final static int ADD_ARGUMENT_VOTE_ACTION = 5;
  public final static int ADD_ATTACK_VOTE_ACTION = 6;
  public final static int REGISTER_ACTION = 7;
  public final static int QUERY_THREAD_LIST_ACTION = 8;
  public final static int EDIT_ARGUMENT_ACTION = 9;
  public final static int QUERY_BY_ARGUMENT_ID_ACTION = 10;
  public static final int DELETE_ARGUMENT_ACTION = 11;

  public final static String USER_ID_FIELD = "id";
  public final static String USER_TYPE_FIELD = "utp";
  public final static String SESSION_ID_FIELD = "sid";
  public final static String ACTION_FIELD = "act";
  public final static String USER_FIELD = "usr";
  public final static String PASSWORD_FIELD = "pwd";
  public final static String RESPONSE_FIELD = "r";
  public final static String REASON_FIELD = "rs";
  public final static String THREAD_ID_FIELD = "tID";
  public final static String GRAPH_FIELD = "g";
  public final static String THREAD_LIST_FIELD = "t";
  public final static String CONTENT_FIELD = "c";
  public final static String ATTACKER_FIELD = "att";
  public final static String DEFENDER_FIELD = "def";
  public final static String ARGUMENT_ID_FIELD = "aid";
  public final static String VOTE_TYPE_FIELD = "vt";
  public final static String EMAIL_FIELD = "@";

  // Content data
  public final static String TEXT_FIELD = "txt";

  // Private constants for deconstructing server messages
  public final static int SERVER_OK = 0;
  public final static int SERVER_FAIL = 1;

  protected int userID;
  protected int userType;
  protected String sessionID;
  protected String hostname;
  protected int port;

  protected BSONGraphEncoder graphEncoder;
  protected BSONGraphDecoder graphDecoder;
  protected BSONThreadListEncoder threadListEncoder;
  protected BSONThreadListDecoder threadListDecoder;

  public IJAgoraLib() {
    userID = -1;
    sessionID = null;

    graphEncoder = new BSONGraphEncoder();
    graphDecoder = new BSONGraphDecoder();

    threadListEncoder = new BSONThreadListEncoder();
    threadListDecoder = new BSONThreadListDecoder();
  }

  // LOGIN REQUEST
  /**
   * Constructs an Agora protocol login request in BSON.
   *
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
   * Tries to parse an Agora login response from BSON. Stores a session ID that
   * should be used in all further communications.
   *
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
    userType = bson.getInt(USER_TYPE_FIELD);
    return true;
  }

  /**
   * Connects to the server and logs the user in.
   *
   * @param user
   * @param password
   * @return
   */
  abstract boolean login(String user, String password);

  //LOGIN REQUEST
  /**
   * Constructs an Agora protocol register request in BSON.
   *
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
   *
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
  
  abstract boolean register(String user, String password, String email);

  /**
   * Constructs an empty response.
   *
   * @return Basic, empty response BSON object.
   */
  protected BasicBSONObject constructBasicRequest() {
    BasicBSONObject request = new BasicBSONObject();
    request.put(USER_ID_FIELD, 0);
    return request;
  }

  /**
   * Constructs an empty response based on this Lib's user ID and session ID.
   *
   * @return Basic, empty response BSON object.
   */
  protected BasicBSONObject constructBasicSessionRequest() {
    if (!isConnected()) {
      return null;
    }
    BasicBSONObject request = new BasicBSONObject();
    request.put(SESSION_ID_FIELD, sessionID);
    request.put(USER_ID_FIELD, userID);
    return request;
  }

  // LOGOUT
  protected BasicBSONObject constructLogoutRequest() {
    BasicBSONObject bsonRequest = constructBasicSessionRequest();
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
   * Logs the user out, if he is logged in.
   *
   * @return
   */
  abstract boolean logout();

  /**
   * Checks whether there is an ongoing session.
   *
   * @return
   */
  abstract boolean isConnected();

 // ADD ARGUMENT REQUEST
  protected BasicBSONObject constructaAddArgumentRequest(BasicBSONObject content, int threadID) {
    BasicBSONObject bsonRequest = constructBasicSessionRequest(); // Contains user ID already
    bsonRequest.put(ACTION_FIELD, ADD_ARGUMENT_ACTION);
    bsonRequest.put(CONTENT_FIELD, content);
    bsonRequest.put(THREAD_ID_FIELD, threadID);
    return bsonRequest;
  }

  protected JAgoraArgumentID parseAddArgumentResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not add argument (" + bson.getString(REASON_FIELD) + ")");
      return null;
    }

    BasicBSONObject ret = (BasicBSONObject) bson.get(ARGUMENT_ID_FIELD);

    return new JAgoraArgumentID(ret.getString("Source"), ret.getInt("ID"));
  }

  abstract JAgoraArgumentID addArgument(BasicBSONObject content, int threadID);

  // ADD ATTACK
  protected BasicBSONObject constructaAddAttackRequest(JAgoraArgumentID attacker, JAgoraArgumentID defender) {
    BasicBSONObject bsonRequest = constructBasicSessionRequest(); // Contains user ID already

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

  abstract boolean addAttack(JAgoraArgumentID attacker, JAgoraArgumentID defender);

  abstract JAgoraArgumentID addArgumentWithAttacks(BasicBSONObject content, int threadID, ArrayList<JAgoraArgumentID> defenders);

  //EDIT ARGUMENT
  protected BasicBSONObject constructEditArgumentRequest(BasicBSONObject content, JAgoraArgumentID nodeID) {
    BasicBSONObject bsonRequest = constructBasicSessionRequest();
    bsonRequest.put(ACTION_FIELD, EDIT_ARGUMENT_ACTION);
    bsonRequest.put(ARGUMENT_ID_FIELD, new BSONGraphEncoder().BSONiseNodeID(nodeID));
    bsonRequest.put(CONTENT_FIELD, content);
    return bsonRequest;
  }

  protected boolean parseEditArgumentResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not edit argument (" + bson.getString(REASON_FIELD) + ")");
      return false;
    }

    return true;
  }

  abstract boolean editArgument(BasicBSONObject content, JAgoraArgumentID id);

  //ADD ARGUMENT VOTE REQUEST
  protected BasicBSONObject constructaAddArgumentVoteRequest(JAgoraArgumentID nodeID, int voteType) {
    BasicBSONObject bsonRequest = constructBasicSessionRequest(); // Contains user ID already
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

  abstract boolean addArgumentVote(JAgoraArgumentID nodeID, int voteType);

  // ADD ATTACK VOTE 
  protected BasicBSONObject constructaAddAttackVoteRequest(JAgoraArgumentID attacker, JAgoraArgumentID defender, int voteType) {
    BasicBSONObject bsonRequest = constructBasicSessionRequest(); // Contains user ID already

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

  abstract boolean addAttackVote(JAgoraArgumentID attacker, JAgoraArgumentID defender, int voteType);

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
      Log.error("[JAgoraLib] Could not get thread by ID (" + bson.getString(REASON_FIELD) + ")");
      return null;
    }

    JAgoraGraph g = graphDecoder.deBSONiseGraph((BasicBSONObject) bson.get(GRAPH_FIELD));
    return g;
  }

  /**
   * Connects to the database and obtains the graph associated with the given
   * thread ID. This includes all arguments proposed in that thread, as well as
   * attacks in which at least one of the intervening arguments is in the
   * thread. Arguments mentioned in Edges but not part of the thread will only
   * get minor information. For more information, request their thread.
   *
   * @param threadID The thread id.
   * @return The graph representing the thread.
   */
  abstract JAgoraGraph getThreadByID(int threadID);

  //GET THREAD LIST
  protected BasicBSONObject constructGetThreadListRequest() {
    BasicBSONObject bsonRequest = constructBasicRequest();
    bsonRequest.put(ACTION_FIELD, QUERY_THREAD_LIST_ACTION);
    return bsonRequest;
  }

  protected ArrayList<JAgoraThread> parseGetThreadListResponse(BasicBSONObject bson) {

    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not get thread list (" + bson.getString(REASON_FIELD) + ")");
      return null;
    }
    ArrayList<JAgoraThread> threads = threadListDecoder.deBSONiseThreadList((BasicBSONObject) bson.get(THREAD_LIST_FIELD));
    return threads;
  }

  abstract ArrayList<JAgoraThread> getThreadList();

  protected BasicBSONObject constructGetThreadByArgumentIDRequest(JAgoraArgumentID id) {
    BSONGraphEncoder enc = new BSONGraphEncoder();
    BasicBSONObject bsonRequest = constructBasicRequest();
    bsonRequest.put(ACTION_FIELD, QUERY_BY_ARGUMENT_ID_ACTION);
    bsonRequest.put(ARGUMENT_ID_FIELD, enc.BSONiseNodeID(id));
    return bsonRequest;
  }

  protected JAgoraGraph parseQueryByArgumentIDResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not get thread by argument ID(" + bson.getString(REASON_FIELD) + ")");
      return null;
    }

    JAgoraGraph g = graphDecoder.deBSONiseGraph((BasicBSONObject) bson.get(GRAPH_FIELD));
    return g;
  }

  abstract JAgoraGraph getThreadByArgumentID(JAgoraArgumentID id);
  
  protected BasicBSONObject constructDeleteArgumentQuery(JAgoraArgumentID id) {
    BSONGraphEncoder enc = new BSONGraphEncoder();
    BasicBSONObject bsonRequest = constructBasicSessionRequest();
    bsonRequest.put(ACTION_FIELD, DELETE_ARGUMENT_ACTION);
    bsonRequest.put(ARGUMENT_ID_FIELD, enc.BSONiseNodeID(id));
    return bsonRequest;
  }
  
  protected boolean parseDeleteArgumentResponse(BasicBSONObject bson) {
    int response = bson.getInt(RESPONSE_FIELD);
    if (response == SERVER_FAIL) {
      Log.error("[JAgoraLib] Could not delete argument (" + bson.getString(REASON_FIELD) + ")");
      return false;
    }
    return true;
  }
  
  abstract boolean deleteArgument(JAgoraArgumentID id);
}
