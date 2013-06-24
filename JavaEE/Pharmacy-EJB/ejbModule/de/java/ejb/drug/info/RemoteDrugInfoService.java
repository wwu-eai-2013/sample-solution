package de.java.ejb.drug.info;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

interface RemoteDrugInfoService {

  @GET
  @Path("/drug/{pzn}")
  @Produces("application/json")
  public DrugInfo getInfo(@PathParam("pzn") int pzn);

}
