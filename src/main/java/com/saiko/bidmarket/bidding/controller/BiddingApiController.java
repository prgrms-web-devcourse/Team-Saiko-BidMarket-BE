package com.saiko.bidmarket.bidding.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;
import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateResponse;
import com.saiko.bidmarket.bidding.controller.dto.BiddingPriceResponse;
import com.saiko.bidmarket.bidding.service.BiddingService;
import com.saiko.bidmarket.common.jwt.JwtAuthentication;

@RestController
@RequestMapping("api/v1/biddings")
public class BiddingApiController {

  private final BiddingService biddingService;

  public BiddingApiController(BiddingService biddingService) {
    this.biddingService = biddingService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BiddingCreateResponse create(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @RequestBody @Valid
      BiddingCreateRequest biddingCreateRequest
  ) {

    long createdBiddingId = biddingService.create(authentication.getUserId(), biddingCreateRequest);

    return new BiddingCreateResponse(createdBiddingId);
  }

  @GetMapping("products/{productId}")
  @ResponseStatus(HttpStatus.OK)
  public BiddingPriceResponse findBiddingPriceByUserIdAndProductId(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable("productId")
      long pathProductId
  ) {
    long biddingPrice = biddingService.findBiddingPriceByProductIdAndUserId(
        authentication.getUserId(),
        pathProductId
    );

    return new BiddingPriceResponse(biddingPrice);
  }
}
