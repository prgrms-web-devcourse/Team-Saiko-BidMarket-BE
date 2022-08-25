package com.saiko.bidmarket.user.entity;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "`user`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(length = 20)
  private String username;

  @Column(length = 512)
  private String profileImage;

  @Column(length = 20)
  private String provider;

  @Column(length = 80)
  private String providerId;

  @Enumerated(EnumType.STRING)
  private Role role;

  private static final String DEFAULT_DELETE_NAME = "Unknown";

  @Builder
  public User(
      String username,
      String profileImage,
      String provider,
      String providerId,
      Role role
  ) {

    Assert.isTrue(isNotBlank(username), "Username must be provided");
    Assert.isTrue(isNotBlank(profileImage), "ProfileImage must be provided");
    Assert.isTrue(isNotBlank(provider), "ProfileImage must be provided");
    Assert.isTrue(isNotBlank(providerId), "ProviderId must be provided");
    Assert.notNull(role, "UserRole must be provided");

    this.username = username;
    this.profileImage = profileImage;
    this.provider = provider;
    this.providerId = providerId;
    this.role = role;
  }

  public boolean isSameUser(long id) {
    return this.id == id;
  }

  public void update(
      String username,
      String profileImage
  ) {
    Assert.notNull(username, "username must be provide");
    Assert.notNull(profileImage, "profileImage must be provide");

    this.username = username;
    this.profileImage = profileImage;
  }

  public void delete() {
    this.username = DEFAULT_DELETE_NAME;
    this.profileImage = "";
    this.provider = null;
    this.providerId = null;
  }

  public void reportPenalty() {
    delete();
  }

  public static User of(
      OAuth2User oAuth2User,
      String provider,
      Role role
  ) {
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String providerId = oAuth2User.getName();
    String username = (String)attributes.get("name");
    String profileImage = (String)attributes.get("picture");

    return User
        .builder()
        .username(username)
        .profileImage(profileImage)
        .provider(provider)
        .providerId(providerId)
        .role(role)
        .build();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User)o;
    return provider.equals(user.provider) && providerId.equals(user.providerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(provider, providerId);
  }
}
