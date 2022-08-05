package com.saiko.bidmarket.product.controller.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "text", nullable = false, updatable = false)
  private String reason;

  @Column(updatable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private User fromUser;

  @Column(updatable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private User toUser;

  @Builder
  private Report(UnsignedLong id, String reason, User fromUser, User toUser) {
    Assert.hasText(reason, "Reason must contain contexts");
    Assert.notNull(fromUser, "From user must be provided");
    Assert.notNull(toUser, "To user must be provided");

    if (id != null) {
      this.id = id.getValue();
    }
    this.reason = reason;
    this.fromUser = fromUser;
    this.toUser = toUser;
  }
}
