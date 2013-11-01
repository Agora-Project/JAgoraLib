package org.agora.lib;

public interface IJAgoraLib {
  // Communication codes!
  // Private constants for constructing messages.
  public final static int LOGIN_ACTION = 0;
  public final static int LOGOUT_ACTION = 1;
  
  // Private constants for deconstructing server messages
  public final static int SERVER_OK = 0;
  public final static int SERVER_FAIL = 1;
  
  boolean connect(String user, String password);
  boolean close();
  boolean isConnected();
}
