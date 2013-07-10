package de.java.ejb.jms.domain;

import java.io.Serializable;

public class Position implements Serializable {

  private static final long serialVersionUID = -3449327475700083055L;

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
