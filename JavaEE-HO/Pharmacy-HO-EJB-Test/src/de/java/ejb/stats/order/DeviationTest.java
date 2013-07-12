package de.java.ejb.stats.order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class DeviationTest {

  private static final long ONE_HOUR = 60*60;
  private static final long ONE_DAY = 24*ONE_HOUR;

  @Test public void
  shouldCalculateSeconds() {
    assertThat(new Deviation(102).seconds(), is(42l));
  }

  @Test public void
  shouldCalculateMinutes() {
    assertThat(new Deviation(ONE_HOUR + 162).minutes(), is(2l));
  }

  @Test public void
  shouldCalculateHours() {
    assertThat(new Deviation(ONE_DAY + ONE_HOUR).hours(), is(1l));
  }

  @Test public void
  shouldCalculateDays() {
    assertThat(new Deviation(ONE_DAY).days(), is(1l));
    assertThat(new Deviation(2 * ONE_DAY + ONE_HOUR).days(), is(2l));
  }

  @Test public void
  shouldNotReturn1440MinutesWhenTheyAreInFactZero() {
    assertThat(new Deviation(86400).minutes(), is(0l));
  }

  @Test public void
  shouldDealWithNegativeInputAsIfItWasPositive() {
    assertThat(new Deviation(-86400).days(), is(1l));
     
  }
}
