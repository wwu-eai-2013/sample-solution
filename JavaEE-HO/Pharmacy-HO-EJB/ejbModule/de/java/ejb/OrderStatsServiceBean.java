package de.java.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

@Stateless
public class OrderStatsServiceBean implements OrderStatsService {

  public List<WrappedOrderStatistic> getStats() {
    ArrayList<WrappedOrderStatistic> result = new ArrayList<WrappedOrderStatistic>();
    result.add(new WrappedOrderStatistic());
    return result;
  }

  @Override
  public double calculateAverageDeviation(List<WrappedOrderStatistic> stats) {
    return 42.5;
  }

}
