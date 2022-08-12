package com.saiko.bidmarket.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatMessageRepository;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultChatRoomService implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void create(Product product) {
    Assert.notNull(product, "Product must be provided");

    User seller = product.getWriter();

    userRepository
        .findWinnerOfBiddingByProductId(product.getId())
        .map(winner -> ChatRoom.of(seller, winner, product))
        .ifPresent(chatRoomRepository::save);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatRoomSelectResponse> findAll(
      long userId,
      ChatRoomSelectRequest request
  ) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(request, "Request must be provided");

    return chatRoomRepository
        .findAllByUserId(userId, request)
        .stream()
        .map(chatRoom -> ChatRoomSelectResponse.of(
                 userId,
                 chatRoom,
                 getLastMessageOfChatRoom(chatRoom.getId())
             )
        )
        .collect(Collectors.toUnmodifiableList());
  }

  private ChatMessage getLastMessageOfChatRoom(long chatRoomId) {
    return chatMessageRepository
        .findLastChatMessageOfChatRoom(chatRoomId)
        .orElse(ChatMessage.getEmptyMessage());
  }
}
