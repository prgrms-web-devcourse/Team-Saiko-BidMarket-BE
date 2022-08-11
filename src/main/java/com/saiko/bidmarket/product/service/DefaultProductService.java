package com.saiko.bidmarket.product.service;

import static com.saiko.bidmarket.notification.NotificationType.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.bidding.repository.dto.BiddingPriceFindingRepoDto;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.notification.event.NotificationCreateEvent;
import com.saiko.bidmarket.product.controller.dto.BiddingResultResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class DefaultProductService implements ProductService {

  private final ProductRepository productRepository;

  private final UserRepository userRepository;

  private final BiddingRepository biddingRepository;

  private final ChatRoomRepository chatRoomRepository;

  private final ApplicationEventPublisher publisher;

  @Override
  @Transactional
  public ProductCreateResponse create(
      ProductCreateRequest productCreateRequest,
      long userId
  ) {
    Assert.notNull(productCreateRequest, "Request must be provided");
    Assert.isTrue(userId > 0, "User id must be positive");

    final User writer = userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException("Product not exist"));

    final Product product = Product.from(productCreateRequest, writer);

    return ProductCreateResponse.from(productRepository
                                          .save(product)
                                          .getId());
  }

  @Override
  public List<ProductSelectResponse> findAll(ProductSelectRequest productSelectRequest) {
    Assert.notNull(productSelectRequest, "ProductSelectRequest must be provided");
    return productRepository
        .findAllProduct(productSelectRequest)
        .stream()
        .map(ProductSelectResponse::from)
        .collect(Collectors.toList());
  }

  @Override
  public ProductDetailResponse findById(long id) {
    Assert.isTrue(id > 0, "Id must be positive");

    return ProductDetailResponse.from(productRepository
                                          .findById(id)
                                          .orElseThrow(() -> new NotFoundException(
                                              "Product not exist")));
  }

  @Override
  public List<Product> findAllThatNeedToClose(LocalDateTime nowTime) {
    Assert.notNull(nowTime, "nowTime must be provided");

    return productRepository.findAllByProgressedAndExpireAtLessThan(true, nowTime);
  }

  @Override
  public void executeClosingProduct(Product product) {
    Assert.notNull(product, "Product must be provided");

    User winner = product.finish();

    if (winner == null) {
      publisher.publishEvent(NotificationCreateEvent
                                 .builder()
                                 .user(product.getWriter())
                                 .notificationType(
                                     END_PRODUCT_FOR_WRITER_NOT_WITH_WINNER)
                                 .product(product));
    } else {
      publisher.publishEvent(NotificationCreateEvent
                                 .builder()
                                 .user(product.getWriter())
                                 .notificationType(
                                     END_PRODUCT_FOR_WRITER_WITH_WINNER)
                                 .product(product));

      publisher.publishEvent(NotificationCreateEvent
                                 .builder()
                                 .user(winner)
                                 .notificationType(
                                     END_PRODUCT_FOR_WINNER)
                                 .product(product));
      product
          .getBiddersExceptWinner()
          .forEach(bidder ->
                       publisher.publishEvent(NotificationCreateEvent
                                                  .builder()
                                                  .user(bidder)
                                                  .notificationType(
                                                      END_PRODUCT_FOR_BIDDER)
                                                  .product(product)));
    }
  }

  @Override
  public BiddingResultResponse getBiddingResult(
      UnsignedLong productId,
      UnsignedLong userId
  ) {
    Assert.notNull(productId, "ProductId must be provided");
    Assert.notNull(userId, "UserId must be provided");

    Product product = productRepository
        .findByIdJoinWithUser(productId.getValue())
        .orElseThrow(() -> new NotFoundException(
            "Product not exist"));

    Optional<ChatRoom> chatRoom = chatRoomRepository.findByProduct_IdAndSeller_Id(
        product.getId(),
        product
            .getWriter()
            .getId()
    );

    if (isSeller(product, userId.getValue())) {
      return generateResponseForSeller(product, chatRoom);
    }

    return generateResponseForBidder(productId, userId, chatRoom);
  }

  private boolean isSeller(
      Product product,
      long userId
  ) {
    return product.isProductOfUser(userId);
  }

  private BiddingResultResponse generateResponseForSeller(
      Product product,
      Optional<ChatRoom> optionalChatRoom
  ) {
    if (product.hasWinner()) {
      ChatRoom chatRoom = verifyChatRoom(optionalChatRoom);
      return BiddingResultResponse.responseForSuccessfulSeller(
          UnsignedLong.valueOf(chatRoom.getId()));
    }
    return BiddingResultResponse.responseForFailedSeller();
  }

  private BiddingResultResponse generateResponseForBidder(
      UnsignedLong productId,
      UnsignedLong userId,
      Optional<ChatRoom> optionalChatRoom
  ) {
    Bidding biddingOfUser = findBiddingOfUser(productId, userId);
    if (biddingOfUser.isWon()) {
      ChatRoom chatRoom = verifyChatRoom(optionalChatRoom);
      return BiddingResultResponse.responseForSuccessfulBidder(
          UnsignedLong.valueOf(chatRoom.getId()));
    }
    return BiddingResultResponse.responseForFailedBidder();
  }

  private Bidding findBiddingOfUser(
      UnsignedLong productId,
      UnsignedLong userId
  ) {
    BiddingPriceFindingRepoDto request = BiddingPriceFindingRepoDto
        .builder()
        .bidderId(userId)
        .productId(productId)
        .build();
    return biddingRepository
        .findByBidderIdAndProductId(request)
        .orElseThrow(() -> new NotFoundException("Bidding not exist"));
  }

  private ChatRoom verifyChatRoom(Optional<ChatRoom> optionalChatRoom) {
    return optionalChatRoom.orElseThrow(
        () -> new NotFoundException("ChatRoom not exist"));
  }
}
