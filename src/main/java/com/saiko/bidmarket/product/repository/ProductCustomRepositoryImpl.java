package com.saiko.bidmarket.product.repository;

import static com.saiko.bidmarket.product.Sort.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.product.entity.Product;

@Repository
public class ProductCustomRepositoryImpl
    implements ProductCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  public ProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<Product> findAllProduct(Pageable pageable) {
    return jpaQueryFactory
        .selectFrom(product)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(getOrderSpecifier(pageable.getSort()))
        .fetch();
  }

  @Override
  public List<Product> findAllUserProduct(long userId, Pageable pageable) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(pageable, "Page must be provided");
    return jpaQueryFactory.selectFrom(product)
                          .join(product.writer, user)
                          .where(user.id.eq(userId))
                          .offset(pageable.getOffset())
                          .limit(pageable.getPageSize())
                          .orderBy(getOrderSpecifier(pageable.getSort()))
                          .fetch();
  }

  private OrderSpecifier getOrderSpecifier(Sort sort) {
    for (Sort.Order order : sort) {
      String property = order.getProperty();
      if (property.equals(END_DATE_ASC.getProperty())) {
        Path<Object> fieldPath = Expressions.path(Object.class, product,
                                                  END_DATE_ASC.getProperty());
        return new OrderSpecifier(END_DATE_ASC.getOrder(), fieldPath);
      }
    }
    return null;
  }
}
