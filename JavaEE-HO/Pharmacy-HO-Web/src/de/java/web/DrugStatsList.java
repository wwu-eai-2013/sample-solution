package de.java.web;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import de.java.ejb.stats.drug.AggregatedDrugStatistic;
import de.java.ejb.stats.drug.DrugStatsService;

@ManagedBean
public class DrugStatsList {

  @EJB
  private DrugStatsService service;

  private List<AggregatedDrugStatistic> stats;

  public List<AggregatedDrugStatistic> getStats() {
    if (stats == null) {
      stats = service.getStats();
    }
    return stats;
  }

}
