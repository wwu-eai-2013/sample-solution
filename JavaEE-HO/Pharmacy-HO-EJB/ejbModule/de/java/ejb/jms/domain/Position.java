package de.java.ejb.jms.domain;

public class Position {

  private final int pzn;
  private final long quantity;

  public Position(int pzn, long quantity) {
    this.pzn = pzn;
    this.quantity = quantity;
  }

}
