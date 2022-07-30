package com.saiko.bidmarket.product.entity;

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

@Entity
public class Image extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @NotNull
  private String url;

  @NotNull
  @Column(name = "\"order\"")
  private int order;

  protected Image() {
  }

  private Image(Builder builder) {
    Assert.notNull(builder.product, "Product must be provided");
    Assert.hasText(builder.url, "Url must be provided");

    this.product = builder.product;
    this.url = builder.url;
    this.order = builder.order;
  }

  public String getUrl() {
    return url;
  }

  public static class Builder {

    private Product product;
    private String url;
    private int order;

    public Builder product(Product product) {
      this.product = product;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder order(int order) {
      Assert.isTrue(1 <= order, "order must be between 1 and 5!");
      Assert.isTrue(5 >= order, "order must be between 1 and 5!");

      this.order = order;
      return this;
    }

    public Image build() {
      return new Image(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
