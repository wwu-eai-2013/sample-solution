package de.java.ejb.drug.info;

import javax.ejb.Remote;

import de.java.web.info.drug.DrugInfo;

@Remote
public interface DrugInfoService {

  public DrugInfo getInfo(int pzn);

}
