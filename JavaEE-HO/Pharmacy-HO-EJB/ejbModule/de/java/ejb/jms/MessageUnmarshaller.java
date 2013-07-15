package de.java.ejb.jms;

import static de.java.util.MessageDelimiter.ORDER_DELIMITER;

import java.util.ArrayList;
import java.util.Collection;

import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

public class MessageUnmarshaller {

  /**
   * @param messageText
   *          Each line within the message text corresponds to a single order without positions
   * @param subsidiary
   *          from which the message was received
   */
   public static Collection<ReplenishmentOrder> unmarshalAll(String messageText,
      Subsidiary subsidiary) {
    if (messageText.isEmpty()) {
      return new ArrayList<>();
    }
    Collection<ReplenishmentOrder> result = new ArrayList<>();
    String[] ordersAsStrings = messageText.split(ORDER_DELIMITER);
    for (int i = 0; i < ordersAsStrings.length; i++) {
      result.add(new OrderUnmarshaller.Batch(ordersAsStrings[i], subsidiary).convert());
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
    return new OrderUnmarshaller.Single(order, subsidiary).convert();
  }

}
