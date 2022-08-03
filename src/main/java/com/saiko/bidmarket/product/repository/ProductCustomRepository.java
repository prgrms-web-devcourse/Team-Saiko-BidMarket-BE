package com.saiko.bidmarket.product.repository;

import java.util.List;

import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;

public interface ProductCustomRepository {
  List<Product> findAllProduct(ProductSelectRequest productSelectRequest);
}
