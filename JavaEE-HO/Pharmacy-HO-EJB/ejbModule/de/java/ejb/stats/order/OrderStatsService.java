package de.java.ejb.stats.order;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface OrderStatsService {

  List<WrappedOrderStatistic> getStats();

  double calculateAverageDeviation(List<WrappedOrderStatistic> stats);

}
