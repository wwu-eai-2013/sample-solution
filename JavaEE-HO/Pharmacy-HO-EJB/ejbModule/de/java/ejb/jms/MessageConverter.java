package de.java.ejb.jms;

import java.util.ArrayList;
import java.util.Collection;

import de.java.ejb.jms.domain.OrderState;
import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

public class MessageConverter {

  /**
   * @param messageText
   *          Each line within the message text corresponds to a single order
   * @param subsidiary
   *          from which the message was received
   */
   public static Collection<ReplenishmentOrder> convertAll(String messageText,
      Subsidiary subsidiary) {
    Collection<ReplenishmentOrder> result = new ArrayList<>();
    String[] ordersAsStrings = messageText.split("\n");
    for (int i = 0; i < ordersAsStrings.length; i++) {
      result.add(convertSingle(ordersAsStrings[i], subsidiary));
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
  public static ReplenishmentOrder convertSingle(String order, Subsidiary subsidiary) {
    String[] orderFragments = order.split(";");

    final ReplenishmentOrder o = new ReplenishmentOrder();
    o.setSubsidiary(subsidiary);
    o.setOrderId(Long.parseLong(orderFragments[0]));
    o.setState(OrderState.valueOf(orderFragments[1]));

    int numberOfPositions = Integer.parseInt(orderFragments[2]);
    for (int i = 0; i < numberOfPositions; i++) {
      int pzn = Integer.parseInt(orderFragments[3 + 2 * i]);
      long quantity = Long.parseLong(orderFragments[4 + 2 * i]);
      o.addPosition(pzn, quantity);
    }
    return o;
  }

}
