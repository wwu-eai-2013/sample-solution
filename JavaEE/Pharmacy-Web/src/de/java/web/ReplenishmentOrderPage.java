package de.java.web;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.java.domain.OrderState;
import de.java.domain.Position;
import de.java.domain.ReplenishmentOrder;
import de.java.ejb.ReplenishmentOrderService;
import de.java.web.util.Util;

@ManagedBean
@ViewScoped
public class ReplenishmentOrderPage {

  @EJB
  private ReplenishmentOrderService orderService;

  private long id;

  private ReplenishmentOrder order;

  private Date actualDelivery;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
    init();
  }

  public void init() {
    order = null;
    setActualDelivery(new Date());
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
      order = orderService.getOrderWithPositions(id);
    }
    return order;
  }

  public String proceed() {
    if (order.getState() == OrderState.ORDERED) {
      orderService.updateActualDeliveryDate(order.getId(), getActualDelivery());
    }
    orderService.proceedToNextState(order.getId());
    init();
    return returnToOrderPage();
  }

  private String returnToOrderPage() {
    return "/replenishmentOrder/details.xhtml?faces-redirect=true&id=" + id;
  }

  public String cancel() {
    orderService.cancel(order.getId());
    init();
    return returnToOrderPage();
  }

  public String remove(Position position) {
    orderService.removePosition(position);
    if (orderService.getOrder(id) == null) {
      return "/replenishmentOrder/list.xhtml?faces-redirect=true";
    }
    init();
    return returnToOrderPage();
  }

  public void setOrder(ReplenishmentOrder order) {
    this.order = order;
  }

  public Date getActualDelivery() {
    return actualDelivery;
  }

  public void setActualDelivery(Date actualDelivery) {
    this.actualDelivery = actualDelivery;
  }
}
