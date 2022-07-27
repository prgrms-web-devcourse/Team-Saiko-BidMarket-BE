package com.saiko.bidmarket.product.service;

import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;

public interface ProductService {

  Product findById(long id);

  long create(ProductCreateRequest productCreateRequest);
}
