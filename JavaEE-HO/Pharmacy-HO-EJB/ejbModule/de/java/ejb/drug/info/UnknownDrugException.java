package de.java.ejb.drug.info;

import javax.ejb.EJBException;

public class UnknownDrugException extends EJBException {

  private static final long serialVersionUID = 341758375478497209L;

  public UnknownDrugException(String exceptionMessage) {
    super(exceptionMessage);
  }
}
