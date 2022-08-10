package com.saiko.bidmarket.chat.repository;

import static com.saiko.bidmarket.chat.entity.QChatRoom.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.entity.ChatRoom;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<ChatRoom> findAllByUserId(
      long userId,
      ChatRoomSelectRequest request
  ) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(request, "Request must be provided");

    return jpaQueryFactory
        .selectFrom(chatRoom)
        .where(
            chatRoom.seller.id
                .eq(userId)
                .or(chatRoom.winner.id.eq(userId))
        )
        .join(chatRoom.seller, user)
        .fetchJoin()
        .join(chatRoom.winner, user)
        .fetchJoin()
        .join(chatRoom.product, product)
        .fetchJoin()
        .offset(request.getOffset())
        .limit(request.getLimit())
        .fetch();
  }
}
