package de.java.ejb.stats.drug;

import java.io.Serializable;

import de.java.web.stats.drug.DrugStatistic;

public class WrappedDrugStatistic implements Serializable {
  
  private static final long serialVersionUID = 3496548403729297148L;

  private String subsidiary;
  private DrugStatistic statistic;

  public WrappedDrugStatistic(String subsidiary, DrugStatistic statistic) {
    this.subsidiary = subsidiary;
    this.statistic = statistic;
  }

  public String getSubsidiary() {
    return subsidiary;
  }

  public int getPzn() {
    return statistic.getPzn();
  }

  public double getAverageInventoryLevel() {
    return statistic.getAverageInventoryLevel();
  }

}
