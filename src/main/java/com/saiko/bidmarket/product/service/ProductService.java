package com.saiko.bidmarket.product.service;

import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.user.entity.User;

public interface ProductService {

  Product findById(long id);

  long create(ProductCreateRequest productCreateRequest, User writer);
}
