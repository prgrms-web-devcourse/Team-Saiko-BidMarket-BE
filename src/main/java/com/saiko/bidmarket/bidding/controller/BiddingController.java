package com.saiko.bidmarket.bidding.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;
import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateResponse;
import com.saiko.bidmarket.bidding.service.BiddingService;
import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.jwt.JwtAuthentication;

@RestController
@RequestMapping("api/v1/bidding")
public class BiddingController {

  private final BiddingService biddingService;

  public BiddingController(BiddingService biddingService) {
    this.biddingService = biddingService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BiddingCreateResponse create(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @RequestBody @Valid BiddingCreateRequest biddingCreateRequest
  ) {
    var createDto = BiddingCreateDto.builder()
                                    .biddingPrice(biddingCreateRequest.getBiddingPrice())
                                    .productId(biddingCreateRequest.getProductId())
                                    .bidderId(UnsignedLong.valueOf(authentication.getUserId()))
                                    .build();

    long createdBiddingId = biddingService.create(createDto);

    return new BiddingCreateResponse(createdBiddingId);
  }
}
