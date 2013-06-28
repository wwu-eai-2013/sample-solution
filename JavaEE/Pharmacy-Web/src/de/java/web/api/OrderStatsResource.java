package de.java.web.api;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.NotFoundException;

import de.java.ejb.ReplenishmentOrderService;
import de.java.ejb.stats.OrderDeliveryDeviationService;

@Path("orderstats")
public class OrderStatsResource {

  @EJB
  private OrderDeliveryDeviationService service;

  @EJB
  private ReplenishmentOrderService orderService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<OrderStatistic> getAllStatistics() {
    return service.getAllStats();
  }

  @GET
  @Path("{orderId}")
  @Produces(MediaType.APPLICATION_JSON)
  public OrderStatistic getStatistic(@PathParam("orderId") long orderId) {

    validateOrderExists(orderId);
    return service.getStatistic(orderId);
  }

  private void validateOrderExists(long orderId) {
    if (orderService.getOrder(orderId) == null)
      throw new NotFoundException(String.format("Order with id %s does not exist here", orderId));
  }

}
