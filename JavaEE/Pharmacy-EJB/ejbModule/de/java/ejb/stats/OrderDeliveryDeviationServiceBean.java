package de.java.ejb.stats;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.java.web.api.OrderStatistic;

@Stateless
public class OrderDeliveryDeviationServiceBean implements
    OrderDeliveryDeviationService {

  @PersistenceContext
  private EntityManager em;

  public Collection<OrderStatistic> getAllStats() {
    List<OrderStatistic> resultList = em
        .createQuery(
            // subtracting two dates automatically yields the difference in seconds
            "select new de.java.web.api.OrderStatistic(o.id, o.actualDelivery - o.expectedDelivery)\n"
                + "from ReplenishmentOrder o\n"
                + "where o.state = de.java.domain.OrderState.FINISHED", OrderStatistic.class)
        .getResultList();
    return resultList;
  }

  public OrderStatistic getStatistic(long orderId) {
    return em
        .createQuery(
            // subtracting two dates automatically yields the difference in seconds
            "select new de.java.web.api.OrderStatistic(o.id, o.actualDelivery - o.expectedDelivery)\n"
                + "from ReplenishmentOrder o\n"
                + "where o.state = de.java.domain.OrderState.FINISHED\n"
                + "  and o.id = :orderId", OrderStatistic.class)
        .setParameter("orderId", orderId).getSingleResult();
  }

}
