package com.saiko.bidmarket.chat.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectResponse;
import com.saiko.bidmarket.chat.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chatRooms")
public class ChatMessageApiController {

  private final ChatMessageService chatMessageService;

  @GetMapping("{chatRoomId}/messages")
  @ResponseStatus(HttpStatus.OK)
  public List<ChatMessageSelectResponse> getAll(
      @PathVariable
      long chatRoomId,
      @ModelAttribute @Valid
      ChatMessageSelectRequest chatMessageSelectRequest
  ) {
    return chatMessageService.findAll(chatRoomId, chatMessageSelectRequest);
  }
}
