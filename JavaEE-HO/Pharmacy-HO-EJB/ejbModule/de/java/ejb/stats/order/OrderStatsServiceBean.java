package de.java.ejb.stats.order;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import de.java.ejb.Subsidiaries;
import de.java.web.stats.order.OrderStatistic;
import de.java.web.stats.order.OrderStatsResource;

@Stateless
public class OrderStatsServiceBean implements OrderStatsService {

  private OrderStatsResource javaOrderStats;
  private OrderStatsResource csharpeOrderStats;

  @PostConstruct
  public void initialise() {
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    javaOrderStats = ProxyFactory.create(OrderStatsResource.class, Subsidiaries.JAVA_HOST);
    csharpeOrderStats = ProxyFactory.create(OrderStatsResource.class, Subsidiaries.CSHARPE_HOST);
  }

  public List<WrappedOrderStatistic> getStats() {
    ArrayList<WrappedOrderStatistic> result = new ArrayList<>();
    for (OrderStatistic statAtJava : javaOrderStats.getAllStatistics()) {
      result.add(new WrappedOrderStatistic("JaVa", statAtJava));
    }
    for (OrderStatistic statAtCsharpe : csharpeOrderStats.getAllStatistics()) {
      result.add(new WrappedOrderStatistic("C.Sharpe", statAtCsharpe));
    }
    return result;
  }

  @Override
  public double calculateAverageDeviation(List<WrappedOrderStatistic> stats) {
    long sum = 0;
    for (WrappedOrderStatistic stat : stats) {
      sum += stat.getDeviation();
    }
    return sum / stats.size();
  }

}
