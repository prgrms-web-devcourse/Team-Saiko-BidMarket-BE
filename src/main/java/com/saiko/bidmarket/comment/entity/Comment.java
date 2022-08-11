package com.saiko.bidmarket.comment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Column(length = 500)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User writer;

  @Builder
  private Comment(
      String content,
      Product product,
      User writer
  ) {
    Assert.hasText(content, "Content must be provided");
    Assert.notNull(product, "Product must be provided");
    Assert.notNull(writer, "Writer must be provided");

    this.content = content;
    this.product = product;
    this.writer = writer;
  }

  public static Comment of(
      User writer,
      Product product,
      String content
  ) {
    return Comment
        .builder()
        .writer(writer)
        .product(product)
        .content(content)
        .build();
  }
}
