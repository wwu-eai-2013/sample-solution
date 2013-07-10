package de.java.ejb.jms;


import static de.java.ejb.jms.OrderMarshaller.marshalSingle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import de.java.ejb.ReplenishmentOrderService;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/JaVaOrderActions"),
    @ActivationConfigProperty(propertyName = "DLQMaxResent", propertyValue = "10"),
})
public class IndividualOrderActionsMDB extends AbstractJmsBean implements MessageListener {

  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  @EJB
  ReplenishmentOrderService orderService;

  public void onMessage(Message incomingMessage) {
    try {
      String action = extractAction(incomingMessage);
      switch (action) {
      case "GET":
        fulfillGetAction(incomingMessage);
        break;
      case "POST":
        fulfillPostAction(incomingMessage);
        break;
      case "ORDER":
        fulfillOrderAction(incomingMessage);
        break;
      case "CANCEL":
        fulfillCancelAction(incomingMessage);
        break;
      default:
        throw new NotImplementedException();
      } 
    } catch (JMSException e) {
      log.error("Error while processing message", e);
    }
  }
  
  private String extractAction(Message fromMessage) throws JMSException {
    return ((TextMessage) fromMessage).getText().split(";")[0];
  }

  private void fulfillGetAction(Message incomingMessage) throws JMSException {
    Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    TextMessage textMessage = (TextMessage) incomingMessage;
    String[] contents = textMessage.getText().split(";");
    try {
      long orderId = Long.parseLong(contents[1]);
      replyWithCurrentOrder(incomingMessage, session, orderId);
    } finally {
      session.close();
    }
  }
  
  private void replyWithCurrentOrder(Message incomingMessage, Session session,
      long orderId) throws JMSException {
    TextMessage reply = session.createTextMessage();
    reply.setText(marshalSingle(orderService.getOrderWithPositions(orderId)));

    MessageProducer producer = session.createProducer(incomingMessage
        .getJMSReplyTo());
    producer.send(reply);
  }

  private void fulfillPostAction(Message incomingMessage) throws JMSException {
    Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    TextMessage textMessage = (TextMessage) incomingMessage;
    String[] contents = textMessage.getText().split(";");
    try {
      long orderId = Long.parseLong(contents[1]);
      orderService.proceedToNextState(orderId);
      replyWithCurrentOrder(incomingMessage, session, orderId);
    } finally {
      session.close();
    }
  }

  private void fulfillOrderAction(Message incomingMessage) throws JMSException {
    Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    TextMessage textMessage = (TextMessage) incomingMessage;
    String[] contents = textMessage.getText().split(";");
    try {
      long orderId = Long.parseLong(contents[1]);
      orderService.updateExpectedDeliveryDate(orderId, parseDate(contents[2]));
      orderService.proceedToNextState(orderId);
      replyWithCurrentOrder(incomingMessage, session, orderId);
    } finally {
      session.close();
    }
  }

  private Date parseDate(String from) {
    try {
      return formatter.parse(from);
    } catch (ParseException e) {
      throw new RuntimeException("Could not parse from : " + from, e);
    }
  }

  private void fulfillCancelAction(Message incomingMessage) throws JMSException {
    Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    TextMessage textMessage = (TextMessage) incomingMessage;
    String[] contents = textMessage.getText().split(";");
    try {
      long orderId = Long.parseLong(contents[1]);
      orderService.cancel(orderId);
      replyWithCurrentOrder(incomingMessage, session, orderId);
    } finally {
      session.close();
    }
  }
}
