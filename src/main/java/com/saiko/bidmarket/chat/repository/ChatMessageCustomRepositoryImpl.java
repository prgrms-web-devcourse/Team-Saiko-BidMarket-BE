package com.saiko.bidmarket.chat.repository;

import static com.saiko.bidmarket.chat.entity.QChatMessage.*;
import static com.saiko.bidmarket.chat.entity.QChatRoom.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.entity.ChatMessage;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatMessageCustomRepositoryImpl implements ChatMessageCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<ChatMessage> findLastChatMessageOfChatRoom(long chatRoomId) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(chatMessage)
            .join(chatMessage.chatRoom, chatRoom)
            .where(chatMessage.chatRoom.id.eq(chatRoomId))
            .orderBy(chatMessage.createdAt.desc())
            .fetchFirst()
    );
  }

  @Override
  public List<ChatMessage> findAllChatMessage(
      long chatRoomId,
      ChatMessageSelectRequest request
  ) {
    return jpaQueryFactory
        .selectFrom(chatMessage)
        .join(chatMessage.chatRoom, chatRoom)
        .where(chatMessage.chatRoom.id.eq(chatRoomId))
        .orderBy(chatMessage.createdAt.desc())
        .offset(request.getOffset())
        .limit(request.getLimit())
        .fetch();
  }
}
