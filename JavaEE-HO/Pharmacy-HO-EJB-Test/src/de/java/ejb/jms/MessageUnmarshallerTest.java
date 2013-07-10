package de.java.ejb.jms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import de.java.ejb.jms.domain.OrderState;
import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

public class MessageUnmarshallerTest {

  private static final Subsidiary DUMMY_SUBSIDIARY = Subsidiary.JaVa;

  @Test public void
  shouldUnmarshalOpenOrderWithSinglePosition() {
    String input = "5;OPEN;1;451122;23";
    ReplenishmentOrder order = convertSingle(input);
    assertThat(order.getSubsidiary(), is(DUMMY_SUBSIDIARY));
    assertThat(order.getOrderId(), is(5l));
    assertThat(order.getState(), is(OrderState.OPEN));
    assertThat(order.getPositions().size(), is(1));
  }

  private ReplenishmentOrder convertSingle(String input) {
    return MessageUnmarshaller.unmarshalSingle(input, DUMMY_SUBSIDIARY);
  }

  @Test public void
  shouldUnmarshalPostingOrderWithSinglePosition() {
    String input = "5;POSTING;1;451122;23";
    ReplenishmentOrder order = convertSingle(input);
    assertThat(order.getState(), is(OrderState.POSTING));
  }

  @Test public void
  shouldUnmarshalOrderedOrderWithTwoPositions() {
    String input = "5;ORDERED;2;451122;23;1715965;15;2013-05-21 15:35";
    ReplenishmentOrder order = convertSingle(input);
    assertThat(order.getPositions().size(), is(2));
    assertThat(order.getExpectedDelivery(), is(on("2013-05-21 15:35")));
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
  shouldUnmarshalFinishedOrderWithTwoPositions() {
    String input = "5;FINISHED;1;1715965;15;2013-05-21 15:35;2014-06-12 18:22";
    ReplenishmentOrder order = convertSingle(input);
    assertThat(order.getExpectedDelivery(), is(on("2013-05-21 15:35")));
    assertThat(order.getActualDelivery(), is(on("2014-06-12 18:22")));
  }
}
