package com.saiko.bidmarket.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.chat.entity.ChatRoom;

public interface ChatRoomRepository extends ChatRoomCustomRepository, JpaRepository<ChatRoom, Long> {
  Optional<ChatRoom> findByProduct_IdAndSeller_Id(long productId, long sellerId);
}
