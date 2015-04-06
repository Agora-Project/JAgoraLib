/*

Copyright (C) 2015 Agora Communication Corporation

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package org.agora.lib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import org.agora.graph.JAgoraArgumentID;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraThread;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

/**
 *
 * @author angle
 */

//This class was never properly finished, and does not yet work correctly.
public abstract class JAgoraWebsocketLib extends IJAgoraLib {

  private final ClientEndpointConfig cec;
  private final BlockingQueue<byte[]> messages;
  private URI target;
  private ClientEndpoint client;

  public JAgoraWebsocketLib(URI target) {
    cec = ClientEndpointConfig.Builder.create().build();
    messages = new LinkedBlockingQueue<>();
    this.target = target;
  }

  public DataInputStream getStream() throws InterruptedException {
    return new DataInputStream(new ByteArrayInputStream(messages.poll(10, TimeUnit.SECONDS)));
  }

  public void openConnection() {
    if (client == null || !client.getConnection().isOpen()) {
      client = new ClientEndpoint(target, new Draft_17());
    }
  }

  @Override
  public boolean login(String user, String password) {
    openConnection();

    if (client == null) {
      Log.error("[JAgoraLib] Could not login because there was no client open.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client,
            constructLoginRequest(user, password));
    if (!success) {
      Log.error("[JAgoraLib] Could not send login message.");
      return false;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (Exception ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
    if (response == null) {
      Log.error("[JAgoraLib] Could not read login response.");
      return false;
    }
    success = parseLoginResponse(response);
    if (!success) {
      Log.error("[JAgoraLib] Wrong login information.");
      return false;
    }

    Log.debug("[JAgoraLib] Successful login for " + user);
    return true;
  }

  @Override
  public boolean logout() {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Logging out but isn't connected.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructLogoutRequest());
    if (!success) {
      Log.error("[JAgoraLib] Could not write logout query.");
      return false;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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
  public boolean isConnected() {
    return (sessionID != null && client != null && client.getConnection().isOpen());
  }

  @Override
  public JAgoraArgumentID addArgument(BasicBSONObject content, int threadID) {
    JAgoraArgumentID ret = null;
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return null;
    }

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructaAddArgumentRequest(content, threadID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addArgument query.");
      return null;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {

    }
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

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructaAddAttackRequest(attacker, defender));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addAttack query.");
      return false;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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

  @Override
  public JAgoraArgumentID addArgumentWithAttacks(BasicBSONObject content, int threadID, ArrayList<JAgoraArgumentID> defenders) {
    JAgoraArgumentID ref = addArgument(content, threadID);
    for (JAgoraArgumentID defender : defenders) {
      addAttack(ref, defender);
    }

    return ref;
  }

  @Override
  public boolean editArgument(BasicBSONObject content, JAgoraArgumentID id) {
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return false;
    }

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructEditArgumentRequest(content, id));
    if (!success) {
      Log.error("[JAgoraLib] Could not write editArgument query.");
      return false;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructaAddArgumentVoteRequest(nodeID, voteType));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addArgumentVote query.");
      return false;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructaAddAttackVoteRequest(attacker, defender, voteType));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addAttack query.");
      return false;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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
  public JAgoraGraph getThreadByID(int threadID) {
    openConnection();

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructGetThreadByIDRequest(threadID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write getThreadByID query.");
      return null;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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

  @Override
  public ArrayList<JAgoraThread> getThreadList() {
    openConnection();

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructGetThreadListRequest());
    if (!success) {
      Log.error("[JAgoraLib] Could not write getThreadList query.");
      return null;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
    if (response == null) {
      Log.error("[JAgoraLib] Could not read getThreadList response.");
      return null;
    }

    ArrayList<JAgoraThread> threads = parseGetThreadListResponse(response);

    success = (threads != null);
    if (!success) {
      Log.error("[JAgoraLib] Could not getThreadList.");
      return null;
    }
    return threads;
  }

  @Override
  public JAgoraGraph getThreadByArgumentID(JAgoraArgumentID id) {
    openConnection();

    boolean success = JAgoraComms.writeBSONObjectToWebSocket(client, constructGetThreadByArgumentIDRequest(id));
    if (!success) {
      Log.error("[JAgoraLib] Could not write getThreadByID query.");
      return null;
    }

    BasicBSONObject response = null;
    try {
      response = JAgoraComms.readBSONObjectFromStream(getStream());
    } catch (InterruptedException | IOException ex) {
      Log.error("[JAgoraLib] Could not read from stream: " + ex.getMessage());
    }
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

  public class ClientEndpoint extends WebSocketClient {

    public ClientEndpoint(URI serverURI, Draft draft) {
      super(serverURI, draft);
    }

    @Override
    public void onOpen(ServerHandshake sh) {
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
      messages.add(bytes.array());
    }

    @Override
    public void onClose(int i, String string, boolean bln) {

    }

    @Override
    public void onError(Exception excptn) {
      Log.error("lJAgoraLib] WebSocketClient encountered error: " + excptn.getMessage());
    }

    @Override
    public void onMessage(String string) {
      Log.error("lJAgoraLib] WebSocketClient encountered message: " + string);
    }

  }

}
