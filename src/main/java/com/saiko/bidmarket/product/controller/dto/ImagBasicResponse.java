package com.saiko.bidmarket.product.controller.dto;

import com.saiko.bidmarket.product.entity.Image;

public class ImagBasicResponse {
  private final String url;
  private final int order;

  private ImagBasicResponse(
      String url,
      int order
  ) {
    this.url = url;
    this.order = order;
  }

  public static ImagBasicResponse from(Image image) {
    return new ImagBasicResponse(image.getUrl(), image.getOrder());
  }

  public String getUrl() {
    return url;
  }

  public int getOrder() {
    return order;
  }
}
