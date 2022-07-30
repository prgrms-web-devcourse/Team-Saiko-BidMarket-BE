package com.saiko.bidmarket.product.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.saiko.bidmarket.product.entity.Product;

public interface ProductCustomRepository {
  List<Product> findAllProduct(Pageable pageable);
}
