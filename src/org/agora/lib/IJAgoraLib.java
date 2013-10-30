package org.agora.lib;

public interface IJAgoraLib {
  boolean connect(String hostname, int port, String user, String password);
  boolean close();
  boolean isConnected();
}
