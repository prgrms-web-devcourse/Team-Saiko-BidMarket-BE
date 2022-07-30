package com.saiko.bidmarket.product;

import com.querydsl.core.types.Order;

public enum Sort {
  END_DATE_ASC("expireAt", Order.ASC);

  private final String property;
  private final Order order;

  Sort(String property, Order order) {
    this.property = property;
    this.order = order;
  }

  public String getProperty() {
    return property;
  }

  public Order getOrder() {
    return order;
  }
}
