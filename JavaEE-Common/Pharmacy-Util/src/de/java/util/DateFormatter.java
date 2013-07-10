package de.java.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
  private static final SimpleDateFormat formatter = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm");

  public static String format(Date date) {
    return formatter.format(date);
  }

  /**
   * @param input
   *          in the format yyyy-MM-dd HH:mm
   * @return
   */
  public static Date parse(String input) {
    try {
      return formatter.parse(input);
    } catch (ParseException e) {
      throw new RuntimeException("Could not parse from : " + input, e);
    }
  }
}
