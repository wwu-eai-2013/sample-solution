package de.java.web;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import de.java.ejb.stats.order.Deviation;
import de.java.ejb.stats.order.OrderStatsService;
import de.java.ejb.stats.order.WrappedOrderStatistic;

@ManagedBean
public class OrderStatsList {

  @EJB
  private OrderStatsService service;
  private List<WrappedOrderStatistic> stats;

  public List<WrappedOrderStatistic> getStats() {
    if (stats == null) {
      stats = service.getStats();
    }
    return stats;
  }

  public Deviation getAverageDeviation() {
    return service.calculateAverageDeviation(getStats());
  }
}
