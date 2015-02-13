
package org.agora.lib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.agora.graph.JAgoraArgumentID;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraThread;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;
import org.glassfish.tyrus.client.ClientManager;

/**
 *
 * @author angle
 */


public class JAgoraWebsocketLib extends IJAgoraLib {
    
    private final ClientEndpointConfig cec;
    private final ClientManager client;
    private Session session;
    private BlockingQueue<byte[]> messages;
    
    public JAgoraWebsocketLib() {
        cec = ClientEndpointConfig.Builder.create().build();
        client = ClientManager.createClient();
        messages = new LinkedBlockingQueue<>();
        
    }
    
    public DataInputStream getStream() throws InterruptedException {
        return new DataInputStream(new ByteArrayInputStream(messages.poll(10, TimeUnit.SECONDS)));
    }
    
    public void openConnection(URI target) {
        try {
            session = client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session s, EndpointConfig ec) {
                    s.addMessageHandler(new MessageHandler.Whole <byte[]>() {

                        @Override
                        public void onMessage(byte[] t) {
                            messages.add(t);
                        }
                    });
                }
            }, cec, target);
        } catch (DeploymentException ex) {
            Log.error("[JAgoraLib] Could not connect to server.");
        } catch (IOException ex) {
            Log.error("[JAgoraLib] Could not read incoming message");
        }
    }
    
    @Override
    public boolean login(String user, String password) {
        if (session == null) {
            Log.error("[JAgoraLib] Could not login because there was no session open.");
            return false;
        }
        
        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session,
        constructLoginRequest(user, password));
        if (!success) {
            Log.error("[JAgoraLib] Could not send login message.");
            return false;
        }
        
        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
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
        
        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructLogoutRequest());
        if (!success) {
            Log.error("[JAgoraLib] Could not write logout query.");
            return false;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
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

    @Override
    public boolean isConnected() {
        return (sessionID != null && session != null && session.isOpen());
    }

    @Override
    public JAgoraArgumentID addArgument(BasicBSONObject content, int threadID) {
        JAgoraArgumentID ret = null;
    if (!isConnected()) {
      Log.error("[JAgoraLib] Querying but not connected.");
      return null;
    }
    
    boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructaAddArgumentRequest(content, threadID));
    if (!success) {
      Log.error("[JAgoraLib] Could not write addArgument query.");
      return null;
    }
    
    BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
        if (response == null) {
            Log.error("[JAgoraLib] Could not read addArgument response.");
            return null;
        }

        ret = parseAddArgumentResponse(response);
        if(ret == null){
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
    
    boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructaAddAttackRequest(attacker, defender));
        if (!success) {
            Log.error("[JAgoraLib] Could not write addAttack query.");
            return false;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
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

    @Override
    public boolean addArgumentWithAttacks(BasicBSONObject content, int threadID, ArrayList<JAgoraArgumentID> defenders) {
        JAgoraArgumentID ref = addArgument(content, threadID);
        for (JAgoraArgumentID defender : defenders) {
            addAttack(ref, defender);
        }

        return (ref != null);
    }

    @Override
    public boolean editArgument(BasicBSONObject content, JAgoraArgumentID id) {
        if (!isConnected()) {
            Log.error("[JAgoraLib] Querying but not connected.");
            return false;
        }

        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructEditArgumentRequest(content, id));
        if (!success) {
            Log.error("[JAgoraLib] Could not write editArgument query.");
            return false;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
        if (response == null) {
            Log.error("[JAgoraLib] Could not read editArgument response.");
            return false;
        }

        success = parseEditArgumentResponse(response);
        if(!success){
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
   
       boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructaAddArgumentVoteRequest(nodeID, voteType));
       if (!success) {
         Log.error("[JAgoraLib] Could not write addArgumentVote query.");
         return false;
       }

       BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromWebSocket(getStream());
        } catch (InterruptedException ex) {}
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

    @Override
    public boolean addAttackVote(JAgoraArgumentID attacker, JAgoraArgumentID defender, int voteType) {
        if (!isConnected()) {
            Log.error("[JAgoraLib] Querying but not connected.");
            return false;
        }
 
        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructaAddAttackVoteRequest(attacker, defender, voteType));
        if (!success) {
            Log.error("[JAgoraLib] Could not write addAttack query.");
            return false;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
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

    @Override
    public JAgoraGraph getThreadByID(int threadID) {
        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructGetThreadByIDRequest(threadID));
        if (!success) {
            Log.error("[JAgoraLib] Could not write getThreadByID query.");
            return null;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromWebSocket(getStream());
        } catch (InterruptedException ex) {}
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

    @Override
    public ArrayList<JAgoraThread> getThreadList() {
        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructGetThreadListRequest());
        if (!success) {
            Log.error("[JAgoraLib] Could not write getThreadList query.");
            return null;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromWebSocket(getStream());
        } catch (InterruptedException ex) {
            Logger.getLogger(JAgoraWebsocketLib.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (response == null) {
            Log.error("[JAgoraLib] Could not read getThreadList response.");
            return null;
        }

        ArrayList<JAgoraThread> threads = parseGetThreadListResponse(response);

        success = (threads != null);
        if(!success){
            Log.error("[JAgoraLib] Could not getThreadList.");
            return null;
        }
        return threads;
    }

    @Override
    public JAgoraGraph getThreadByArgumetID(JAgoraArgumentID id) {
        boolean success = JAgoraComms.writeBSONObjectToWebSocket(session, constructGetThreadByArgumentIDRequest(id));
        if (!success) {
            Log.error("[JAgoraLib] Could not write getThreadByID query.");
            return null;
        }

        BasicBSONObject response = null;
        try {
            response = JAgoraComms.readBSONObjectFromStream(getStream());
        } catch (InterruptedException ex) {}
        if (response == null) {
            Log.error("[JAgoraLib] Could not read getThreadByID response.");
            return null;
        }

        JAgoraGraph graph = parseQueryByArgumentIDResponse(response);

        success = (graph != null);
        if(!success){
            Log.error("[JAgoraLib] Could not getThreadByID.");
            return null;
        }

        return graph;
    }
    
}
