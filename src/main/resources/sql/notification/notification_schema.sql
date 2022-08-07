DROP TABLE IF EXISTS `notification` CASCADE;

CREATE TABLE `notification`
(
    id         bigint       not null,
    content    varchar(100) not null,
    product_id bigint       not null,
    user_id    bigint       not null,
    created_at timestamp    not null,
    updated_at timestamp
);