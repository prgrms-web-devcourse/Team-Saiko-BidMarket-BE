package com.saiko.bidmarket.chat.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.user.entity.User;

@Entity
public class ChatMessage extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  private ChatRoom chatRoom;

  @NotBlank
  @Length(max = 2000)
  private String message;

}
