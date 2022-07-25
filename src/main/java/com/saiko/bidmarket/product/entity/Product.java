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
import com.sun.istack.NotNull;

@Entity
public class Product extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(length = 16)
  private String title;

  @NotNull
  @Column(length = 500)
  private String description;

  @NotNull
  private int minimumPrice;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Category category;

  private String location;

  @NotNull
  private LocalDateTime expireAt;

  protected Product() {
  }
}
