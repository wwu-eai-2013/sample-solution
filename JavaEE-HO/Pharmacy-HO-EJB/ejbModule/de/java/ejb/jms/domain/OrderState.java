package de.java.ejb.jms.domain;

public enum OrderState {
  OPEN {
    public OrderState getNext() { return POSTING; }
  },
  POSTING {
    public OrderState getNext() { return ORDERED; }
    public boolean isCancellable() { return true; }
    public OrderState cancel() { return CANCELLED; }
  },
  ORDERED {
    public OrderState getNext() { return FINISHED; }
  },
  FINISHED {
    public OrderState getNext() { throw new IllegalOrderStatusTransitionException(); }
    public boolean isProceedable() { return false; }
  },
  CANCELLED {
    public OrderState getNext() { throw new IllegalOrderStatusTransitionException(); }
    public boolean isProceedable() { return false; }
  };

  public abstract OrderState getNext();

  public boolean isProceedable() {
    return true;
  }

  public boolean isCancellable() {
    return false;
  }

  public OrderState cancel() {
    throw new IllegalOrderStatusTransitionException();
  }

}
