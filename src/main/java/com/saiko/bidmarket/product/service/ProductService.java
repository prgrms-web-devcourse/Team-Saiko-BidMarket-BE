package com.saiko.bidmarket.product.service;

import java.time.LocalDateTime;
import java.util.List;

import com.saiko.bidmarket.product.controller.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;

public interface ProductService {
  ProductCreateResponse create(ProductCreateRequest productCreateRequest, Long userId);

  List<ProductSelectResponse> findAll(ProductSelectRequest productSelectRequest);

  ProductDetailResponse findById(long id);

  List<Product> findAllThatNeedToClose(LocalDateTime start, LocalDateTime end);

  void executeClosingProduct(List<Product> productsInProgress);
}
