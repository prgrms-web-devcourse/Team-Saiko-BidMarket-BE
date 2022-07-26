DROP TABLE IF EXISTS `bidding` CASCADE;

CREATE TABLE `bidding`
(
    id            bigint     not null,
    bidding_price bigint     not null,
    won           tinyint(1) not null,
    created_at    timestamp  not null,
    updated_at    timestamp,
    bidder_id     bigint     not null,
    product_id    bigint     not null
);