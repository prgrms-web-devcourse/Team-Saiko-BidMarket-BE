package com.saiko.bidmarket.chat.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectResponse;
import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatMessageRepository;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class DefaultChatMessageService implements ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;

  @Override
  public ChatPublishMessage create(ChatMessageCreateParam createParam) {
    Assert.notNull(createParam, "Create Param must be provided");

    ChatRoom chatRoom = chatRoomRepository.findById(createParam.getRoomId())
                                          .orElseThrow(
                                              () -> new NotFoundException("Room not exist"));

    User sender = userRepository.findById(createParam.getUserId())
                                .orElseThrow(() -> new NotFoundException("User not exist"));

    ChatMessage chatMessage = ChatMessage.builder()
                                         .message(createParam.getContent())
                                         .sender(sender)
                                         .chatRoom(chatRoom)
                                         .build();

    ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

    return ChatPublishMessage.of(savedChatMessage);
  }

  @Override
  public List<ChatMessageSelectResponse> findAll(
      long userId,
      long chatRoomId,
      ChatMessageSelectRequest request
  ) {
    return Collections.emptyList();
  }
}
