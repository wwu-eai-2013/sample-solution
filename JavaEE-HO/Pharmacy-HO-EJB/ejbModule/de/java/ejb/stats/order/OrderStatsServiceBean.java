package de.java.ejb.stats.order;

import static de.java.ejb.Subsidiaries.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import de.java.web.stats.order.OrderStatistic;
import de.java.web.stats.order.OrderStatsResource;

@Stateless
public class OrderStatsServiceBean implements OrderStatsService {

  private OrderStatsResource javaOrderStats;
  private OrderStatsResource csharpeOrderStats;

  @PostConstruct
  public void initialise() {
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    javaOrderStats = ProxyFactory.create(OrderStatsResource.class, JAVA_HOST);
    csharpeOrderStats = ProxyFactory.create(OrderStatsResource.class, CSHARPE_HOST);
  }

  public List<WrappedOrderStatistic> getStats() {
    ArrayList<WrappedOrderStatistic> result = new ArrayList<>();
    for (OrderStatistic statAtJava : javaOrderStats.getAllStatistics()) {
      result.add(new WrappedOrderStatistic(JAVA_DISPLAY_NAME, statAtJava));
    }
    for (OrderStatistic statAtCsharpe : csharpeOrderStats.getAllStatistics()) {
      result.add(new WrappedOrderStatistic(CSHARPE_DISPLAY_NAME, statAtCsharpe));
    }
    return result;
  }

  @Override
  public Deviation calculateAverageDeviation(List<WrappedOrderStatistic> stats) {
    if (stats.isEmpty())
      return new Deviation(0);

    long sum = 0;
    for (WrappedOrderStatistic stat : stats) {
      sum += Math.abs(stat.getDeviation().getTotalSeconds());
    }
    return new Deviation(sum / stats.size());
  }

}
