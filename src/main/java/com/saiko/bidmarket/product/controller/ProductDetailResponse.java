package com.saiko.bidmarket.product.controller;

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.saiko.bidmarket.product.controller.dto.ImagBasicResponse;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.dto.UserBasicResponse;

public class ProductDetailResponse {
  private final Long id;
  private final String title;
  private final String description;
  private final int minimumPrice;
  private final String categoryName;
  private final String location;
  private final LocalDateTime expireAt;
  private final LocalDateTime createdAt;
  private final UserBasicResponse writer;
  private final List<ImagBasicResponse> imageUrls;

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

  public UserBasicResponse getWriter() {
    return writer;
  }

  public List<ImagBasicResponse> getImageUrls() {
    return imageUrls;
  }

  private final LocalDateTime updatedAt;

  private ProductDetailResponse(Product product) {
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
    this.writer = UserBasicResponse.from(product.getWriter());
    this.imageUrls = product.getImages()
                            .stream()
                            .map(ImagBasicResponse::from)
                            .collect(Collectors.toList());
  }

  public static ProductDetailResponse from(Product product) {
    return new ProductDetailResponse(product);
  }
}
