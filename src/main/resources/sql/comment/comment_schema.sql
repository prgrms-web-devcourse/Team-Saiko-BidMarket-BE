DROP TABLE IF EXISTS `comment` CASCADE;

CREATE TABLE `comment`
(
    id         bigint       not null,
    content    varchar(500) not null,
    product_id bigint       not null,
    user_id    bigint       not null
);