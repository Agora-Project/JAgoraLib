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

import java.io.*;

public class WriterLog extends Log {

  protected PrintWriter logStream;
  protected PrintWriter errorStream;
  protected PrintWriter debugStream;
  
  public WriterLog(PrintWriter logStream, PrintWriter errorStream, PrintWriter debugStream) {
    this.logStream = logStream;
    this.errorStream = errorStream;
    this.debugStream = debugStream;
  }
  
  @Override
  public void logMessage(String message, boolean newline) {
    if (newline) logStream.println(message);
    else logStream.print(message);
  }

  @Override
  public void errorMessage(String message, boolean newline) {
    if (newline) errorStream.println(message);
    else errorStream.print(message);
  }
  
  @Override
  public void debugMessage(String message, boolean newline) {
    if (newline) debugStream.println(message);
    else debugStream.print(message);
  }
}
