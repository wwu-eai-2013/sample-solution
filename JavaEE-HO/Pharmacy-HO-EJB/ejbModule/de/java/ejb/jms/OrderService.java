package de.java.ejb.jms;

import java.util.Collection;

import javax.ejb.Remote;

import de.java.ejb.jms.domain.ReplenishmentOrder;

@Remote
public interface OrderService {

  public Collection<ReplenishmentOrder> getAllOrders();

}
