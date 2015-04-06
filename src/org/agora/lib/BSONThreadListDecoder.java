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
