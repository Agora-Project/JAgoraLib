package org.agora;

import java.util.*;

import org.agora.lib.*;
import org.agora.logging.*;

public class TextClient {

  protected JAgoraLib lib;
  protected boolean running;
  
  public TextClient(){
    running = false;
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
    boolean result = lib.connect(tokens[1], tokens[2]);
    if (result){
      System.out.println("Connected!");
    } else {
      System.out.println("Failed.");
    }
  }
  
  public void logout(String[] tokens) {
    System.out.println("Logging out...");
  }
  
  public static void main(String[] args) {
    Log.addLog(new ConsoleLog());
    TextClient tc = new TextClient();
    tc.run();
  }
}
