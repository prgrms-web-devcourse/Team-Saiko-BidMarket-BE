INSERT INTO `permission`(id, name)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN')
;

INSERT INTO `group`(id, name)
VALUES (1, 'USER_GROUP'),
       (2, 'ADMIN_GROUP')
;

-- USER_GROUP (ROLE_USER)
-- ADMIN_GROUP (ROLE_USER, ROLE_ADMIN)
INSERT INTO `group_permission`(id, group_id, permission_id)
VALUES (1, 1, 1),
       (2, 2, 1),
       (3, 2, 2)
;

INSERT INTO `user`(username, provider, provider_id, profile_image, group_id, created_at)
VALUES ('유재희2', 'google', '113052825681484000000',
        'https://lh3.googleusercontent.com/a/AItbvmmujg3pE4C3iRbHWRZCd-BtvUykZ2BaaIAuSoo7=s96-c', 1,
        '2022-07-03T18:06:15'),
       ('abramgech', 'google', '103163611869308335152',
        'https://lh3.googleusercontent.com/a/AItbvmmRBO7OpOF-F-6FrAY-lP8FHJOWwC1n_GNyaNsH=s96-c', 1,
        '2022-07-04 17:23:07');
