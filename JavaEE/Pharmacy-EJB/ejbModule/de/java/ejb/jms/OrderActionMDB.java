package de.java.ejb.jms;

import static de.java.ejb.jms.OrderMarshaller.marshalAll;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import de.java.ejb.ReplenishmentOrderService;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/OrderActions"),
    @ActivationConfigProperty(propertyName = "DLQMaxResent", propertyValue = "10"),
})
public class OrderActionMDB implements MessageListener {

  @Resource(lookup = "java:/JmsXA")
  private ConnectionFactory cf;

  /**
   * is initialized in PostConstruct hook and is closed gracefully by PreDestroy hook
   */
  private Connection connection;

  Logger log = Logger.getLogger(OrderActionMDB.class);

  @EJB
  ReplenishmentOrderService orderService;

  @PostConstruct
  public void initialize() {
    try {
      connection = cf.createConnection();
      log.info("Created JMS connection");
    } catch (JMSException e) {
      log.error("Could not create JMS connection", e);
    }
  }

  @PreDestroy
  public void destroy() {
    try {
      connection.close();
      log.info("Closed JMS connection");
    } catch (JMSException e) {
      log.error("Could not close JMS connection", e);
    }
  }

  /**
   * @see MessageListener#onMessage(Message)
   */
  public void onMessage(Message incomingMessage) {
    try {
      TextMessage textMessage = (TextMessage) incomingMessage;
      String content = textMessage.getText();
      if (content.equals("ALL")) {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
          TextMessage reply = session.createTextMessage();
          reply.setStringProperty("subsidiary", "JaVa");
          reply.setText(marshalAll(orderService.getAllReplenishmentOrders()));
          
          MessageProducer producer = session.createProducer(incomingMessage.getJMSReplyTo());
          producer.send(reply);
        } finally {
          session.close();
        }
      }
    } catch (JMSException e) {
      log.error("Error while processing message", e);
    }
  }

}
