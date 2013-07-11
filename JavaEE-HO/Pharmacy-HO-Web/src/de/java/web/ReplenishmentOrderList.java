package de.java.web;

import java.util.Collection;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import de.java.ejb.jms.OrderService;
import de.java.ejb.jms.domain.ReplenishmentOrder;

@ManagedBean
public class ReplenishmentOrderList {

  @EJB
  private OrderService orderService;

  private Collection<ReplenishmentOrder> orders;

  public Collection<ReplenishmentOrder> getOrders() {
    if (orders == null) {
      orders = orderService.getAllOrders();
    }
    return orders;
  }
}
