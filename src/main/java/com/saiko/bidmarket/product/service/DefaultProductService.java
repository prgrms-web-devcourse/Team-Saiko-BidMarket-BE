package com.saiko.bidmarket.product.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.Repository.ProductRepository;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;

@Service
public class DefaultProductService implements ProductService {
  private final ProductRepository productRepository;

  public DefaultProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Product findById(long id) {
    Assert.isTrue(0 < id, "id must be positive number!");

    return productRepository.findById(id).orElseThrow(NotFoundException::new);
  }

  @Override
  public long create(ProductCreateRequest productCreateRequest) {
    return 0;
  }
}

