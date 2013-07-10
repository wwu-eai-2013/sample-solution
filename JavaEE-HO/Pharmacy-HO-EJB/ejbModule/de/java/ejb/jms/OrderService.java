package de.java.ejb.jms;

import java.util.Collection;
import java.util.Date;

import javax.ejb.Remote;

import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

@Remote
public interface OrderService {

  public Collection<ReplenishmentOrder> getAllOrders();

  public ReplenishmentOrder getOrder(Subsidiary subsidiary, long orderId);

  public ReplenishmentOrder post(ReplenishmentOrder order);

  public ReplenishmentOrder order(ReplenishmentOrder order, Date expectedDelivery);

  public ReplenishmentOrder cancel(ReplenishmentOrder order);

}
