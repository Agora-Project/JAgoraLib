package org.agora.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;

import org.agora.logging.Log;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.java_websocket.client.WebSocketClient;

public class JAgoraComms {
  
  // Serialisation
  //public static DateFormat DATE_FORMAT = new SimpleDateFormat(); //Deprecated?
  
  // ***********************
  // **** Communication ****
  // ***********************
  public static BasicBSONObject readBSONObjectFromSocket(Socket s) {
    try {
      return readBSONObjectFromStream(s.getInputStream());
    } catch (IOException ex) {
      Log.error("[JAgoraCommc] Could not read BSON object from socket: " + ex.getMessage());
    }
      return null;
  }
  
  public static BasicBSONObject readBSONObjectFromStream(InputStream is) throws IOException {
    BSONDecoder bdec = new BasicBSONDecoder();
    return (BasicBSONObject)bdec.readObject(is); 
  }
  
  public static void writeBSONObjectToStream(OutputStream os, BasicBSONObject bson) throws IOException {
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(bson);
      
    os.write(b);
  }
  
  public static boolean writeBSONObjectToSocket(Socket s, BasicBSONObject bson) {
    try {
      writeBSONObjectToStream(s.getOutputStream(), bson);
      return true;
    } catch (IOException e) {
      Log.error("[JAgoraComms] Could not write BSON object to socket: " + e.getMessage());
    }
    
    return false;
  }
  
  public static boolean writeBSONObjectToHTTPConnection(HttpURLConnection connection, BasicBSONObject bson) {
    try {
      writeBSONObjectToStream(connection.getOutputStream(), bson);
    } catch (IOException ex) {
      Log.error("[JAgoraComms] Writing to HTTP output stream failed: " + ex.getMessage());
      return false;
    }
    return true;
  }
  
  public static BasicBSONObject readBSONObjectFromHTTPConnection(HttpURLConnection connection) {
    try {
      return readBSONObjectFromStream(connection.getInputStream());
    } catch (IOException ex) {
      Log.error("[JAgoraComms] Could not read BSON object from HTTP: " + ex.getMessage());
    }
    Log.error("[JAgoraComms] Could not read BSON object from HTTP");
    return null;
  }
  
  public static boolean writeBSONObjectToWebSocket(WebSocketClient client, BasicBSONObject bson) {
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(bson);
    
    client.send(b);
    return true;
    
  }
}




