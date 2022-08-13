DROP TABLE IF EXISTS `report` CASCADE;

CREATE TABLE `report`
(
    id              bigint       not null,
    reporter_id     bigint       not null,
    `type`          varchar(16)  not null,
    type_id         bigint       not null,
    reason          text         not null,
    created_at      timestamp    not null,
    updated_at      timestamp
);
