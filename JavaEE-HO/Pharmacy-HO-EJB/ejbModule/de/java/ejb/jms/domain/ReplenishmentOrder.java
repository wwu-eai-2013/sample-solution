package de.java.ejb.jms.domain;

import java.util.ArrayList;
import java.util.Collection;

public class ReplenishmentOrder {

  private Subsidiary subsidiary;
  private long orderId;
  private OrderState state;
  private Collection<Position> positions = new ArrayList<>();

  public Subsidiary getSubsidiary() {
    return subsidiary;
  }

  public void setSubsidiary(Subsidiary subsidiary) {
    this.subsidiary = subsidiary;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public OrderState getState() {
    return state;
  }

  public void setState(OrderState state) {
    this.state = state;
  }

  public Collection<Position> getPositions() {
    return positions;
  }

  public void addPosition(int pzn, long quantity) {
    positions.add(new Position(pzn, quantity));
  }

}
