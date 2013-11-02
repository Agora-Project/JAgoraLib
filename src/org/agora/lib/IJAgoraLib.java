package org.agora.lib;

public interface IJAgoraLib {
  // Communication codes!
  // Private constants for constructing messages.
  public final static int LOGIN_ACTION = 0;
  public final static int LOGOUT_ACTION = 1;
  
  public final static String USER_ID_FIELD = "id";
  public final static String SESSION_ID_FIELD = "sid";
  public final static String ACTION_FIELD = "act";
  public final static String USER_FIELD = "usr";
  public final static String PASSWORD_FIELD = "pwd";
  public final static String RESPONSE_FIELD = "r";
  public final static String REASON_FIELD = "rs";
  
  // Private constants for deconstructing server messages
  public final static int SERVER_OK = 0;
  public final static int SERVER_FAIL = 1;
  
  boolean login(String user, String password);
  boolean logout();
  boolean isConnected();
}
