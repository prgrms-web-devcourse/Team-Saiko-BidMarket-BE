package com.saiko.bidmarket.product.repository;

import java.util.List;

import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;

public interface ProductCustomRepository {
  List<Product> findAllProduct(ProductSelectRequest productSelectRequest);

  List<Product> findAllUserProduct(long userId, UserProductSelectRequest request);
}
