package com.saiko.bidmarket.product.service;

import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.entity.Product;

public interface ProductService {

  Product findById(long id);

  long create(ProductCreateRequest productCreateRequest, Long userId);
}
