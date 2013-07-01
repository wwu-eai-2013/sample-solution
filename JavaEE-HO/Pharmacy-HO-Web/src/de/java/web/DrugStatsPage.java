package de.java.web;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;

import de.java.ejb.stats.drug.AggregatedDrugStatistic;
import de.java.ejb.stats.drug.DrugStatsService;
import de.java.web.util.Util;

@ManagedBean
public class DrugStatsPage {

  @EJB
  private DrugStatsService service;

  private int pzn;
  private AggregatedDrugStatistic statistic;

  public void ensureInitialized(){
    try{
      if(getStatistic() != null)
        // Success
        return;
    } catch(EJBException e) {
      e.printStackTrace();
    }
    Util.redirectToRoot();
  }

  public int getPzn() {
    return pzn;
  }

  public void setPzn(int pzn) {
    this.pzn = pzn;
    statistic = null;
  }

  public AggregatedDrugStatistic getStatistic() {
    if (statistic == null) {
      statistic = service.getStatistic(pzn);
    }
    return statistic;
  }

}
