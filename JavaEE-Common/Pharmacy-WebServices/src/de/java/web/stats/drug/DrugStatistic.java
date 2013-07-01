package de.java.web.stats.drug;

import java.io.Serializable;

public class DrugStatistic implements Serializable {

  private static final long serialVersionUID = 2349185609186l;

  private int pzn;
  private double averageInventoryLevel;

  public DrugStatistic() {
    this(42, 5.5);
  }

  public DrugStatistic(int pzn, double averageInventoryLevel) {
    this.pzn = pzn;
    this.averageInventoryLevel = averageInventoryLevel;
  }
  
  public int getPzn() {
    return pzn;
  }

  public double getAverageInventoryLevel(){
    return averageInventoryLevel;
  }
}
