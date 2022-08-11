package com.saiko.bidmarket.heart.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import com.saiko.bidmarket.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Hearts {

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Heart> hearts = new ArrayList<>();

  public boolean toggleHeart(Heart heart) {
    if (contains(heart)) {
      removeHeart(heart);
      return false;
    }

    hearts.add(heart);
    return true;
  }

  private boolean contains(Heart heart) {
    Long productId = heart
        .getProduct()
        .getId();
    return hearts
        .parallelStream()
        .anyMatch(h -> h.ownAbout(productId));
  }

  private void removeHeart(Heart heart) {
    Long productId = heart
        .getProduct()
        .getId();
    Heart removalTarget = hearts
        .parallelStream()
        .filter(h -> h.ownAbout(productId))
        .findAny()
        .orElseThrow(NotFoundException::new);
    hearts.remove(removalTarget);
  }
}
