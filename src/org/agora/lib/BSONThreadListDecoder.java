package org.agora.lib;

import java.util.ArrayList;
import org.agora.graph.JAgoraThread;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

/**
 *
 * @author greg
 */
public class BSONThreadListDecoder {
    
    public JAgoraThread deBSONiseThread(BasicBSONObject object) {
        int id = object.getInt("ID");
        String title = object.getString("Title");
        String description = object.getString("Description");
        JAgoraThread thread = new JAgoraThread(id, title, description);
        return thread;
    }
    
    public ArrayList<JAgoraThread> deBSONiseThreadList(BasicBSONObject bsonThreads) {
        
        BasicBSONList bsonList = (BasicBSONList) bsonThreads.get("Threads");
        ArrayList<JAgoraThread> threads = new ArrayList<>();
        for (Object o : bsonList) {
            threads.add(deBSONiseThread((BasicBSONObject) o));
        }
        return threads;
    }
}
