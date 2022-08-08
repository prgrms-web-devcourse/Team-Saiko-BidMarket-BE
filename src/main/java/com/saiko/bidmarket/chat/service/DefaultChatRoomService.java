package com.saiko.bidmarket.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.chat.service.dto.ChatRoomCreateParam;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultChatRoomService implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;

  @Override
  public long create(ChatRoomCreateParam createParam) {
    Assert.notNull(createParam, "CreateParam must be provided");

    long sellerId = createParam.getSellerId();
    long productId = createParam.getProductId();

    User seller = userRepository.findById(sellerId)
                                .orElseThrow(() -> new NotFoundException("User not exists"));

    User winner = userRepository.findWinnerOfBiddingByProductId(productId)
                                .orElseThrow(
                                    () -> new NotFoundException("Winner not exists"));

    ChatRoom chatRoom = ChatRoom.builder()
                                .seller(seller)
                                .winner(winner)
                                .build();

    return chatRoomRepository.save(chatRoom).getId();
  }
}
