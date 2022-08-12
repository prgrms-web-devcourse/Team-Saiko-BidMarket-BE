package com.saiko.bidmarket.comment.repository;

import static com.saiko.bidmarket.comment.entity.QComment.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.common.Sort;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CommentCustomRepositoryImpl
    implements CommentCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Comment> findAllByProduct(CommentSelectRequest commentSelectRequest) {
    Assert.notNull(commentSelectRequest, "CommentSelectRequest must be provided");

    return jpaQueryFactory
        .selectFrom(comment)
        .join(comment.product, product)
        .fetchJoin()
        .join(comment.writer, user)
        .fetchJoin()
        .where(
            comment.product.id.eq(commentSelectRequest
                                      .getProductId()
                                      .getValue()))
        .orderBy(getOrderSpecifier(commentSelectRequest.getSort()))
        .fetch();
  }

  private OrderSpecifier getOrderSpecifier(Sort sort) {
    for (Sort value : Sort.values()) {
      if (sort == value) {
        Path<Object> fieldPath = Expressions.path(Object.class, product,
                                                  value.getProperty());
        return new OrderSpecifier(value.getOrder(), fieldPath);
      }
    }
    return null;
  }
}
