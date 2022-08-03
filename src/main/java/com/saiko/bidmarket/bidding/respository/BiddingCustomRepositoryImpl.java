package com.saiko.bidmarket.bidding.respository;

import static com.saiko.bidmarket.bidding.entity.QBidding.*;
import static com.saiko.bidmarket.product.Sort.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;

@Repository
public class BiddingCustomRepositoryImpl
    implements BiddingCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  public BiddingCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<Bidding> findAllUserBidding(long userId, UserBiddingSelectRequest request) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(request, "Request must be provided");

    return jpaQueryFactory
        .selectFrom(bidding)
        .join(bidding.product, product).fetchJoin()
        .join(bidding.bidder, user).fetchJoin()
        .where(bidding.bidder.id.eq(userId))
        .offset(request.getOffset())
        .limit(request.getLimit())
        .orderBy(getOrderSpecifier(request.getSort()))
        .fetch();
  }

  private OrderSpecifier getOrderSpecifier(com.saiko.bidmarket.product.Sort sort) {
    if (sort == END_DATE_ASC) {
      Path<Object> fieldPath = Expressions.path(Object.class, product,
                                                END_DATE_ASC.getProperty());
      return new OrderSpecifier(END_DATE_ASC.getOrder(), fieldPath);
    }
    return null;
  }
}
