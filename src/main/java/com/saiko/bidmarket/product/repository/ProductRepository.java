package com.saiko.bidmarket.product.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.saiko.bidmarket.product.entity.Product;

public interface ProductRepository extends ProductCustomRepository, JpaRepository<Product, Long> {

  List<Product> findAllByProgressedAndExpireAtLessThan(boolean progressed, LocalDateTime nowTime);

  @Query("select p from Product p join fetch p.writer where p.id = :id")
  Optional<Product> findByIdJoinWithUser(long id);
}
