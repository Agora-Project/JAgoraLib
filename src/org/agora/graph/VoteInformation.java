package org.agora.graph;

/**
 * This class contains vote information: the number of pro and con votes.
 *
 */
public class VoteInformation {
  protected int proVotes;
  protected int conVotes;
  
  public VoteInformation() {
    proVotes = 0;
    conVotes = 0;
  }
  
  public VoteInformation(int proVotes, int conVotes) {
    this.proVotes = proVotes;
    this.conVotes = conVotes;
  }

  public int getProVotes() { return proVotes; }
  public int getConVotes() { return conVotes; }
}
