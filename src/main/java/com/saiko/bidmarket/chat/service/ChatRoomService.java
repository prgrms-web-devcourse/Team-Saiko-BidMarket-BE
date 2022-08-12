package com.saiko.bidmarket.chat.service;

import java.util.List;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.product.entity.Product;

public interface ChatRoomService {

  void create(Product product);

  List<ChatRoomSelectResponse> findAll(
      long userId,
      ChatRoomSelectRequest request
  );

}
