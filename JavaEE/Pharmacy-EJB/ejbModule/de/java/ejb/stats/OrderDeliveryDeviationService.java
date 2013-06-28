package de.java.ejb.stats;

import java.util.Collection;

import javax.ejb.Remote;

import de.java.web.api.OrderStatistic;

@Remote
public interface OrderDeliveryDeviationService {

  /**
   * @return statistics for all finished orders
   */
  public Collection<OrderStatistic> getAllStats();

  /**
   * @param orderId id of the order for which the statistic is generated
   * @return statistic for single order
   */
  public OrderStatistic getStatistic(long orderId);

}
