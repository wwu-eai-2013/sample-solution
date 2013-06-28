package de.java.web.api;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.NotFoundException;

import de.java.ejb.DrugService;
import de.java.ejb.stats.AverageInventoryStatisticsService;

@Path("drugstats")
public class DrugStatsResource {

  @EJB
  private AverageInventoryStatisticsService service;

  @EJB
  private DrugService drugService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<DrugStatistic> getAllStatistics() {
    Calendar cal = Calendar.getInstance();
    Date toDate = cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, -30);
    Date fromDate = cal.getTime();
    return service.getAllStats(fromDate, toDate);
  }

  @GET
  @Path("{pzn}")
  @Produces(MediaType.APPLICATION_JSON)
  public DrugStatistic getStatistic(@PathParam("pzn") int pzn) {

    validateDrugExists(pzn);

    Calendar cal = Calendar.getInstance();
    Date toDate = cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, -30);
    Date fromDate = cal.getTime();
    return service.getStatistic(pzn, fromDate, toDate);
  }

  private void validateDrugExists(int pzn) {
    if (drugService.getDrug(pzn) == null)
      throw new NotFoundException(String.format("Drug with pzn %s does not exist here", pzn));
  }

}
