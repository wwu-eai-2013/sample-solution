package de.java.ejb.stats.drug;

import java.io.Serializable;
import java.util.Collection;

import de.java.web.stats.drug.DrugStatistic;

public class AggregatedDrugStatistic implements Serializable {

  private static final long serialVersionUID = 3496548403729297148L;

  private Collection<DrugStatistic> statistics;

  public AggregatedDrugStatistic(Collection<DrugStatistic> statistics) {
    this.statistics = statistics;
  }

  public long getPzn() {
    // return pzn from first element
    return statistics.iterator().next().getPzn();
  }

  public double getAverageInventoryLevel() {
    double sum = 0;
    for (DrugStatistic statistic : statistics) {
      sum += statistic.getAverageInventoryLevel();
    }
    return sum;
  }

}
