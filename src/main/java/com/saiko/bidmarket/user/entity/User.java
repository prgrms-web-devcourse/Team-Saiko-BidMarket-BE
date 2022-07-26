package com.saiko.bidmarket.user.entity;

import static org.apache.commons.lang3.StringUtils.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;

@Entity
public class User extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(length = 64, unique = true)
  private String email;

  @NotBlank
  @Column(length = 20)
  private String nickname;

  @NotBlank
  @Column(length = 512)
  private String profileImage;

  @NotBlank
  @Column(length = 20)
  private String provider;

  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id")
  private Group group;

  protected User() {/*no-op*/}

  public User(String email, String nickname, String profileImage, String provider, Group group) {
    Assert.isTrue(isNotBlank(email), "Email must be provided");
    Assert.isTrue(isNotBlank(nickname), "Nickname must be provided");
    Assert.isTrue(isNotBlank(profileImage), "ProfileImage must be provided");
    Assert.isTrue(isNotBlank(provider), "ProfileImage must be provided");
    Assert.notNull(group, "Group must be provided");

    this.email = email;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.provider = provider;
    this.group = group;
  }
}
