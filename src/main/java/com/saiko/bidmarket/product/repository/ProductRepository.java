package com.saiko.bidmarket.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.product.entity.Product;

public interface ProductRepository extends ProductCustomRepository, JpaRepository<Product, Long> {
}
