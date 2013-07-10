package de.java.ejb.jms;


import static de.java.ejb.jms.OrderMarshaller.marshalSingle;
import static de.java.util.DateFormatter.parse;

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

import de.java.domain.IllegalOrderStatusTransitionException;
import de.java.domain.OrderState;
import de.java.ejb.ReplenishmentOrderService;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/JaVaOrderActions"),
    @ActivationConfigProperty(propertyName = "DLQMaxResent", propertyValue = "10"),
})
public class IndividualOrderActionsMDB extends AbstractJmsBean implements MessageListener {

  @EJB
  ReplenishmentOrderService orderService;

  public void onMessage(Message incomingMessage) {
    try {
      switch (extractAction(incomingMessage)) {
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
    execute(new Action() {
      public void perform(long orderId, String[] contents) {
        // execute will always reply with the order corresponding to orderId
      }}, incomingMessage);
  }

  private void execute(Action action, Message incomingMessage) throws JMSException {
    Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    TextMessage textMessage = (TextMessage) incomingMessage;
    String[] contents = textMessage.getText().split(";");
    try {
      long orderId = Long.parseLong(contents[1]);
      
      action.perform(orderId, contents);
      
      replyWithCurrentOrder(incomingMessage, session, orderId);
    } finally {
      session.close();
    }
  }

  private interface Action {
    void perform(long orderId, String[] contents);
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
    execute(new Action() {
      public void perform(long orderId, String[] contents) {
        validate(orderId, OrderState.OPEN);
        orderService.proceedToNextState(orderId);
      }
    }, incomingMessage);
  }

  private void validate(long orderId, OrderState inState) {
    if (orderService.getOrder(orderId).getState() != inState) {
      throw new IllegalOrderStatusTransitionException();
    }
  }

  private void fulfillOrderAction(Message incomingMessage) throws JMSException {
    execute(new Action() {
      public void perform(long orderId, String[] contents) {
        validate(orderId, OrderState.POSTING);
        orderService.updateExpectedDeliveryDate(orderId, parse(contents[2]));
        orderService.proceedToNextState(orderId);
      }
    }, incomingMessage);
  }

  private void fulfillCancelAction(Message incomingMessage) throws JMSException {
    execute(new Action() {
      public void perform(long orderId, String[] contents) {
        orderService.cancel(orderId);
      }
    }, incomingMessage);
  }
}
