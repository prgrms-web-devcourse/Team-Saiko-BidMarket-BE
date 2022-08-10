package com.saiko.bidmarket.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.chat.service.dto.ChatRoomCreateParam;
import com.saiko.bidmarket.chat.service.dto.ChatRoomSelectParam;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultChatRoomService implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  @Override
  public long create(ChatRoomCreateParam createParam) {
    Assert.notNull(createParam, "CreateParam must be provided");

    long sellerId = createParam.getSellerId();
    long productId = createParam.getProductId();

    User seller = userRepository
        .findById(sellerId)
        .orElseThrow(() -> new NotFoundException("User not exists"));

    User winner = userRepository
        .findWinnerOfBiddingByProductId(productId)
        .orElseThrow(
            () -> new NotFoundException("Winner not exists"));

    Product product = productRepository
        .findById(createParam.getProductId())
        .orElseThrow(
            () -> new NotFoundException("Product not exists"));

    ChatRoom chatRoom = ChatRoom
        .builder()
        .seller(seller)
        .winner(winner)
        .product(product)
        .build();

    return chatRoomRepository
        .save(chatRoom)
        .getId();
  }

  @Override
  public List<ChatRoomSelectResponse> findAll(ChatRoomSelectParam selectParam) {
    return null;
  }
}
