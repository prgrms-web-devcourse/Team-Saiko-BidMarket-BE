DROP TABLE IF EXISTS `user` CASCADE;

CREATE TABLE `user`
(
    id            bigint      NOT NULL,
    username      varchar(20) NOT NULL,
    provider      varchar(20)  DEFAULT NULL,
    provider_id   varchar(80)  DEFAULT NULL,
    profile_image varchar(512) DEFAULT NULL,
    user_role     varchar(20) NOT NULL,
    created_at    timestamp   NOT NULL,
    updated_at    timestamp
);
