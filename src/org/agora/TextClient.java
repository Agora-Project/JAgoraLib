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

package org.agora;

import java.util.*;

import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraArgumentID;
import org.agora.lib.*;
import org.agora.logging.*;
import org.bson.BasicBSONObject;

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
      else if (tokens[0].equals("register"))
        register(tokens);
      else if (tokens[0].equals("login"))
        login(tokens);
      else if (tokens[0].equals("logout"))
        logout(tokens);
      else if(tokens[0].equals("quit"))
        running = false;
      else if(tokens[0].equals("thread"))
        getThreadByID(tokens);
      else if(tokens[0].equals("argument"))
        addArgument(tokens, s);
      else if(tokens[0].equals("attack"))
        addAttack(tokens);
      else if(tokens[0].equals("argumentVote"))
        addArgumentVote(tokens);
      else if(tokens[0].equals("attackVote"))
        addAttackVote(tokens);
      else
        System.out.println("Unrecognised command.");
    }
    s.close();
  }

  public void host(String[] tokens) {
    if (tokens.length < 2) {
      System.out.println("Usage: host <hostname> [port]");
      return;
    }
    System.out.println("Setting host...");
    int port = Options.AGORA_PORT;
    if (tokens.length > 2)
      port = Integer.parseInt(tokens[2]);
    lib = new JAgoraLib(tokens[1], port);
    System.out.println("Setting host complete.");
  }
  
  public void register(String[] tokens) {
    if (tokens.length < 4) {
      System.out.println("Usage: register <username> <password> <email>");
      return;
    }
    
    System.out.print("Logging in...");
    boolean result = lib.register(tokens[1], tokens[2], tokens[3]);
    if (result){
      System.out.println("Registered!");
    } else {
      System.out.println("Failed.");
    }
  }
  
  public void login(String[] tokens) {
    if (tokens.length < 3) {
      System.out.println("Usage: login <username> <password>");
      return;
    }
    
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
  
  public void addArgument(String[] tokens, Scanner s) {
    if (tokens.length < 2) {
      System.out.println("Missing thread ID.");
      return;
    }
    int threadID = Integer.parseInt(tokens[1]);
    
    System.out.println("Please state argument text:");
    String text = s.nextLine();
    
    BasicBSONObject content = new BasicBSONObject();
    content.put(IJAgoraLib.TEXT_FIELD, text);
    
    if (lib.addArgument(content, threadID) != null)
      System.out.println("Argument added!");
    else
      System.out.println("Argument could not be added");
  }
  
  private void addAttack(String[] tokens) {
    if (tokens.length < 5) {
      System.out.println("Wrong arguments.");
      return;
    }
    
    JAgoraArgumentID attacker = new JAgoraArgumentID(tokens[1], Integer.parseInt(tokens[2]));
    JAgoraArgumentID defender = new JAgoraArgumentID(tokens[3], Integer.parseInt(tokens[4]));
    
    if (lib.addAttack(attacker, defender))
      System.out.println("Attack added!");
    else
      System.out.println("Attack could not be added");
  }
  
  private void addAttackVote(String[] tokens) {
    if (tokens.length < 6) {
      System.out.println("Wrong arguments.");
      return;
    }
    
    JAgoraArgumentID attacker = new JAgoraArgumentID(tokens[1], Integer.parseInt(tokens[2]));
    JAgoraArgumentID defender = new JAgoraArgumentID(tokens[3], Integer.parseInt(tokens[4]));
    int voteType = Integer.parseInt(tokens[5]);
    if(voteType != 0 && voteType != 1) {
      System.out.println("Vote types are 0 or 1 only.");
      return;
    }
    
    if (lib.addAttackVote(attacker, defender, voteType))
      System.out.println("Attack vote added!");
    else
      System.out.println("Attack vote could not be added");
  }
  
  private void addArgumentVote(String[] tokens) {
    if (tokens.length < 3) {
      System.out.println("Wrong arguments.");
      return;
    }
    
    JAgoraArgumentID nodeID = new JAgoraArgumentID(tokens[1], Integer.parseInt(tokens[2]));
    int voteType = Integer.parseInt(tokens[3]);
    if(voteType != 0 && voteType != 1) {
      System.out.println("Vote types are 0 or 1 only.");
      return;
    }
    
    if (lib.addArgumentVote(nodeID, voteType))
      System.out.println("Argument vote added!");
    else
      System.out.println("Argument vote could not be added.");
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
