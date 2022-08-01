DROP TABLE IF EXISTS `chat_message` CASCADE;
DROP TABLE IF EXISTS `chat_room` CASCADE;

create table `chat_message`
(
    id           bigint        not null,
    sender_id    bigint        not null,
    chat_room_id bigint        not null,
    message      varchar(2000) not null,
    created_at   timestamp,
    updated_at   timestamp
);

create table `chat_room`
(
    id         bigint not null,
    seller_id  bigint not null,
    buyer_id   bigint not null,
    created_at timestamp,
    updated_at timestamp
);
