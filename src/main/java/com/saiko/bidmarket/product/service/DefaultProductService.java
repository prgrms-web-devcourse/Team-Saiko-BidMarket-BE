package com.saiko.bidmarket.product.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.service.UserService;

@Service
public class DefaultProductService implements ProductService {

  private final ProductRepository productRepository;
  private final UserService userService;

  public DefaultProductService(
      ProductRepository productRepository, UserService userService) {
    this.productRepository = productRepository;
    this.userService = userService;
  }

  @Override
  public Product findById(long id) {
    Assert.isTrue(0 < id, "id must be positive number!");

    return productRepository.findById(id).orElseThrow(NotFoundException::new);
  }

  @Override
  public long create(ProductCreateRequest productCreateRequest, Long userId) {
    Assert.notNull(productCreateRequest, "ProductCreateRequest must be provided");
    Assert.notNull(userId, "userId must be provided ");

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
    return productRepository.save(product).getId();
  }
}

