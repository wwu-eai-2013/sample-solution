package de.java.web.api;

import java.util.Collection;

import javax.ejb.EJB;

import org.jboss.resteasy.spi.NotFoundException;

import de.java.domain.OrderState;
import de.java.domain.ReplenishmentOrder;
import de.java.ejb.ReplenishmentOrderService;
import de.java.ejb.stats.OrderDeliveryDeviationService;

public class OrderStatsResourceImpl implements OrderStatsResource {

  @EJB
  private OrderDeliveryDeviationService service;

  @EJB
  private ReplenishmentOrderService orderService;

  @Override
  public Collection<OrderStatistic> getAllStatistics() {
    return service.getAllStats();
  }

  @Override
  public OrderStatistic getStatistic(long orderId) {
    validateOrderExists(orderId);
    return service.getStatistic(orderId);
  }

  private void validateOrderExists(long orderId) {
    ReplenishmentOrder order = orderService.getOrder(orderId);
    if (order == null)
      throw new NotFoundException(String.format("Order with id %s does not exist here", orderId));
    if (order.getState() != OrderState.FINISHED)
      throw new NotFoundException(String.format("Order with id %s is not finished yet", orderId));
  }

}
