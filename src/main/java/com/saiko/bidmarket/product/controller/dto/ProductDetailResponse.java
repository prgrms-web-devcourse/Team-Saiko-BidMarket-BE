package com.saiko.bidmarket.product.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.dto.UserBasicResponse;

import lombok.Getter;

@Getter
public class ProductDetailResponse {

  private final Long id;

  private final String title;

  private final String description;

  private final int minimumPrice;

  private final Category category;

  private final String location;

  private final LocalDateTime expireAt;

  private final LocalDateTime createdAt;

  private final LocalDateTime updatedAt;

  private final UserBasicResponse writer;

  private final List<ImagBasicResponse> images;

  private ProductDetailResponse(Product product) {
    // 생성을 from 함수로만 하도록 제한
    this.id = product.getId();
    this.title = product.getTitle();
    this.description = product.getDescription();
    this.minimumPrice = product.getMinimumPrice();
    this.category = product.getCategory();
    this.location = product.getLocation();
    this.expireAt = product.getExpireAt();
    this.createdAt = product.getCreatedAt();
    this.updatedAt = product.getUpdatedAt();
    this.writer = UserBasicResponse.from(product.getWriter());
    this.images = product.getImages()
                         .stream()
                         .map(ImagBasicResponse::from)
                         .collect(Collectors.toList());
  }

  public static ProductDetailResponse from(Product product) {
    return new ProductDetailResponse(product);
  }
}
