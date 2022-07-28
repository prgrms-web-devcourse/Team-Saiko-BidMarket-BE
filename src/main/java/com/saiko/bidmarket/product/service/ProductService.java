package com.saiko.bidmarket.product.service;

import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;

public interface ProductService {
  long create(ProductCreateRequest productCreateRequest);
}
