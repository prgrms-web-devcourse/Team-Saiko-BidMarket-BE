package com.saiko.bidmarket.heart.repository;

import java.util.List;

import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.user.controller.dto.UserHeartSelectRequest;

public interface HeartCustomRepository {
  List<Heart> findAllUserHeart(
      long userId,
      UserHeartSelectRequest request
  );
}
