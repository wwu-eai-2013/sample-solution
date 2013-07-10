package de.java.web;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.java.ejb.jms.OrderService;
import de.java.ejb.jms.domain.OrderState;
import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;
import de.java.web.util.Util;

@ManagedBean
@ViewScoped
public class ReplenishmentOrderPage {

  @EJB
  private OrderService orderService;

  private long orderId;
  private Subsidiary subsidiary;

  private ReplenishmentOrder order;
  private Date expectedDelivery;

  private String errorMessage;

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
    init();
  }

  public Subsidiary getSubsidiary() {
    return subsidiary;
  }

  public void setSubsidiary(Subsidiary subsidiary) {
    this.subsidiary = subsidiary;
  }

  public void init() {
    order = null;
    setExpectedDelivery(new Date());
  }

  public void ensureInitialized(){
    try{
      if(getOrder() != null)
        // Success
        return;
    } catch(EJBException e) {
      e.printStackTrace();
    }
    Util.redirectToRoot();
  }

  public ReplenishmentOrder getOrder() {
    if (order == null) {
      order = orderService.getOrder(subsidiary, orderId);
    }
    return order;
  }

  public void proceed() {
    try {
      // without temporarily keeping the order state,
      // orders would automatically proceed to ORDERED
      OrderState currentState = order.getState();
      if (currentState == OrderState.OPEN) {
        order = orderService.post(order);
      }
      if (currentState == OrderState.POSTING) {
        order = orderService.order(order, getExpectedDelivery());
      }
      errorMessage = null;
    } catch (EJBException e) {
      errorMessage = Util.getCausingMessage(e);
    }
  }

  public void cancel() {
    try {
      order = orderService.cancel(order);
      errorMessage = null;
    } catch (EJBException e) {
      errorMessage = Util.getCausingMessage(e);
    }
  }

  public void setOrder(ReplenishmentOrder order) {
    this.order = order;
  }

  public Date getExpectedDelivery() {
    return expectedDelivery;
  }

  public void setExpectedDelivery(Date expectedDelivery) {
    this.expectedDelivery = expectedDelivery;
  }

  public boolean isError() {
    return errorMessage != null;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

}
