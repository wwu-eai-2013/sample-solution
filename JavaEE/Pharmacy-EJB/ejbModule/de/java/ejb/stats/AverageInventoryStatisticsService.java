package de.java.ejb.stats;

import java.util.Collection;
import java.util.Date;

import javax.ejb.Remote;

import de.java.web.api.DrugStatistic;

@Remote
public interface AverageInventoryStatisticsService {

  /**
   * @param from beginning of analysis time span (inclusive)
   * @param to end of analysis time span (inclusive)
   * @return statistics for all drugs
   */
  public Collection<DrugStatistic> getAllStats(final Date from, final Date to);

}
