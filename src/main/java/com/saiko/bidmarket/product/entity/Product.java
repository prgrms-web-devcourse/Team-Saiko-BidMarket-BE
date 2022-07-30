package com.saiko.bidmarket.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.user.entity.User;

@Entity
public class Product extends BaseTime {
  public static final int PROGRESSION_PERIOD_OF_BIDDING = 7;

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

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User writer;

  protected Product() {
  }

  private Product(Builder builder) {
    Assert.hasText(builder.title, "Title must be provided");
    Assert.hasText(builder.description, "Description must be provided");
    Assert.notNull(builder.writer, "Writer must be provided");

    this.title = builder.title;
    this.description = builder.description;
    this.minimumPrice = builder.minimumPrice;
    this.category = builder.category;
    this.location = builder.location;
    this.expireAt = LocalDateTime.now().plusDays(PROGRESSION_PERIOD_OF_BIDDING);
    this.images = createImages(builder.images);
    this.writer = builder.writer;
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

  public List<Image> getImages() {
    return images;
  }

  public User getWriter() {
    return writer;
  }

  public static class Builder {

    private String title;
    private String description;
    private int minimumPrice;
    private Category category;
    private String location;
    private List<String> images;
    private User writer;

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder minimumPrice(int minimumPrice) {
      this.minimumPrice = minimumPrice;
      return this;
    }

    public Builder category(Category category) {
      this.category = category;
      return this;
    }

    public Builder location(String location) {
      this.location = location;
      return this;
    }

    public Builder images(List<String> images) {
      this.images = images;
      return this;
    }

    public Builder writer(User writer) {
      this.writer = writer;
      return this;
    }

    public Product build() {
      return new Product(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private List<Image> createImages(List<String> imageUrls) {
    AtomicInteger order = new AtomicInteger(1);
    return imageUrls.stream()
             .map((url) -> Image.builder()
                                .url(url)
                                .product(this)
                                .order(order.getAndIncrement())
                                .build())
             .collect(Collectors.toList());
  }
}

