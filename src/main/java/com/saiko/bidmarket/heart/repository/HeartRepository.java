package com.saiko.bidmarket.heart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

public interface HeartRepository extends HeartCustomRepository, JpaRepository<Heart, Long> {
  Optional<Heart> findByUserAndProduct(User user, Product product);
}
