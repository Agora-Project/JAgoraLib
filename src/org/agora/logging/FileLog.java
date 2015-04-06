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

package org.agora.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLog extends WriterLog {
  
  public PrintWriter createPrintWriter(String file) {
    PrintWriter printWriter = null;
    try {
    File f = new File(file);
    if (!f.exists())
      f.createNewFile();

    printWriter = new PrintWriter(new FileOutputStream(f, true));
    } catch (IOException e) {
      System.err.println("[ERROR] Failed to create PrintWriter for '"+file+"'.");
      Log.error("[ERROR] Failed to create PrintWriter for '"+file+"'.");
    }
    
    return printWriter;
  }
  
  public FileLog(String logFile, String errorFile, String debugFile) {
    super(null, null, null);
    logStream = createPrintWriter(logFile);
    errorStream = createPrintWriter(errorFile);
    debugStream = createPrintWriter(debugFile);
  }

}
