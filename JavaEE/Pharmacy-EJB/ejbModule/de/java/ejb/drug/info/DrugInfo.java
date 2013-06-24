package de.java.ejb.drug.info;

import java.io.Serializable;

public class DrugInfo implements Serializable {

  private static final long serialVersionUID = 3670925650906144551L;

  private int pzn;
  private String name;
  private String description;

  public int getPzn() {
    return pzn;
  }

  public String getName() {
    return name;
  }


  public String getDescription() {
    return description;
  }

}
