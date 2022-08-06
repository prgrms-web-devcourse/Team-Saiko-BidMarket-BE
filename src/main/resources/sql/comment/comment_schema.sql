DROP TABLE IF EXISTS `comment` CASCADE;

CREATE TABLE `comment`
(
    id         bigint       not null,
    content    varchar(500) not null,
    product_id bigint,
    user_id    bigint
);