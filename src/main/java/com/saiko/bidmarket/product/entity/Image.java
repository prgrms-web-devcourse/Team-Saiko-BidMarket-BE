package com.saiko.bidmarket.product.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.saiko.bidmarket.common.entity.BaseTime;

@Entity
public class Image extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private String url;

  private boolean isThumbnail = false;

  protected Image() {
  }
}
