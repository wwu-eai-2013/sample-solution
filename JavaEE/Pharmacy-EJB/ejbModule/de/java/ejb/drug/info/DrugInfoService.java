package de.java.ejb.drug.info;

import javax.ejb.Remote;

@Remote
public interface DrugInfoService {

  public DrugInfo getInfo(int pzn);

}
