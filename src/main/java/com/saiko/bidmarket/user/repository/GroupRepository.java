package com.saiko.bidmarket.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.user.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
  Optional<Group> findByName(String name);
}
