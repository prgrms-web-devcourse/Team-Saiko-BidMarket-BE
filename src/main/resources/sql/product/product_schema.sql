DROP TABLE IF EXISTS `image` CASCADE;
DROP TABLE IF EXISTS `product` CASCADE;

CREATE TABLE `product`
(
    id              bigint       not null,
    title           varchar(32)  not null,
    description     varchar(500) not null,
    minimum_price   int          not null,
    category        varchar(100) not null,
    thumbnail_image varchar(512) not null,
    location        varchar(20),
    progressed      tinyint(1)   not null,
    winning_price   bigint,
    expire_at       timestamp    not null,
    created_at      timestamp    not null,
    updated_at      timestamp,
    user_id         bigint
);

CREATE TABLE `image`
(
    id         bigint       not null,
    product_id bigint,
    url        varchar(512) not null,
    `order`    int          not null,
    created_at timestamp    not null,
    updated_at timestamp
);
