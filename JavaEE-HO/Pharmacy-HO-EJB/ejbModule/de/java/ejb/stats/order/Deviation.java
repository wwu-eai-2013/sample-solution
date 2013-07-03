package de.java.ejb.stats.order;

import java.io.Serializable;

public class Deviation implements Serializable {

  private static final long serialVersionUID = -1284676130222211633L;

  private long deviation;

  /**
   * @param deviation in seconds
   */
  public Deviation(final long deviation) {
    this.deviation = deviation;
  }

  /**
   * @return deviation in seconds
   */
  public long getTotalSeconds() {
    return deviation;
  }
  
  long days() {
    return deviation / (24*60*60);
  }
  
  long hours() {
    return deviation / (60*60) - (days() * 24);
  }

  long minutes() {
    return deviation / 60 - (hours() * 60) - (days() * 24 * 60);
  }

  long seconds() {
    return deviation % 60;
  }

  @Override
  public String toString() {
    return "" + dayString() + hourString() + minuteString() + secondString();
  }

  private String dayString() {
    long days = days();
    if (days == 1)
      return days + " day, ";
    if (days > 1)
      return days + " days, ";
    return "";
  }

  private String hourString() {
    long hours = hours();
    if (hours == 1)
      return hours + " hour, ";
    // also show hours when days are present
    if (hours > 1 || days() > 0)
      return hours + " hours, ";
    return "";
  }

  private String minuteString() {
    long minutes = minutes();
    if (minutes == 1)
      return minutes + " minute, ";
    // also show minutes when hours or days are present
    if (minutes > 1 || hours() > 0 || days() > 0)
      return minutes + " minutes, ";
    return "";
  }

  private String secondString() {
    long seconds = seconds();
    if (seconds == 1)
      return seconds + " second";
    // always show seconds
    return seconds + " seconds";
  }

}
