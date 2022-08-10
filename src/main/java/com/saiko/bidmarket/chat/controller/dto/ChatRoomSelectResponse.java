package com.saiko.bidmarket.chat.controller.dto;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = PRIVATE)
@RequiredArgsConstructor(access = PRIVATE)
public class ChatRoomSelectResponse {

  @Positive
  private final long chatRoomId;

  @NotNull
  private final ProductInfo productInfo;

  @NotNull
  private final OpponentUserInfo opponentUserInfo;

  @NotBlank
  private final String lastMessage;

  @NotNull
  private final LocalDateTime lastMessageDate;

  @Valid
  @Getter
  @Builder(access = PRIVATE)
  @RequiredArgsConstructor(access = PRIVATE)
  private static class ProductInfo {

    @Positive
    private final long productId;

    @NotBlank
    private final String thumbnailImage;

    private static ProductInfo getProductInfo(Product product) {
      return ProductInfo
          .builder()
          .productId(product.getId())
          .thumbnailImage(product.getThumbnailImage())
          .build();
    }
  }

  @Valid
  @Getter
  @Builder(access = PRIVATE)
  @RequiredArgsConstructor(access = PRIVATE)
  private static class OpponentUserInfo {

    @NotBlank
    private final String username;

    @NotBlank
    private final String profileImage;

    private static OpponentUserInfo getOpponentUserInfo(User opponent) {
      return OpponentUserInfo
          .builder()
          .username(opponent.getUsername())
          .profileImage(opponent.getProfileImage())
          .build();
    }
  }

  public static ChatRoomSelectResponse of(
      long userId,
      ChatRoom chatRoom
  ) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(chatRoom, "Chat room must be provided");

    User opponent = chatRoom.getOpponentUser(userId);
    Product product = chatRoom.getProduct();

    ProductInfo productInfo = ProductInfo.getProductInfo(product);
    OpponentUserInfo opponentUserInfo = OpponentUserInfo.getOpponentUserInfo(opponent);
    ChatMessage lastMessage = chatRoom.getLastMessage();

    return ChatRoomSelectResponse
        .builder()
        .chatRoomId(chatRoom.getId())
        .productInfo(productInfo)
        .opponentUserInfo(opponentUserInfo)
        .lastMessage(lastMessage.getMessage())
        .lastMessageDate(lastMessage.getCreatedAt())
        .build();
  }

}
