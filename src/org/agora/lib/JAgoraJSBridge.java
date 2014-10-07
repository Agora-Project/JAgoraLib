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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import netscape.javascript.*;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraThread;
import org.agora.logging.ConsoleLog;
import org.agora.logging.Log;

public class JAgoraJSBridge extends JApplet {

    // Instance variables
    JSObject browser = null;		// The browser
    private JAgoraLib lib;
    ArrayList<JAgoraThread> threadList;
    boolean running = false;		// Am I still running?
    String address = "127.0.0.1";			// Where you will connect to
    int port = 1597;					// Port

    // Initialize
    public void init(){
        browser = JSObject.getWindow(this);
        lib = new JAgoraLib(address, port);
        threadList = new ArrayList<>();
        Log.addLog(new ConsoleLog());
    }

    // Main
    // Note: This method loops over and over to handle requests becuase only
    //       this thread gets the elevated security policy.  Java == stupid.
    public void start(){
            browser.call("javaSocketBridgeReady", null);
            running = true;

    }

    public void stop() {
        running = false;
    }

    public void destroy() {
        running = false;
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
	
}