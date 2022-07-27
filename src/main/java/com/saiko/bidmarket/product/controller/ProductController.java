package com.saiko.bidmarket.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.product.service.ProductService;

@Validated
@RestController
@RequestMapping("api/v1/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ProductDetailResponse findById(@PathVariable long id) {
    return ProductDetailResponse.from(productService.findById(id));
  }
}
