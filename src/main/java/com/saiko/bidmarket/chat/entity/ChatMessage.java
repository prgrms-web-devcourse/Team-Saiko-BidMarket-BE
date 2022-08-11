package com.saiko.bidmarket.chat.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  private ChatRoom chatRoom;

  @NotBlank
  @Length(min = 1, max = 2000)
  private String message;

  @Builder(access = AccessLevel.PUBLIC)
  private ChatMessage(User sender, ChatRoom chatRoom, String message) {
    Assert.notNull(sender, "Sender must be provided");
    Assert.notNull(chatRoom, "ChatRoom must be provided");
    Assert.hasText(message, "Message must be provided");

    this.sender = sender;
    this.chatRoom = chatRoom;
    this.message = message;
  }

  public static ChatMessage getEmptyMessage() {
    return ChatMessage
        .builder()
        .message(null)
        .sender(null)
        .chatRoom(null)
        .build();
  }
}
