package de.java.ejb.jms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class ReplenishmentOrder implements Serializable {

  private static final long serialVersionUID = -1850882584651740416L;

  private Subsidiary subsidiary;
  private long orderId;
  private OrderState state;
  private Collection<Position> positions = new ArrayList<>();
  private Date expectedDelivery;
  private Date actualDelivery;

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

  public Date getExpectedDelivery() {
    return expectedDelivery;
  }

  public void setExpectedDelivery(Date expectedDelivery) {
    this.expectedDelivery = expectedDelivery;
  }

  public Date getActualDelivery() {
    return actualDelivery;
  }

  public void setActualDelivery(Date actualDelivery) {
    this.actualDelivery = actualDelivery;
  }

}
