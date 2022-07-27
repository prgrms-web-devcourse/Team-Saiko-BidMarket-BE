package com.saiko.bidmarket.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.saiko.bidmarket.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u join fetch u.group g left join fetch g.permissions gp join fetch gp.permission where u.username = :username")
  Optional<User> findByUsername(String username);

  @Query("select u from User u join fetch u.group g left join fetch g.permissions gp join fetch gp.permission where u.provider = :provider and u.providerId = :providerId")
  Optional<User> findByProviderAndProviderId(String provider, String providerId);

}
