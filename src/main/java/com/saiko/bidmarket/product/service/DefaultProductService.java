package com.saiko.bidmarket.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;

@Service
@Transactional
public class DefaultProductService implements ProductService {

  private final ProductRepository productRepository;

  public DefaultProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Product findById(long id) {
    Assert.isTrue(0 < id, "id must be positive number!");

    return productRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Product not exist"));
  }

  @Override
  public Product create(Product product) {
    Assert.notNull(product, "Product must be provided");

    return productRepository.save(product);
  }

  @Override
  public List<Product> findAll(ProductSelectRequest productSelectRequest) {
    Assert.notNull(productSelectRequest, "ProductSelectRequest must be provided");
    return productRepository.findAllProduct(productSelectRequest);
  }
}
