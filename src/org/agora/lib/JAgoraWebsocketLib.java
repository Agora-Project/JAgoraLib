
package org.agora.lib;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
    
    public JAgoraWebsocketLib() {
        cec = ClientEndpointConfig.Builder.create().build();
        client = ClientManager.createClient();
        
    }
    
    public void openConncection(URI target) {
        try {
            session = client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session sn, EndpointConfig ec) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }, cec, target);
        } catch (DeploymentException ex) {
            Logger.getLogger(JAgoraWebsocketLib.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JAgoraWebsocketLib.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean login(String user, String password) {
        if (session == null) {
            Log.error("[JAgoraLib] Could not login because there was no session open.");
            return false;
        }
        
        return true;
    }

    @Override
    public boolean logout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JAgoraArgumentID addArgument(BasicBSONObject content, int threadID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAttack(JAgoraArgumentID attacker, JAgoraArgumentID defender) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addArgumentWithAttacks(BasicBSONObject content, int threadID, ArrayList<JAgoraArgumentID> defenders) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean editArgument(BasicBSONObject content, JAgoraArgumentID id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addArgumentVote(JAgoraArgumentID nodeID, int voteType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAttackVote(JAgoraArgumentID attacker, JAgoraArgumentID defender, int voteType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JAgoraGraph getThreadByID(int threadID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<JAgoraThread> getThreadList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JAgoraGraph getThreadByArgumetID(JAgoraArgumentID id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
