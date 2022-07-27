package com.saiko.bidmarket.product.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
