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

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.user.entity.User;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Getter
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class Product extends BaseTime {
  public static final int PROGRESSION_PERIOD_OF_BIDDING = 7;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(length = 32)
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

  private String thumbnailImage;

  private boolean progressed;

  private Long winningPrice;

  @NotNull
  private LocalDateTime expireAt;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User writer;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Bidding> biddings = new ArrayList<>();

  protected Product() {
  }

  @Builder
  private Product(String title, String description, int minimumPrice, Category category,
                  String location, List<String> images, User writer) {
    Assert.hasText(title, "Title must be provided");
    Assert.hasText(description, "Description must be provided");
    Assert.notNull(writer, "Writer must be provided");

    this.title = title;
    this.description = description;
    this.minimumPrice = minimumPrice;
    this.category = category;
    this.location = location;
    this.expireAt = LocalDateTime.now().plusDays(PROGRESSION_PERIOD_OF_BIDDING);
    this.images = createImages(images);
    this.thumbnailImage = createThumbnailImage(images);
    this.writer = writer;
    this.progressed = true;
  }

  private List<Image> createImages(List<String> imageUrls) {
    if (imageUrls == null) {
      return new ArrayList<>();
    }

    AtomicInteger order = new AtomicInteger(1);
    return imageUrls.stream()
                    .map((url) -> Image.builder()
                                       .url(url)
                                       .product(this)
                                       .order(order.getAndIncrement())
                                       .build())
                    .collect(Collectors.toList());
  }

  private String createThumbnailImage(List<String> imageUrls) {
    if (imageUrls == null || imageUrls.isEmpty()) {
      return null;
    }
    return imageUrls.get(0);
  }

  public void finish(Long winningPrice) {
    Assert.notNull(winningPrice, "winningPrice must be provided");

    this.progressed = false;
    setWinningPrice(winningPrice);
  }

  private void setWinningPrice(Long winningPrice) {
    this.winningPrice = winningPrice;
  }
}

