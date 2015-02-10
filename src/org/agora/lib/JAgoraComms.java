package org.agora.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.agora.logging.Log;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;

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
          Logger.getLogger(JAgoraComms.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
  }
  
  public static BasicBSONObject readBSONObjectFromStream(InputStream is) {
      try {
      BSONDecoder bdec = new BasicBSONDecoder();
      return (BasicBSONObject)bdec.readObject(is);
    } catch (IOException e) {
      Log.error("Could not read BSON object from stream " + is);
      Log.error(e.getMessage());
    }
    
    return null; 
  }
  
  public static boolean writeBSONObjectToSocket(Socket s, BasicBSONObject bson) {
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(bson);
    
    try {
      s.getOutputStream().write(b);
      return true;
    } catch (IOException e) {
      Log.error("Could not write BSON object to socket " + s);
      Log.error(e.getMessage());
    }
    
    return false;
  }
}




