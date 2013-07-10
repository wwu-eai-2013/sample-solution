package de.java.ejb.jms;

import static de.java.ejb.jms.MessageUnmarshaller.unmarshalAll;
import static de.java.ejb.jms.MessageUnmarshaller.unmarshalSingle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

@Stateless
public class OrderServiceBean extends AbstractJmsBean implements OrderService {

  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  @Resource(lookup = "java:/topic/OrderActions")
  private Topic orderActions;

  @Resource(lookup = "java:/queue/JaVaOrderActions")
  private Queue javaQueue;

  @Resource(lookup = "java:/queue/CSharpeOrderActions")
  private Queue csharpeQueue;

  @Override
  public Collection<ReplenishmentOrder> getAllOrders() {
    final Collection<ReplenishmentOrder> result = new ArrayList<>();
    try {
      final Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
      try {
        TextMessage message = session.createTextMessage("ALL");
        final Queue replyQueue = session.createTemporaryQueue();
        message.setJMSReplyTo(replyQueue);
        
        MessageProducer producer = session.createProducer(orderActions);
        producer.send(message);
        log.debug("Message send to topic");
        
        execute(new HasOpenConnection() {
          @Override
          public void action() throws JMSException {
            /*
             * the order of retrieval is done this way, because the response
             * from JaVa can be expected to arrive sooner (HO and JaVa run
             * on the same server) than that of C.Sharpe
             */
            {
              // retrieve orders from JaVa
              MessageConsumer consumer = session.createConsumer(replyQueue, "subsidiary='JaVa'");
              TextMessage resultMsg = (TextMessage) consumer.receive(1000);
              if (resultMsg != null) {
                result.addAll(convert(resultMsg, Subsidiary.JaVa, resultMsg.getText()));
              }
            }
            
            {
              // retrieve orders from CSharpe
              MessageConsumer consumer = session.createConsumer(replyQueue, "subsidiary='CSharpe'");
              TextMessage resultMsg = (TextMessage) consumer.receive(1000);
              if (resultMsg != null) {
                result.addAll(convert(resultMsg, Subsidiary.CSharpe, resultMsg.getText()));
              }
            }
          }
        });
      } finally {
        session.close();
      }
    } catch (JMSException e) {
      log.error("Tried to send JMS message", e);
      throw new EJBException(e);
    }
    return result;
  }

  private Collection<ReplenishmentOrder> convert(TextMessage msg,
      Subsidiary subsidiary, String messageText) {
    return unmarshalAll(messageText, subsidiary);
  }

  private void execute(HasOpenConnection requiresOpenConnection) throws JMSException {
    getConnection().start();
    try {
      requiresOpenConnection.action();
    } finally {
      getConnection().stop();
    }
  }

  private interface HasOpenConnection {
    void action() throws JMSException;
  }

  private <T> T execute(HasOpenConnectionWithTypedReturn<T> requiresOpenConnection) throws JMSException {
    getConnection().start();
    try {
      return requiresOpenConnection.action();
    } finally {
      getConnection().stop();
    }
  }
  
  private interface HasOpenConnectionWithTypedReturn<T> {
    T action() throws JMSException;
  }

  @Override
  public ReplenishmentOrder getOrder(final Subsidiary subsidiary, final long orderId) {
    return sendMessage(subsidiary, "GET;" + orderId);
  }

  private ReplenishmentOrder sendMessage(final Subsidiary subsidiary,
      final String messageToSubsidiary) {
    try {
      final Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
      try {
        TextMessage message = session.createTextMessage(messageToSubsidiary);
        final Queue replyQueue = session.createTemporaryQueue();
        message.setJMSReplyTo(replyQueue);
        
        MessageProducer producer = session.createProducer(forSubsidiary(subsidiary));
        producer.send(message);
        log.debug("Message send to topic");
        
        return execute(new HasOpenConnectionWithTypedReturn<ReplenishmentOrder>() {
          @Override
          public ReplenishmentOrder action() throws JMSException {
            MessageConsumer consumer = session.createConsumer(replyQueue);
            TextMessage resultMsg = (TextMessage) consumer.receive(1000);
            if (resultMsg == null) {
              throw new EJBException("Received no reply from subsidiary "
                  + subsidiary + " for message: " + messageToSubsidiary);
            }
            return unmarshalSingle(resultMsg.getText(), subsidiary);
          }
        });
      } finally {
        session.close();
      }
    } catch (JMSException e) {
      log.error("Tried to send JMS message", e);
      throw new EJBException(e);
    }
  }

  private Destination forSubsidiary(Subsidiary forSubsidiary) {
    switch (forSubsidiary) {
    case JaVa:
      return javaQueue;
    case CSharpe:
      return csharpeQueue;
    default:
      throw new NotImplementedException();
    }
  }

  @Override
  public ReplenishmentOrder post(ReplenishmentOrder order) {
    return sendMessage(order.getSubsidiary(), "POST;" + order.getOrderId());
  }

  @Override
  public ReplenishmentOrder order(ReplenishmentOrder order, Date expectedDelivery) {
    return sendMessage(order.getSubsidiary(), "ORDER;" + order.getOrderId()
        + ";" + formatter.format(expectedDelivery));
  }

  @Override
  public ReplenishmentOrder cancel(ReplenishmentOrder order) {
    return sendMessage(order.getSubsidiary(), "CANCEL;" + order.getOrderId());
  }

}
