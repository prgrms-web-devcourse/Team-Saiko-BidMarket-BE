DROP TABLE IF EXISTS `group_permission` CASCADE;
DROP TABLE IF EXISTS `user` CASCADE;
DROP TABLE IF EXISTS `group` CASCADE;
DROP TABLE IF EXISTS `permission` CASCADE;

CREATE TABLE `permission`
(
    id   bigint      NOT NULL,
    name varchar(20) NOT NULL
);

CREATE TABLE `group`
(
    id   bigint      NOT NULL,
    name varchar(20) NOT NULL
);

CREATE TABLE `group_permission`
(
    id            bigint NOT NULL,
    group_id      bigint NOT NULL,
    permission_id bigint NOT NULL
);

CREATE TABLE `user`
(
    id            bigint      NOT NULL,
    username      varchar(20) NOT NULL,
    provider      varchar(20) NOT NULL,
    provider_id   varchar(80) NOT NULL,
    profile_image varchar(512) DEFAULT NULL,
    group_id      bigint      NOT NULL,
    created_at    timestamp,
    updated_at    timestamp
);
