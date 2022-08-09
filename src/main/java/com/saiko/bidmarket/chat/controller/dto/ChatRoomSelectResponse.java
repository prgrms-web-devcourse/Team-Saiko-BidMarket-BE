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
    private final String thumbnailImg;
  }

  @Valid
  @Getter
  @Builder(access = PRIVATE)
  @RequiredArgsConstructor(access = PRIVATE)
  private static class OpponentUserInfo {

    @NotBlank
    private final String username;

    @NotBlank
    private final String profileImg;
  }

  public static ChatRoomSelectResponse of(
      ChatRoom chatRoom,
      User opponent,
      ChatMessage lastMessage
  ) {
    Assert.notNull(chatRoom, "Chat room must be provided");
    Assert.notNull(opponent, "Opponent must be provided");
    Assert.notNull(lastMessage, "Last message must be provided");

    ProductInfo productInfo = ProductInfo.builder()
                                         .productId(chatRoom.getProduct().getId())
                                         .thumbnailImg(chatRoom.getProduct().getThumbnailImage())
                                         .build();

    OpponentUserInfo opponentUserInfo = OpponentUserInfo.builder()
                                                        .username(opponent.getUsername())
                                                        .profileImg(opponent.getProfileImage())
                                                        .build();

    return ChatRoomSelectResponse.builder()
                                 .chatRoomId(chatRoom.getId())
                                 .productInfo(productInfo)
                                 .opponentUserInfo(opponentUserInfo)
                                 .lastMessage(lastMessage.getMessage())
                                 .lastMessageDate(lastMessage.getCreatedAt())
                                 .build();
  }

}
