package de.java.ejb.jms;

import static de.java.util.DateFormatter.format;
import static de.java.util.MessageDelimiter.DELIMITER;
import static de.java.util.MessageDelimiter.ORDER_DELIMITER;

import java.util.Collection;
import java.util.Iterator;

import de.java.domain.OrderState;
import de.java.domain.Position;
import de.java.domain.ReplenishmentOrder;

public class OrderMarshaller {

  public static String marshalAll(Collection<ReplenishmentOrder> orders) {
    String marshalledResult = "";
    Iterator<ReplenishmentOrder> iterator = orders.iterator();
    while (iterator.hasNext()) {
      ReplenishmentOrder order = iterator.next();
      marshalledResult += order.getId();
      marshalledResult += DELIMITER;
      marshalledResult += order.getState();
      if (order.getState() == OrderState.ORDERED
          || order.getState() == OrderState.FINISHED) {
        marshalledResult += DELIMITER;
        marshalledResult += format(order.getExpectedDelivery());
      }
      if (order.getState() == OrderState.FINISHED) {
        marshalledResult += DELIMITER;
        marshalledResult += format(order.getActualDelivery());
      }
      if (iterator.hasNext()) {
        marshalledResult += ORDER_DELIMITER;
      }
    }
    return marshalledResult;
  }

  public static String marshalSingle(ReplenishmentOrder order) {
    String marshalledResult = "";
    marshalledResult += order.getId();
    marshalledResult += DELIMITER;
    marshalledResult += order.getState();
    marshalledResult += marshalPositions(order.getPositions());
    if (order.getState() == OrderState.ORDERED
        || order.getState() == OrderState.FINISHED) {
      marshalledResult += DELIMITER;
      marshalledResult += format(order.getExpectedDelivery());
    }
    if (order.getState() == OrderState.FINISHED) {
      marshalledResult += DELIMITER;
      marshalledResult += format(order.getActualDelivery());
    }
    return marshalledResult;
  }

  private static String marshalPositions(Collection<Position> positions) {
    String marshalledResult = "";
    marshalledResult += DELIMITER;
    marshalledResult += positions.size();
    for (Position p : positions) {
      marshalledResult += DELIMITER;
      marshalledResult += p.getReplenishedDrug().getPzn();
      marshalledResult += DELIMITER;
      marshalledResult += p.getQuantity();
    }
    return marshalledResult;
  }

}
