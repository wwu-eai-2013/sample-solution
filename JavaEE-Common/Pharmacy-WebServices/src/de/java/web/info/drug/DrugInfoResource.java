package de.java.web.info.drug;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

public interface DrugInfoResource {

  @GET
  @Path("/drug/{pzn}")
  @Produces("application/json")
  DrugInfo getInfo(@PathParam("pzn") int pzn);

}
