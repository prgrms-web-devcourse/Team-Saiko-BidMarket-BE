package com.saiko.bidmarket.product.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.Category;

@Entity
public class Product extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 16, nullable = false)
  private String title;

  @Column(length = 500, nullable = false)
  private String description;

  @Column(nullable = false)
  private int minimumPrice;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;

  private String location;

  @Column(nullable = false)
  private LocalDateTime expireDate;

  protected Product() {
  }
}
