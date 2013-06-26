package de.java.web.api;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("drug")
public class DrugResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<DrugStatistic> getAllStatistics() {
    ArrayList<DrugStatistic> statistics = new ArrayList<>();
    statistics.add(new DrugStatistic());
    return statistics;
  }

  @GET
  @Path("{pzn}")
  @Produces(MediaType.APPLICATION_JSON)
  public DrugStatistic getStatistic(@PathParam("pzn") int pzn) {
    return new DrugStatistic();
  }

}
