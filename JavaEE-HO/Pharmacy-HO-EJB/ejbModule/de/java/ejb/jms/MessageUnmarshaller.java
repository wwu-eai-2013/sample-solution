package de.java.ejb.jms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.java.ejb.jms.domain.OrderState;
import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

public class MessageUnmarshaller {
  /**
   * @param messageText
   *          Each line within the message text corresponds to a single order
   * @param subsidiary
   *          from which the message was received
   */
   public static Collection<ReplenishmentOrder> unmarshalAll(String messageText,
      Subsidiary subsidiary) {
    Collection<ReplenishmentOrder> result = new ArrayList<>();
    String[] ordersAsStrings = messageText.split("\n");
    for (int i = 0; i < ordersAsStrings.length; i++) {
      result.add(unmarshalSingle(ordersAsStrings[i], subsidiary));
    }
    return result;
  }

  /**
   * @param order 
   *          Order as string. Has to adhere to the following format (elements are delimited by semicolons):
   *          orderId;state;numberOfPositions{;pzn;quantity}+[;expectedDelivery[;actualDelivery]]
   *          
   *          orderId as long
   *          state as string
   *          numberOfPositions as int
   *          pzn as int
   *          quantity as long
   *          expected and actual delivery as string (format: yyyy-MM-dd HH:mm)
   *          
   * @param subsidiary
   *          from which the message was received
   */
  public static ReplenishmentOrder unmarshalSingle(String order, Subsidiary subsidiary) {
    return new SingleOrderUnmarshaller(order, subsidiary).convert();
  }

  static class SingleOrderUnmarshaller {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private Subsidiary subsidiary;
    private String[] orderFragments;
    private ReplenishmentOrder order = new ReplenishmentOrder();

    public SingleOrderUnmarshaller(String orderString, Subsidiary subsidiary) {
      this.subsidiary = subsidiary;
      orderFragments = orderString.split(";");
    }

    public ReplenishmentOrder convert() {
      order.setSubsidiary(subsidiary);
      order.setOrderId(orderId());
      order.setState(state());

      addPositions();

      if (orderedOrFinished(order)) {
        String expectedDelivery = orderFragments[3 + 2 * numberOfPositions()];
        order.setExpectedDelivery(parseDate(expectedDelivery));
      }
      if (finished(order)) {
        String actualDelivery = orderFragments[4 + 2 * numberOfPositions()];
        order.setActualDelivery(parseDate(actualDelivery));
      }
      return order;
    }

    private long orderId() {
      return Long.parseLong(orderFragments[0]);
    }

    private OrderState state() {
      return OrderState.valueOf(orderFragments[1]);
    }
    
    private void addPositions() {
      for (int i = 0; i < numberOfPositions(); i++) {
        int pzn = Integer.parseInt(orderFragments[3 + 2 * i]);
        long quantity = Long.parseLong(orderFragments[4 + 2 * i]);
        order.addPosition(pzn, quantity);
      }
    }

    private boolean orderedOrFinished(ReplenishmentOrder order) {
      return order.getState() == OrderState.ORDERED || finished(order);
    }

    private boolean finished(ReplenishmentOrder order) {
      return order.getState() == OrderState.FINISHED;
    }

    private int numberOfPositions() {
      return Integer.parseInt(orderFragments[2]);
    }

    private Date parseDate(String from) {
      try {
        return formatter.parse(from);
      } catch (ParseException e) {
        throw new RuntimeException("Could not parse from : " + from, e);
      }
    }
  }
}
