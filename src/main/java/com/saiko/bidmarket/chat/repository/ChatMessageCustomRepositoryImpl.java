package com.saiko.bidmarket.chat.repository;

import static com.saiko.bidmarket.chat.entity.QChatMessage.*;
import static com.saiko.bidmarket.chat.entity.QChatRoom.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
            .join(chatMessage.sender, user)
            .fetchJoin()
            .where(chatMessage.chatRoom.id.eq(chatRoomId))
            .orderBy(chatMessage.createdAt.desc())
            .fetchFirst()
    );
  }
}
