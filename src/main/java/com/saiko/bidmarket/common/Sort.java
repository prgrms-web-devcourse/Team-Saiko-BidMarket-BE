package com.saiko.bidmarket.common;

import com.querydsl.core.types.Order;

public enum Sort {
  END_DATE_ASC("expireAt", Order.ASC),
  MINIMUM_PRICE_ASC("minimumPrice", Order.ASC),
  MINIMUM_PRICE_DESC("minimumPrice", Order.DESC),
  CREATED_AT_DESC("createdAt", Order.DESC),
  CREATED_AT_ASC("createdAt", Order.ASC);

  private final String property;
  private final Order order;

  Sort(
      String property,
      Order order
  ) {
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
