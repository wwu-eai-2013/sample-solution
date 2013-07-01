package de.java.web.util;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Workaround, because Tomcat does not interpret a context parameter
 * <code>javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL = true</code>
 * correctly.
 * <br/>
 * Without the following configuration, this only works for the validation phase. 
 * When assigning the value to the backing bean, a null string is still coerced to "".
 * <br/>
 * To avoid this, the system property org.apache.el.parser.COERCE_TO_ZERO has to be set to false at
 * server startup. In Eclipse, double-click on the server in the Servers view, click "Open
 * launch configuration" and add the argument <code>-Dorg.apache.el.parser.COERCE_TO_ZERO=false</code>
 * to the VM arguments.
 * 
 * @author Henning Heitkoetter
 */
public class EmptyStringToNullConverter implements Converter {
  public Object getAsObject(FacesContext facesContext, UIComponent component,
      String value) {
    if (value != null && value.isEmpty()) {
      if (component instanceof EditableValueHolder) {
        ((EditableValueHolder) component).setSubmittedValue(null);
      }
      value = null;
    }
    return value;
  }

  public String getAsString(FacesContext facesContext, UIComponent component,
      Object value) {
    return (value != null) ? value.toString() : null;
  }
}
