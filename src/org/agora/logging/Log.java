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

import java.util.*;

import org.agora.lib.Options;

public abstract class Log {
  
  protected static List<Log> logs = new LinkedList<Log>();
  
  public static void addLog(Log log) { logs.add(log); }
  public static void removeLog(Log log) { logs.remove(log); }
  
  
  public static void log(String message) { log(message, true); }
  public static void log(String message, boolean newline) {
    if (!Options.LOG_MESSAGES) return;
    for (Log logInst : Log.logs)
      logInst.logMessage(message, newline);
  }
  
  public static void error(String message) { error(message, true); }
  public static void error(String message, boolean newline) {
    if (!Options.ERROR_MESSAGES) return;
    for (Log logInst : Log.logs)
      logInst.errorMessage(message, newline);
  }
  
  public static void debug(String message) { debug(message, true); }
  public static void debug(String message, boolean newline) {
    if (!Options.DEBUG_MESSAGES) return;
    for (Log logInst : Log.logs)
      logInst.debugMessage(message, newline);
  }
  
  public abstract void logMessage(String message, boolean newline);
  public abstract void errorMessage(String message, boolean newline);
  public abstract void debugMessage(String message, boolean newline);
}
