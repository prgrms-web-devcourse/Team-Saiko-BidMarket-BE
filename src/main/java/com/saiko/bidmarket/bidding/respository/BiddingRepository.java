package com.saiko.bidmarket.bidding.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.bidding.entity.Bidding;

public interface BiddingRepository extends JpaRepository<Bidding, Long> {
}
