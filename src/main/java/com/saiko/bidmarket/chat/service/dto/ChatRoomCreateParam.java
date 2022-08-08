package com.saiko.bidmarket.chat.service.dto;

import javax.validation.constraints.Positive;

import com.saiko.bidmarket.product.entity.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomCreateParam {

  @Positive
  private final long productId;

  @Positive
  private final long sellerId;

  public static ChatRoomCreateParam from(Product product) {
    return ChatRoomCreateParam.builder()
                              .productId(product.getId())
                              .sellerId(product.getWriter().getId())
                              .build();
  }
}
