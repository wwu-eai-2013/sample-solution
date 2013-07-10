package de.java.ejb.jms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import de.java.ejb.jms.domain.OrderState;
import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

public class ConversionTest {

  private static final Subsidiary DUMMY_SUBSIDIARY = Subsidiary.JaVa;

  @Test public void
  shouldConvertOpenOrderWithSinglePosition() {
    String input = "5;OPEN;1;451122;23";
    ReplenishmentOrder order = convertSingle(input);
    assertThat(order.getSubsidiary(), is(DUMMY_SUBSIDIARY));
    assertThat(order.getOrderId(), is(5l));
    assertThat(order.getState(), is(OrderState.OPEN));
    assertThat(order.getPositions().size(), is(1));
  }

  private ReplenishmentOrder convertSingle(String input) {
    return MessageConverter.convertSingle(input, DUMMY_SUBSIDIARY);
  }

}
