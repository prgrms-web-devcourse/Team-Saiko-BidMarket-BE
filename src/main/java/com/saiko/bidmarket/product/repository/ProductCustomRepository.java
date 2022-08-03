package com.saiko.bidmarket.product.repository;

import java.util.List;

import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.dto.UserProductSelectQueryParameter;

public interface ProductCustomRepository {
  List<Product> findAllProduct(ProductSelectRequest productSelectRequest);

  List<Product> findAllUserProduct(UserProductSelectQueryParameter userProductSelectQueryParameter);
}
