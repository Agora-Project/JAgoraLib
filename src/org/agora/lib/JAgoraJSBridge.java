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

import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import netscape.javascript.*;
import org.agora.graph.JAgoraArgument;
import org.agora.graph.JAgoraArgumentID;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraThread;
import org.agora.logging.ConsoleLog;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public class JAgoraJSBridge extends JApplet {

    // Instance variables
    JSObject browser = null;		// The browser
    private IJAgoraLib lib;
    ArrayList<JAgoraThread> threadList;
    boolean running = false;		// Am I still running?
    String address;			// Where you will connect to
    int port;					// Port

    // Initialize
    public void init(){
        Log.addLog(new ConsoleLog());
        browser = JSObject.getWindow(this);
        threadList = new ArrayList<>();
        address = "50.97.175.55";
        port = 27387;
    }

    // Main
    // Note: This method loops over and over to handle requests becuase only
    //       this thread gets the elevated security policy.  Java == stupid.
    public void start(){
            browser.call("javaSocketBridgeReady", null);
            running = true;

    }
    
    public void startLib() {
        try {
            //lib = new JAgoraWebsocketLib(new URI("ws://" + getCodeBase().getHost() + ":" + getCodeBase().getPort() + getCodeBase().getPath() + "Websocket/Agora"));
            lib = new JAgoraHTTPLib(new URL(getCodeBase() + "JAgoraHttpServer"));
        } catch (Exception e) {
            Log.error("[JAgoraJSBridge] Failed to start JAgoraHTTPLib: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }

    public void destroy() {
        running = false;
    }
    
    public JAgoraArgumentID findArgument(int id) {
        return new JAgoraArgument(address, id).getID();
    }
    
    public boolean register(String user, String password, String email) {
      return lib.register(user, password, email);
    }

    public boolean logIn(String user, String password) {
        try {
            return lib.login(user, password);
        } catch (Exception ex) {
            Log.error("[JAgoraJSBridge] Failed to login: " + ex.getMessage());
        }
        return false;
    }
    
    public boolean isModerator() {
      return lib.userType == 2;
    }
    
    public void deletePosts(JAgoraArgument[] posts) {
      for (JAgoraArgument arg : posts) {
        lib.deleteArgument(arg.getID());
      }
    }
    
    public ArrayList<JAgoraThread> getThreadList() {
        return lib.getThreadList();
    }
    
//    public int getThreadListSize() {return threadList.size();}
//    
//    public JAgoraThread getThread(int id) {
//        return threadList.get(id);
//    }
    
    public JAgoraGraph getThread(int threadID) {
        return lib.getThreadByID(threadID);
    }
    
    public JAgoraGraph getArgumentbyID(JAgoraArgumentID id) {
        try {
            return lib.getThreadByArgumentID(id);
        } catch (Exception e) {
            Log.error("[JAgoraJSBridge] Failed to getArgumentbyID: " + e.getClass().getName());
        } return null;
    }
    
    public JAgoraArgumentID newArgument(String title, String text, JAgoraArgument[] posts) {
        int threadID = 0;
        if (posts.length > 0) threadID = posts[0].getThreadID();
        BasicBSONObject content = new BasicBSONObject();
        content.put("Title", title);
        content.put("Text", text);
        ArrayList<JAgoraArgumentID> targets = new ArrayList<>();
        for (JAgoraArgument j :posts) {
            targets.add(j.getID());
        }
        return lib.addArgumentWithAttacks(content, threadID, targets);
    }
	
}