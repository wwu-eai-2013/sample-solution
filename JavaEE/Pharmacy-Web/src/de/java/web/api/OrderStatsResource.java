package de.java.web.api;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("orderstats")
public interface OrderStatsResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  Collection<OrderStatistic> getAllStatistics();

  @GET
  @Path("{orderId}")
  @Produces(MediaType.APPLICATION_JSON)
  OrderStatistic getStatistic(@PathParam("orderId") long orderId);

}