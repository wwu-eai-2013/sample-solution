package de.java.web.stats.order;

import java.io.Serializable;

public class OrderStatistic implements Serializable {

  private static final long serialVersionUID = 3881973517501991243L;

  private long orderId;
  private long deviation;

  /**
   * @param orderId
   * @param deviation in seconds
   */
  public OrderStatistic(long orderId, double deviation) {
    // constructor for jpql invocation
    this(orderId, (long) deviation);
  }

  /**
   * @param orderId
   * @param deviation in seconds
   */
  public OrderStatistic(long orderId, long deviation) {
    this.orderId = orderId;
    this.deviation = deviation;
  }

  OrderStatistic() {
    // for deserialization from JSON
  }

  public long getOrderId() {
    return orderId;
  }

  public long getDeviation(){
    return deviation;
  }
}
