package de.java.web.api;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("drugstats")
public interface DrugStatsResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  Collection<DrugStatistic> getAllStatistics();

  @GET
  @Path("{pzn}")
  @Produces(MediaType.APPLICATION_JSON)
  DrugStatistic getStatistic(@PathParam("pzn") int pzn);

}