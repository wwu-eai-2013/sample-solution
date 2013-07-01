package de.java.ejb.stats.drug;

import java.io.Serializable;
import java.util.Collection;

public class AggregatedDrugStatistic implements Serializable {

  private static final long serialVersionUID = 3496548403729297148L;

  private Collection<WrappedDrugStatistic> statistics;

  public AggregatedDrugStatistic(Collection<WrappedDrugStatistic> statistics) {
    this.statistics = statistics;
  }

  public long getPzn() {
    // return pzn from first element
    return statistics.iterator().next().getPzn();
  }

  public Collection<WrappedDrugStatistic> getIndividualStatistics() {
    return statistics;
  }

  public double getAverageInventoryLevel() {
    double sum = 0;
    for (WrappedDrugStatistic statistic : statistics) {
      sum += statistic.getAverageInventoryLevel();
    }
    return sum;
  }

}
