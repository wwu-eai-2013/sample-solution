package de.java.ejb.jms;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import de.java.domain.OrderState;
import de.java.domain.Position;
import de.java.domain.ReplenishmentOrder;

public class OrderMarshaller {
  private static final String DELIMITER = ";";
  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
        marshalledResult += formatter.format(order.getExpectedDelivery());
      }
      if (order.getState() == OrderState.FINISHED) {
        marshalledResult += DELIMITER;
        marshalledResult += formatter.format(order.getActualDelivery());
      }
      if (iterator.hasNext()) {
        marshalledResult += "\n";
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
      marshalledResult += formatter.format(order.getExpectedDelivery());
    }
    if (order.getState() == OrderState.FINISHED) {
      marshalledResult += DELIMITER;
      marshalledResult += formatter.format(order.getActualDelivery());
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
