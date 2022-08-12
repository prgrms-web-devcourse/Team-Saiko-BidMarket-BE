package com.saiko.bidmarket.bidding.repository;

import static com.saiko.bidmarket.bidding.entity.QBidding.*;
import static com.saiko.bidmarket.common.Sort.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.common.Sort;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;

@Repository
public class BiddingCustomRepositoryImpl
    implements BiddingCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  public BiddingCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<Bidding> findAllUserBidding(
      long userId,
      UserBiddingSelectRequest request
  ) {
    Assert.notNull(request, "Request must be provided");

    return jpaQueryFactory
        .selectFrom(bidding)
        .join(bidding.product, product)
        .fetchJoin()
        .join(bidding.bidder, user)
        .fetchJoin()
        .where(bidding.bidder.id.eq(userId))
        .offset(request.getOffset())
        .limit(request.getLimit())
        .orderBy(getOrderSpecifier(request.getSort()))
        .fetch();
  }

  @Override
  public Optional<Bidding> findByBidderIdAndProductId(
      long bidderId,
      long productId
  ) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(bidding)
            .where(
                bidding.bidder.id.eq(bidderId),
                bidding.product.id.eq(productId)
            )
            .fetchFirst());
  }

  private OrderSpecifier getOrderSpecifier(Sort sort) {
    if (sort == END_DATE_ASC) {
      Path<Object> fieldPath = Expressions.path(Object.class, product, END_DATE_ASC.getProperty());

      return new OrderSpecifier(END_DATE_ASC.getOrder(), fieldPath);
    }
    return null;
  }
}
