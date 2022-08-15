package com.saiko.bidmarket.notification.repository;

import static com.saiko.bidmarket.notification.entity.QNotification.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<NotificationRepoDto> findAllNotification(
      long userId,
      NotificationSelectRequest request
  ) {
    Assert.notNull(request, "Request must be provided");

    return jpaQueryFactory
        .select(
            Projections.constructor(
                NotificationRepoDto.class, notification.id, product.id,
                product.title, product.thumbnailImage, notification.type,
                notification.checked, notification.createdAt, notification.updatedAt
            ))
        .from(notification)
        .join(notification.product, product)
        .where(notification.user.id.eq(userId))
        .offset(request.getOffset())
        .limit(request.getLimit())
        .fetch();
  }
}
