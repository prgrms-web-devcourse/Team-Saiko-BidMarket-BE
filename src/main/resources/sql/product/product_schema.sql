DROP TABLE IF EXISTS `image` CASCADE;
DROP TABLE IF EXISTS `product` CASCADE;

CREATE TABLE `product`
(
    id            bigint       NOT NULL,
    title         varchar(16)  NOT NULL,
    description   varchar(500) NOT NULL,
    minimum_price int          NOT NULL,
    category      varchar(255) NOT NULL,
    location      varchar(255) NOT NULL,
    expire_at     timestamp    NOT NULL,
    created_at    timestamp,
    updated_at    timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE `image`
(
    id         bigint       NOT NULL,
    product_id bigint       NOT NULL,
    url        varchar(512) NOT NULL,
    `order`    int,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_id_for_image FOREIGN KEY (product_id) REFERENCES `product` (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
