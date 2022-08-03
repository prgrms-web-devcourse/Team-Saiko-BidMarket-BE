package com.saiko.bidmarket.product.service;

import java.util.List;

import com.saiko.bidmarket.product.controller.dto.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;

public interface ProductService {
  ProductCreateResponse create(ProductCreateRequest productCreateRequest, Long userId);

  List<ProductSelectResponse> findAll(ProductSelectRequest productSelectRequest);

  ProductDetailResponse findById(long id);
}
