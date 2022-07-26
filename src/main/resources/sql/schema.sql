CREATE TABLE `product`
(
    id            bigint       not null auto_increment,
    title         varchar(16)  not null,
    description   varchar(500) not null,
    minimum_price int          not null,
    category      varchar(100) not null,
    location      varchar(100),
    expire_at     datetime     not null,
    created_at    datetime,
    updated_at    datetime,
    primary key (id)
);

CREATE TABLE `image`
(
    id         bigint       not null auto_increment,
    product_id bigint,
    url        varchar(512) not null,
    `order`    int          not null,
    created_at datetime,
    updated_at datetime,
    primary key (id)
);