package de.java.ejb.jms;

import static de.java.util.DateFormatter.parse;

import java.util.Date;

import de.java.ejb.jms.domain.OrderState;
import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;
import de.java.util.DateFormatter;

abstract class OrderUnmarshaller {

  protected String[] orderFragments;
  protected ReplenishmentOrder order = new ReplenishmentOrder();

  protected OrderUnmarshaller(String orderString, Subsidiary subsidiary) {
    order.setSubsidiary(subsidiary);
    orderFragments = orderString.split(";");
  }

  long orderId() {
    return Long.parseLong(orderFragments[0]);
  }

  OrderState state() {
    return OrderState.valueOf(orderFragments[1]);
  }

  boolean orderedOrFinished(ReplenishmentOrder order) {
    return order.getState() == OrderState.ORDERED || finished(order);
  }

  boolean finished(ReplenishmentOrder order) {
    return order.getState() == OrderState.FINISHED;
  }

  Date parseDate(String from) {
    return DateFormatter.parse(from);
  }

  static class Batch extends OrderUnmarshaller {

    public Batch(String orderString, Subsidiary subsidiary) {
      super(orderString, subsidiary);
    }

    public ReplenishmentOrder convert() {
      order.setOrderId(orderId());
      order.setState(state());

      if (orderedOrFinished(order)) {
        String expectedDelivery = orderFragments[2];
        order.setExpectedDelivery(parse(expectedDelivery));
      }
      if (finished(order)) {
        String actualDelivery = orderFragments[3];
        order.setActualDelivery(parse(actualDelivery));
      }
      return order;
    }
  }
  static class Single extends OrderUnmarshaller {

    public Single(String orderString, Subsidiary subsidiary) {
      super(orderString, subsidiary);
    }

    public ReplenishmentOrder convert() {
      order.setOrderId(orderId());
      order.setState(state());

      // unmarshal positions
      addPositions();

      if (orderedOrFinished(order)) {
        String expectedDelivery = orderFragments[3 + 2 * numberOfPositions()];
        order.setExpectedDelivery(parse(expectedDelivery));
      }
      if (finished(order)) {
        String actualDelivery = orderFragments[4 + 2 * numberOfPositions()];
        order.setActualDelivery(parse(actualDelivery));
      }
      return order;
    }

    private void addPositions() {
      for (int i = 0; i < numberOfPositions(); i++) {
        int pzn = Integer.parseInt(orderFragments[3 + 2 * i]);
        long quantity = Long.parseLong(orderFragments[4 + 2 * i]);
        order.addPosition(pzn, quantity);
      }
    }

    private int numberOfPositions() {
      return Integer.parseInt(orderFragments[2]);
    }
  }
}
