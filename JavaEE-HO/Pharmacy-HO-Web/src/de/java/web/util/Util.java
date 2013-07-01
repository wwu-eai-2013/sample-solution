package de.java.web.util;

import java.io.IOException;
import java.util.Set;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class Util {

  public static String getCausingMessage(EJBException e) {
    String message = "";
    Exception causingException = e.getCausedByException();
    if (causingException instanceof ConstraintViolationException) {
      message += getConstraintMessages((ConstraintViolationException) causingException);
    } else
      message += e.getMessage();
    return message;
  }

  private static String getConstraintMessages(ConstraintViolationException cve) {
    String messages = "";
    Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
    if (violations != null)
      for (ConstraintViolation<?> cur : violations)
        messages += cur.getMessage() + " ";
    else
      messages += cve.getMessage();
    return messages;
  }

  public static void redirectToRoot() {
    try {
      ExternalContext ctx = FacesContext.getCurrentInstance()
          .getExternalContext();
      ctx.redirect("/" + ctx.getContextName() + "/");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static FacesMessage infoMessage(String text) {
    return new FacesMessage(FacesMessage.SEVERITY_INFO, text, text);
  }
  public static FacesMessage warnMessage(String text) {
    return new FacesMessage(FacesMessage.SEVERITY_WARN, text, text);
  }
  public static FacesMessage errorMessage(String text) {
    return new FacesMessage(FacesMessage.SEVERITY_ERROR, text, text);
  }
  public static FacesMessage successMessage(String text) {
    return new FacesMessage(FacesMessage.SEVERITY_FATAL, text, text);
  }
}
