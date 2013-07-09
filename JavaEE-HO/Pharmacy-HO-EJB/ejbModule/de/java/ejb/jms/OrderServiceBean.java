package de.java.ejb.jms;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

@Singleton
@Startup
public class OrderServiceBean extends AbstractJmsBean {

  @Resource(lookup = "java:/topic/OrderActions")
  private Topic orderActionTopic;

  @PostConstruct
  public void start() {
    while (true) {
      try {
        sendMessage();
        Thread.sleep(50000);
      } catch (InterruptedException e) {
      }
    }
  }

  private void sendMessage() {
    try {
      Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
      try {
        MessageProducer producer = session.createProducer(orderActionTopic);
        TextMessage message = session.createTextMessage("testmessage at" + new Date());
        message.setStringProperty("subsidiary", "Csharpe");
        producer.send(message);
        log.debug("Message send");
      } finally {
        session.close();
      }
    } catch (JMSException e) {
      log.error("Tried to send JMS message", e);
      throw new EJBException(e);
    }
  }
}
