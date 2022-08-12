DROP TABLE IF EXISTS `report` CASCADE;

CREATE TABLE `report`
(
    id              bigint       not null,
    reason          text         not null,
    from_user_id    bigint       not null,
    to_user_id      bigint       not null,
    `type`          varchar(16),
    type_id         bigint,
    created_at      timestamp    not null,
    updated_at      timestamp
);
