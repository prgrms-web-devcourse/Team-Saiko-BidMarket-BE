package com.saiko.bidmarket.product.repository;

import static com.saiko.bidmarket.product.Sort.*;
import static com.saiko.bidmarket.product.entity.QProduct.*;
import static com.saiko.bidmarket.user.entity.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.service.dto.UserProductSelectQueryParameter;

@Repository
public class ProductCustomRepositoryImpl
    implements ProductCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  public ProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<Product> findAllProduct(ProductSelectRequest productSelectRequest) {
    Assert.notNull(productSelectRequest, "ProductSelectRequest must be provided");
    return jpaQueryFactory
        .selectFrom(product)
        .where(eqCategory(productSelectRequest.getCategory()))
        .offset(productSelectRequest.getOffset())
        .limit(productSelectRequest.getLimit())
        .orderBy(getOrderSpecifier(productSelectRequest.getSort()))
        .fetch();
  }

  @Override
  public List<Product> findAllUserProduct(UserProductSelectQueryParameter query) {
    Assert.notNull(query, "Operation must be provided");

    return jpaQueryFactory.selectFrom(product)
                          .join(product.writer, user)
                          .where(user.id.eq(query.getUserId()))
                          .offset(query.getOffset())
                          .limit(query.getLimit())
                          .orderBy(getOrderSpecifier(query.getSort()))
                          .fetch();
  }

  private Predicate eqCategory(Category category) {
    if (category == null) {
      return null;
    }
    return product.category.eq(category);
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
