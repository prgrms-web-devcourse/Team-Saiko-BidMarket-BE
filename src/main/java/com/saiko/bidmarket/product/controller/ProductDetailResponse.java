package com.saiko.bidmarket.product.controller;

import java.time.LocalDateTime;

import com.saiko.bidmarket.product.entity.Product;

public class ProductDetailResponse {
  private final Long id;
  private final String title;
  private final String description;
  private final int minimumPrice;
  private final String categoryName;
  private final String location;
  private final LocalDateTime expireAt;
  private final LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getMinimumPrice() {
    return minimumPrice;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getLocation() {
    return location;
  }

  public LocalDateTime getExpireAt() {
    return expireAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  private final LocalDateTime updatedAt;
  
  private ProductDetailResponse(Product product){
    // 생성을 from 함수로만 하도록 제한
    this.id = product.getId();
    this.title = product.getTitle();
    this.description = product.getDescription();
    this.minimumPrice = product.getMinimumPrice();
    this.categoryName = product.getCategory().getDisplayName();
    this.location = product.getLocation();
    this.expireAt = product.getExpireAt();
    this.createdAt = product.getCreatedAt();
    this.updatedAt = product.getUpdatedAt();
  }

  public static ProductDetailResponse from(Product product) {
    return new ProductDetailResponse(product);
  }
}
