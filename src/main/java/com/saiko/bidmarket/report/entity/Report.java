package com.saiko.bidmarket.report.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
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

  @ManyToOne(fetch = FetchType.LAZY)
  private User fromUser;

  @ManyToOne(fetch = FetchType.LAZY)
  private User toUser;

  @Enumerated(value = EnumType.STRING)
  private Type type;

  private Long typeId;

  private Report(
      User fromUser,
      User toUser,
      String reason,
      Type type,
      Long typeId
  ) {
    Assert.hasText(reason, "Reason must contain contexts");
    Assert.notNull(fromUser, "From user must be provided");
    Assert.notNull(toUser, "To user must be provided");

    this.fromUser = fromUser;
    this.toUser = toUser;
    this.reason = reason;
    this.type = type;
    this.typeId = typeId;

    validate();
  }

  public static Report toUser(
      User fromUser,
      User toUser,
      String reason
  ) {
    return new Report(fromUser, toUser, reason, null, null);
  }

  public static Report toProduct(
      User fromUser,
      Product product,
      String reason
  ) {
    return new Report(
        fromUser,
        product.getWriter(),
        reason,
        Type.PRODUCT,
        product.getId()
    );
  }

  public static Report toComment(
      User fromUser,
      Comment comment,
      String reason
  ) {
    return new Report(
        fromUser,
        comment.getWriter(),
        reason,
        Type.PRODUCT,
        comment.getId()
    );
  }

  private void validate() {
    validateUsers();
    validateTypeAndTypeId();
  }

  private void validateTypeAndTypeId() {
    if ((type == null) != (typeId == null)) {
      throw new IllegalArgumentException("type과 typeId 둘 중 하나만 null일 수 없습니다.");
    }
  }

  private void validateUsers() {
    if (fromUser.equals(toUser)) {
      throw new IllegalArgumentException("신고자와 피신고자는 같을 수 없습니다");
    }
  }

  public enum Type {
    PRODUCT,
    COMMENT
  }
}
