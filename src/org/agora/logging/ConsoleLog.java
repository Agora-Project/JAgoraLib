package org.agora.logging;

import java.io.PrintWriter;

public class ConsoleLog extends WriterLog {
  public ConsoleLog() {
    super(new PrintWriter(System.out, true), new PrintWriter(System.err, true), new PrintWriter(System.out, true));
  }
}
