package de.java.ejb.jms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.jboss.logging.Logger;

/**
 * Handles the lifecycle of a connection. Sub-classes are strongly encouraged to use {@link AbstractJmsBean#connection} when 
 */
public abstract class AbstractJmsBean {

  @Resource(lookup = "java:/ConnectionFactory")
  private ConnectionFactory cf;
  
  /**
   * is initialized in PostConstruct hook and is closed gracefully by PreDestroy hook
   */
  private Connection connection;
  
  Logger log = Logger.getLogger(AbstractJmsBean.class);

  @PostConstruct
  public void initialize() {
    try {
      connection = cf.createConnection();
      log.debug("Created JMS connection");
    } catch (JMSException e) {
      log.error("Could not create JMS connection", e);
    }
  }

  @PreDestroy
  public void destroy() {
    try {
      connection.close();
      log.debug("Closed JMS connection");
    } catch (JMSException e) {
      log.error("Could not close JMS connection", e);
    }
  }
  
  /**
   * @return Lifecycle managed instance of connection
   */
  protected Connection getConnection() {
    return connection;
  }

}
