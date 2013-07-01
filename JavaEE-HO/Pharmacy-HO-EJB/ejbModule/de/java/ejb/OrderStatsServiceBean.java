package de.java.ejb;

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

  static final String JAVA_SERVICE_HOST = "http://localhost:8080/Pharmacy-Web/api/";

  private OrderStatsResource remote;

  @PostConstruct
  public void initialise() {
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    remote = ProxyFactory.create(OrderStatsResource.class, JAVA_SERVICE_HOST);
  }

  public List<WrappedOrderStatistic> getStats() {
    ArrayList<WrappedOrderStatistic> result = new ArrayList<WrappedOrderStatistic>();
    for (OrderStatistic stat : remote.getAllStatistics()) {
      result.add(new WrappedOrderStatistic("JaVa", stat));
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
