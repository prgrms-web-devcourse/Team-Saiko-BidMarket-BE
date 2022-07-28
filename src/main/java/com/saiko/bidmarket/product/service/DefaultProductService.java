package com.saiko.bidmarket.product.service;

import org.springframework.stereotype.Service;

import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;

@Service
public class DefaultProductService implements ProductService {
  @Override
  public long create(ProductCreateRequest productCreateRequest) {
    return 0;
  }
}

