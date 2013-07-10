package de.java.ejb.jms;

import static de.java.ejb.jms.OrderMarshaller.marshalAll;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import de.java.ejb.ReplenishmentOrderService;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/OrderActions"),
    @ActivationConfigProperty(propertyName = "DLQMaxResent", propertyValue = "10"),
})
public class OrderActionMDB extends AbstractJmsBean implements MessageListener {

  @EJB
  ReplenishmentOrderService orderService;

  public void onMessage(Message incomingMessage) {
    try {
      TextMessage textMessage = (TextMessage) incomingMessage;
      String content = textMessage.getText();
      if (content.equals("ALL")) {
        Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
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
