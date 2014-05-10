package org.agora.lib;

import java.util.ArrayList;
import org.agora.graph.JAgoraThread;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

/**
 *
 * @author greg
 */
public class BSONThreadListEncoder {
    
    public BasicBSONObject BSONiseThread(JAgoraThread thread) {
        BasicBSONObject bsonthread = new BasicBSONObject();
        bsonthread.put("ID", thread.getId());
        bsonthread.put("Title", thread.getTitle());
        bsonthread.put("Description", thread.getDescription());
        return bsonthread;
    }
    public BasicBSONObject BSONiseThreadList(ArrayList<JAgoraThread> threads) {
        BasicBSONObject bsonThreadList = new BasicBSONObject();
        
        BasicBSONList bsonThreads = new BasicBSONList();
        for (int i = 0; i < threads.size(); i++) {
            bsonThreads.add(BSONiseThread(threads.get(i)));
        }
        
        bsonThreadList.put("Threads", bsonThreads);
        
        return bsonThreadList;
    }
}
