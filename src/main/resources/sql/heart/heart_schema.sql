DROP TABLE IF EXISTS `heart` CASCADE;

CREATE TABLE `heart`
(
    id         bigint       not null,
    user_id    bigint       not null,
    product_id bigint       not null,
    created_at timestamp    not null,
    updated_at timestamp
);