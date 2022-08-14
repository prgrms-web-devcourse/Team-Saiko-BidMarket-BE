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

import com.saiko.bidmarket.common.entity.BaseTime;
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

  @ManyToOne(fetch = FetchType.LAZY)
  private User reporter;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private Type type;

  @Column(nullable = false, updatable = false)
  private long typeId;

  @Builder(access = AccessLevel.PRIVATE)
  private Report(
      User reporter,
      Type type,
      long typeId,
      String reason
  ) {
    Assert.notNull(reporter, "Reporter must be provided");
    Assert.notNull(type, "Type must be provided");
    Assert.hasText(reason, "Reason must contain contexts");

    this.reporter = reporter;
    this.type = type;
    this.typeId = typeId;
    this.reason = reason;

    validateUsers();
  }

  public static Report of(
      User reporter,
      Type type,
      long targetId,
      String reason
  ) {
    return Report
        .builder()
        .reporter(reporter)
        .type(type)
        .typeId(targetId)
        .reason(reason)
        .build();
  }

  private void validateUsers() {
    if (type == Type.USER && reporter.getId() == typeId) {
      throw new IllegalArgumentException("신고자와 피신고자는 같을 수 없습니다");
    }
  }

  public enum Type {
    USER(25),
    PRODUCT(10),
    COMMENT(5);

    public final int MAX_COUNT;

    Type(int max_count) {
      MAX_COUNT = max_count;
    }
  }
}
