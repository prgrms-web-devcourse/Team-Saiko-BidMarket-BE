package com.saiko.bidmarket.product.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.product.entity.Product;

public interface ProductRepository extends ProductCustomRepository, JpaRepository<Product, Long> {

  List<Product> findAllByProgressedAndExpireAtLessThan(boolean progressed, LocalDateTime nowTime);
}
