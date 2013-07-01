package de.java.web;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;

import de.java.ejb.drug.info.DrugInfoService;
import de.java.ejb.drug.info.UnknownDrugException;
import de.java.ejb.stats.drug.AggregatedDrugStatistic;
import de.java.ejb.stats.drug.DrugStatsService;
import de.java.web.info.drug.DrugInfo;
import de.java.web.util.Util;

@ManagedBean
public class DrugStatsPage {

  @EJB
  private DrugStatsService service;

  @EJB
  private DrugInfoService drugInfoService;

  private int pzn;
  private DrugInfo info;
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
    info = null;
  }

  public AggregatedDrugStatistic getStatistic() {
    if (statistic == null) {
      statistic = service.getStatistic(pzn);
    }
    return statistic;
  }

  public DrugInfo getDrug() {
    if (info == null) {
      try {
        info = drugInfoService.getInfo(pzn);
      } catch (UnknownDrugException e) {
        info = new EmptyDrugInfo(pzn);
      }
    }
    return info;
  }

}
