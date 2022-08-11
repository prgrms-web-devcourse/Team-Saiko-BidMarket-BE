package com.saiko.bidmarket.product.service;

import java.time.LocalDateTime;
import java.util.List;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.product.controller.dto.BiddingResultResponse;
import com.saiko.bidmarket.product.controller.dto.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;

public interface ProductService {
  ProductCreateResponse create(
      ProductCreateRequest productCreateRequest,
      long userId
  );

  List<ProductSelectResponse> findAll(ProductSelectRequest productSelectRequest);

  ProductDetailResponse findById(long id);

  List<Product> findAllThatNeedToClose(LocalDateTime nowTime);

  void executeClosingProduct(Product product);

  BiddingResultResponse getBiddingResult(
      UnsignedLong productId,
      UnsignedLong userId
  );
}
