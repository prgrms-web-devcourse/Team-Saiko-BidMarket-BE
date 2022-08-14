package com.saiko.bidmarket.heart.repository;

import static com.saiko.bidmarket.bidding.entity.QBidding.*;
import static com.saiko.bidmarket.common.Sort.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;
import static com.saiko.bidmarket.heart.entity.QHeart.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.common.Sort;
import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.user.controller.dto.UserHeartSelectRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HeartCustomRepositoryImpl implements HeartCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Heart> findAllUserHeart(
      long userId,
      UserHeartSelectRequest request
  ) {
    Assert.notNull(request, "Request must be provided");

    return jpaQueryFactory
        .selectFrom(heart)
        .join(heart.product, product)
        .fetchJoin()
        .where(heart.user.id.eq(userId)
        .and(heart.actived.eq(true)))
        .offset(request.getOffset())
        .limit(request.getLimit())
        .orderBy(getOrderSpecifier(request.getSort()))
        .fetch();
  }

  private OrderSpecifier getOrderSpecifier(Sort sort) {
    if (sort == END_DATE_ASC) {
      Path<Object> fieldPath = Expressions.path(Object.class, product, END_DATE_ASC.getProperty());

      return new OrderSpecifier(END_DATE_ASC.getOrder(), fieldPath);
    }
    return null;
  }
}
