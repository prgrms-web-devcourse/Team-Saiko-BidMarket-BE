package com.saiko.bidmarket.chat.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User seller;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User winner;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  private Product product;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatMessage> chatMessage;

  @Builder
  private ChatRoom(User seller, User winner, Product product) {
    Assert.notNull(seller, "Seller must be provided");
    Assert.notNull(winner, "Winner must be provided");
    Assert.notNull(product, "Product must be provided");

    this.seller = seller;
    this.winner = winner;
    this.product = product;
  }

  public User getOpponentUser(long userId) {
    return seller.getId() == userId ? winner : seller;
  }

  public ChatMessage getLastMessage() {
    if (chatMessage.size() == 0) {
      return null;
    }
    int lastIdx = chatMessage.size() - 1;
    return chatMessage.get(lastIdx);
  }

}
