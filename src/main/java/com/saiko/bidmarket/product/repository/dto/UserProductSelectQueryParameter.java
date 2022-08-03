package com.saiko.bidmarket.product.repository.dto;

import static com.saiko.bidmarket.product.Sort.*;

import org.springframework.util.Assert;

import com.saiko.bidmarket.product.Sort;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;

import lombok.Getter;

@Getter
public class UserProductSelectQueryParameter {

  private final long userId;

  private final int offset;

  private final int limit;

  private final Sort sort;

  private UserProductSelectQueryParameter(long userId, int offset, int limit, Sort sort) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.isTrue(offset >= 0, "offset must not be negative");
    Assert.isTrue(limit > 0, "limit must be positive");

    this.userId = userId;
    this.offset = offset;
    this.limit = limit;
    this.sort = sort != null ? sort : END_DATE_ASC;
  }

  public static UserProductSelectQueryParameter of(long userId, UserProductSelectRequest request) {
    return new UserProductSelectQueryParameter(
        userId,
        request.getOffset(),
        request.getLimit(),
        request.getSort()
    );
  }

}
