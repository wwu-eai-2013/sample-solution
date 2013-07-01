package de.java.ejb;

import java.io.Serializable;

import de.java.web.stats.order.OrderStatistic;

public class WrappedOrderStatistic implements Serializable {
  
  private static final long serialVersionUID = 3496548403729297148L;

  private String subsidiary;
  private OrderStatistic statistic;

  public WrappedOrderStatistic(String subsidiary, OrderStatistic statistic) {
    this.subsidiary = subsidiary;
    this.statistic = statistic;
  }

  public String getSubsidiary() {
    return subsidiary;
  }

  public long getOrderId() {
    return statistic.getOrderId();
  }

  public long getDeviation() {
    return statistic.getDeviation();
  }

}
