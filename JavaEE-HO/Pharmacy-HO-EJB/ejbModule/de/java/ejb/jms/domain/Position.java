package de.java.ejb.jms.domain;

public class Position {

  private final int pzn;
  private final long quantity;

  public Position(int pzn, long quantity) {
    this.pzn = pzn;
    this.quantity = quantity;
  }

  @Override
  public String toString() {
    return String.format("pzn: %d, quantity: %d", pzn, quantity);
  }

  public int getPzn() {
    return pzn;
  }

  public long getQuantity() {
    return quantity;
  }

}
