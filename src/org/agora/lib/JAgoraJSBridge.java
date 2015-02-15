package org.agora.lib;

// JavaSocketBridge.java
// by Stephen Ware
// April 25, 2009
//
// Part of the JavaSocketBridge project.
// This applet provides an interface for using true sockets in JavaScript.
//
// Note: You will need to have the Java Plugin archive in your classpath to compile this.
//       For me, that's C:\Program Files\Java\jre6\lib\plugin.jar
// Note: You will need to jar this class and Listener.class into a signed jar file if
//       you want your sockets to access domains other than the one this is running on.
// Note: Apparently, when you grant permissions to Java applets in Java 6, you only grant
//       them to the main applet thread.  That's the reason for all the confusing stuff
//       in the connect methods... so that connections always happen on the main thread.

import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private JAgoraWebsocketLib lib;
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
            lib = new JAgoraWebsocketLib();
            lib.openConnection(new URI("ws://" + getCodeBase().getHost() + ":" + getCodeBase().getPort() + getCodeBase().getPath() + "Websocket/Agora"));
        } catch (Exception e) {
            Log.error(e.getMessage());
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

    public boolean logIn(String user, String password) {
        try {
            return lib.login(user, password);
        } catch (Exception ex) {
            Log.error(ex.getMessage());
        }
        return false;
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
        return lib.getThreadByArgumetID(id);
    }
    
    public boolean newArgument(String title, String text, JAgoraArgument[] posts) {
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