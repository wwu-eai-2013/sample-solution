package de.java.ejb.jms;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import de.java.ejb.jms.domain.ReplenishmentOrder;
import de.java.ejb.jms.domain.Subsidiary;

@Stateless
public class OrderServiceBean extends AbstractJmsBean implements OrderService {

  @Resource(lookup = "java:/topic/OrderActions")
  private Topic orderActions;

  @Resource(lookup = "java:/queue/JaVaOrderActions")
  private Queue javaQueue;

  @Resource(lookup = "java:/queue/CSharpeOrderActions")
  private Queue csharpeQueue;

  @PostConstruct
  public void testAllOrders() {
    Collection<ReplenishmentOrder> allOrders = getAllOrders();
    System.out.println("retrieved " + allOrders.size() + " orders");
  }

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
    return MessageUnmarshaller.unmarshalAll(messageText, subsidiary);
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

}
