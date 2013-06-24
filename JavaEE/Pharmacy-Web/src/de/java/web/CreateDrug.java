package de.java.web;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;

import de.java.domain.Drug;
import de.java.ejb.DrugService;
import de.java.ejb.drug.info.DrugInfo;
import de.java.ejb.drug.info.DrugInfoService;
import de.java.web.util.Util;

@ManagedBean
public class CreateDrug {
  private Drug drug = new Drug();
  private Drug lastDrug;
  
  private boolean batch;
  
  private String errorMessage;
  
  @EJB
  private DrugService drugService;

  @EJB
  private DrugInfoService infoService;

  public Drug getDrug() {
    return drug;
  }

  public boolean isBatch() {
    return batch;
  }

  public void setBatch(boolean batch) {
    this.batch = batch;
  }

  public void prefill() {
    try{
      DrugInfo info = infoService.getInfo(drug.getPzn());
      drug.setName(info.getName());
      drug.setDescription(info.getDescription());
      errorMessage = null;
    } catch(EJBException e){
      errorMessage = Util.getCausingMessage(e);
    }
  }

  public void pznChanged() {
    // set dummy value for name
    drug.setName("drug");
  }

  public String persist() {
    // Action
    try{
      lastDrug = drugService.createDrug(drug);
      drug = null;
      errorMessage = null;
    }
    catch(EJBException e){
      errorMessage = "Drug not created: " + Util.getCausingMessage(e);
    }
    
    // Navigation
    if(isBatch() || isError())
      return null;
    else
      return "/drug/list.xhtml";
  }

  public boolean isError() {
    return errorMessage != null;
  }
  
  public boolean isSuccess() {
    return lastDrug != null;
  }
  
  public String getLastResult(){
    if(lastDrug != null)
      return "Drug created: " + lastDrug.toString();
    return errorMessage!=null?errorMessage:"";
  }
}
