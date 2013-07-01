package de.java.web.api;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;
import javax.ws.rs.Path;

import org.jboss.resteasy.spi.NotFoundException;

import de.java.ejb.DrugService;
import de.java.ejb.stats.AverageInventoryStatisticsService;
import de.java.web.stats.drug.DrugStatistic;
import de.java.web.stats.drug.DrugStatsResource;

@Path("drugstats")
public class DrugStatsResourceImpl implements DrugStatsResource {

  @EJB
  private AverageInventoryStatisticsService service;

  @EJB
  private DrugService drugService;

  @Override
  public Collection<DrugStatistic> getAllStatistics() {
    Calendar cal = Calendar.getInstance();
    Date toDate = cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, -30);
    Date fromDate = cal.getTime();
    return service.getAllStats(fromDate, toDate);
  }

  @Override
  public DrugStatistic getStatistic(int pzn) {

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
