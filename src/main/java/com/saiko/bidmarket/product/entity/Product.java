package com.saiko.bidmarket.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.Basic;
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
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Formula;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  @Column(length = 20)
  private String location;

  @NotNull
  @Column(length = 512)
  private String thumbnailImage;

  private boolean progressed;

  private Long winningPrice;

  @Basic(fetch = FetchType.LAZY)
  @Formula("(select count(1) from heart h where h.product_id = id and h.actived = true)")
  private long heartCount;

  @NotNull
  private LocalDateTime expireAt;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User writer;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy(value = "biddingPrice desc")
  private List<Bidding> biddings = new ArrayList<>();

  public static final String DELETE_DESCRIPTION = "삭제된 상품입니다.";

  public static final String DELETED_TITLE = "삭제된 상품입니다.";

  @Builder
  private Product(
      String title,
      String description,
      int minimumPrice,
      Category category,
      String location,
      List<String> images,
      User writer
  ) {
    Assert.hasText(title, "Title must be provided");
    Assert.hasText(description, "Description must be provided");
    Assert.notNull(writer, "Writer must be provided");

    this.title = title;
    this.description = description;
    this.minimumPrice = minimumPrice;
    this.category = category;
    this.location = location;
    this.expireAt = LocalDateTime
        .now()
        .plusDays(PROGRESSION_PERIOD_OF_BIDDING);
    this.images = createImages(images);
    this.thumbnailImage = createThumbnailImage(images);
    this.writer = writer;
    this.progressed = true;
  }

  public static Product of(
      ProductCreateRequest request,
      User writer
  ) {
    return Product
        .builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .location(request.getLocation())
        .category(request.getCategory())
        .minimumPrice(request.getMinimumPrice())
        .images(request.getImages())
        .writer(writer)
        .build();
  }

  public boolean hasWinner() {
    return this.winningPrice != null;
  }

  public boolean isProductOfUser(long userId) {
    return this.writer.isSameUser(userId);
  }

  private List<Image> createImages(List<String> imageUrls) {
    if (imageUrls == null) {
      return new ArrayList<>();
    }

    AtomicInteger order = new AtomicInteger(1);
    return imageUrls
        .stream()
        .map((url) -> Image
            .builder()
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

  public User finish() {
    User winner = selectWinner();

    this.progressed = false;

    return winner;
  }

  private User selectWinner() {
    if (biddings.isEmpty()) {
      return null;
    }

    Bidding wonBidding = biddings.get(0);

    wonBidding.win();

    setWinningPrice();

    return wonBidding.getBidder();
  }

  private void setWinningPrice() {
    this.winningPrice =
        biddings.size() == 1 ? (long)minimumPrice : biddings
            .get(1)
            .getBiddingPrice() + 1000L;
  }

  public List<User> getBiddersExceptWinner() {
    List<User> bidders = biddings
        .stream()
        .map(Bidding::getBidder)
        .collect(Collectors.toList());

    bidders.remove(0);

    return bidders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Product product = (Product)o;
    return id != null && Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public void reportPenalty() {
    if (progressed) {
      progressed = false;
    }

    title = DELETED_TITLE;
    description = DELETE_DESCRIPTION;
    // TODO: 사진 처리
  }
}
