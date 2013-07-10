package de.java.ejb.jms;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import de.java.domain.Drug;
import de.java.domain.OrderState;
import de.java.domain.Position;
import de.java.domain.ReplenishmentOrder;

public class OrderMarshallerTest {

  @Test public void
  shouldMarshalSingleOrdersOntoSingleLines() {
    ReplenishmentOrder openOrder = new ReplenishmentOrder();
    openOrder.setId(5);
    ReplenishmentOrder finishedOrder = new ReplenishmentOrder();
    finishedOrder.setId(6);
    finishedOrder.setState(OrderState.FINISHED);
    finishedOrder.setExpectedDelivery(on("2014-05-03 09:23"));
    finishedOrder.setActualDelivery(on("2013-04-01 12:45"));
    String marshalledOrders = OrderMarshaller.marshalAll(asList(openOrder, finishedOrder));
    assertThat(marshalledOrders, is("5;OPEN\n6;FINISHED;2014-05-03 09:23;2013-04-01 12:45"));
  }

  private Date on(String date) {
    String pattern = "yyyy-MM-dd HH:mm";
    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    try {
      return formatter.parse(date);
    } catch (ParseException e) {
      fail("Provided date could not be parsed: " + date + " should adhere to " + pattern);
      return null;
    }
  }

  @Test public void
  shouldMarshalSingleOrderWithPositions() {
    Position position = new Position();
    position.setReplenishedDrug(new Drug(56, ""));
    position.setQuantity(42);

    ReplenishmentOrder finishedOrder = new ReplenishmentOrder();
    finishedOrder.setId(42);
    finishedOrder.setState(OrderState.FINISHED);
    finishedOrder.setExpectedDelivery(on("2014-05-03 09:23"));
    finishedOrder.setActualDelivery(on("2013-04-01 12:45"));
    finishedOrder.getPositions().add(position);
    String orders = OrderMarshaller.marshalSingle(finishedOrder);
    assertThat(orders, is("42;FINISHED;1;56;42;2014-05-03 09:23;2013-04-01 12:45"));
  }

}
