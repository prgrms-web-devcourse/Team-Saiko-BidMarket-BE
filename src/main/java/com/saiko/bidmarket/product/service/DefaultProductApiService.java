package com.saiko.bidmarket.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.product.controller.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.service.UserService;

@Service
@Transactional
public class DefaultProductApiService implements ProductApiService {

  private final ProductService productService;
  private final UserService userService;

  public DefaultProductApiService(ProductService productService,
                                  UserService userService) {
    this.productService = productService;
    this.userService = userService;
  }

  @Override
  public ProductCreateResponse create(ProductCreateRequest productCreateRequest, Long userId) {
    Assert.notNull(productCreateRequest, "Request must be provided");
    Assert.isTrue(userId > 0, "User id must be positive");

    final User writer = userService.findById(userId);
    final Product product = Product.builder()
                                   .title(productCreateRequest.getTitle())
                                   .description(productCreateRequest.getDescription())
                                   .location(productCreateRequest.getLocation())
                                   .category(productCreateRequest.getCategory())
                                   .minimumPrice(productCreateRequest.getMinimumPrice())
                                   .images(productCreateRequest.getImages())
                                   .writer(writer)
                                   .build();

    final Product savedProduct = productService.create(product);
    return ProductCreateResponse.from(savedProduct.getId());
  }

  @Override
  public List<ProductSelectResponse> findAll(ProductSelectRequest productSelectRequest) {
    Assert.notNull(productSelectRequest, "Request must be provided");

    return productService.findAll(productSelectRequest)
                         .stream()
                         .map(ProductSelectResponse::from)
                         .collect(Collectors.toList());
  }

  @Override
  public ProductDetailResponse findById(long id) {
    Assert.isTrue(id > 0, "Id must be positive");

    final Product foundProduct = productService.findById(id);
    return ProductDetailResponse.from(foundProduct);
  }
}

