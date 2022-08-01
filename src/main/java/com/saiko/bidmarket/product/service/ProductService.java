package com.saiko.bidmarket.product.service;

import java.util.List;

import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;

public interface ProductService {

  Product findById(long id);

  Product create(Product product);

  List<Product> findAll(ProductSelectRequest productSelectRequest);
}
