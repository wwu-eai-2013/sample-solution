package de.java.web;

import de.java.web.info.drug.DrugInfo;

public class EmptyDrugInfo extends DrugInfo {

  private static final long serialVersionUID = -1597926460777516863L;

  private int pzn2;

  public EmptyDrugInfo(int pzn) {
    pzn2 = pzn;
  }

  @Override
  public int getPzn() {
    return pzn2;
  }

  @Override
  public String getName() {
    return "no name available";
  }

  @Override
  public String getDescription() {
    return "no description available";
  }
}
