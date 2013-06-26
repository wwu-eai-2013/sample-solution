package de.java.web.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DrugStatistic {

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
