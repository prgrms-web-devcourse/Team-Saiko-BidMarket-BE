package com.saiko.bidmarket.chat.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.service.ChatRoomService;
import com.saiko.bidmarket.chat.service.dto.ChatRoomSelectParam;
import com.saiko.bidmarket.common.jwt.JwtAuthentication;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/chatRooms")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatRoomApiController {

  private final ChatRoomService chatRoomService;

  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<ChatRoomSelectResponse> findAll(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @ModelAttribute @Valid ChatRoomSelectRequest request
  ) {
    long userId = authentication.getUserId();
    ChatRoomSelectParam param = ChatRoomSelectParam.of(userId, request);
    return chatRoomService.findAll(param);
  }
}
