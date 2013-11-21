package org.agora;

import java.util.*;

import org.agora.graph.JAgoraGraph;
import org.agora.lib.*;
import org.agora.logging.*;

public class TextClient {

  protected JAgoraLib lib;
  protected boolean running;
  
  protected JAgoraGraph graph;
  
  public TextClient(){
    running = false;
    lib = new JAgoraLib("127.0.0.1", Options.AGORA_PORT);
  }
  
  public void run() {
    running = true;
    Scanner s = new Scanner(System.in);
    while(running){
      String command = s.nextLine();
      String[] tokens = command.split(" ");
      if (tokens[0].equals("host"))
        host(tokens);
      else if (tokens[0].equals("login"))
        login(tokens);
      else if (tokens[0].equals("logout"))
        logout(tokens);
      else if(tokens[0].equals("quit"))
        running = false;
      else if(tokens[0].equals("thread"))
        getThreadByID(tokens);
      else
        System.out.println("Unrecognised command.");
    }
    s.close();
  }
  
  public void host(String[] tokens) {
    System.out.println("Setting host...");
    int port = Options.AGORA_PORT;
    if (tokens.length > 2)
      port = Integer.parseInt(tokens[2]);
    lib = new JAgoraLib(tokens[1], port);
    System.out.println("Setting host complete.");
  }
  
  public void login(String[] tokens) {
    System.out.print("Logging in...");
    boolean result = lib.login(tokens[1], tokens[2]);
    if (result){
      System.out.println("Connected!");
    } else {
      System.out.println("Failed.");
    }
  }
  
  public void logout(String[] tokens) {
    System.out.println("Logging out...");
    if(!lib.logout())
      System.out.println("Failed!");
    else
      System.out.println("Logged out!");
  }
  
  public void getThreadByID(String[] tokens) {
    if (tokens.length < 2) {
      System.out.println("Wrong arguments.");
      return;
    }
    
    int threadID = Integer.parseInt(tokens[1]);
    System.out.println("Getting JAgoraGraph for thread id = " + threadID + ".");
    graph = lib.getThreadByID(threadID);
    if(graph == null) {
      System.out.println("Something failed.");
    } else {
      System.out.println("Got it!");
    }
  }
  
  public static void main(String[] args) {
    Log.addLog(new ConsoleLog());
    TextClient tc = new TextClient();
    tc.run();
  }
}
