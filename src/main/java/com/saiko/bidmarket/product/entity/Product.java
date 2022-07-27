package com.saiko.bidmarket.product.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.user.entity.User;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User writer;

  protected Product() {
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getMinimumPrice() {
    return minimumPrice;
  }

  public Category getCategory() {
    return category;
  }

  public String getLocation() {
    return location;
  }

  public LocalDateTime getExpireAt() {
    return expireAt;
  }
}
