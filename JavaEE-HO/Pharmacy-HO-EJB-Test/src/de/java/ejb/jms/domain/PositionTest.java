package de.java.ejb.jms.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class PositionTest {

  @Test public void
  shouldProvidePrettyString() {
    Position testObject = new Position(23, 42);
    assertThat(testObject.toString(), is("pzn: 23, quantity: 42"));
  }

}
