package de.java.ejb;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface OrderStatsService {

  List<WrappedOrderStatistic> getStats();

  double calculateAverageDeviation(List<WrappedOrderStatistic> stats);

}
