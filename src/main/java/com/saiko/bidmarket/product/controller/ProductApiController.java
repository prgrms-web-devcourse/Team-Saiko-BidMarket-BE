package com.saiko.bidmarket.product.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.service.ProductService;

@RestController
@RequestMapping("api/v1/products")
public class ProductApiController {
  private final ProductService productService;

  public ProductApiController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  // TODO: 로그인한 유저 정보 필요
  public ProductCreateResponse create(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @RequestBody @Valid ProductCreateRequest productCreateRequest) {
    long productId = productService.create(productCreateRequest, authentication.getUserId());
    return ProductCreateResponse.from(productId);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ProductDetailResponse findById(@PathVariable long id) {
    return ProductDetailResponse.from(productService.findById(id));
  }
}
