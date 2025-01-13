-- Sample data for players table
INSERT INTO players (player_id)
VALUES ('7701935d-4efc-4f53-81f1-1010dce8a943'),
       ('5c035da0-3a05-4cd7-8e0f-d9cae488de8b');

-- Sample data for games table
INSERT INTO games (game_id, is_finished)
VALUES ('33333333-3333-3333-3333-333333333333', FALSE);

-- Sample data for games_players table
INSERT INTO games_players (game_id, player_id)
VALUES ('33333333-3333-3333-3333-333333333333', '7701935d-4efc-4f53-81f1-1010dce8a943'),
       ('33333333-3333-3333-3333-333333333333', '5c035da0-3a05-4cd7-8e0f-d9cae488de8b');

insert into achievements (achievement_id, desired_number_of_games, is_achieved, performer_id, achievement_type, description, game_ids_played, image_url, name)
values ('ad4c99a5-1184-4e80-a087-05e2c36d0435', 10, TRUE, '7701935d-4efc-4f53-81f1-1010dce8a943', 'PLAYNGAMES', 'You need to play 10 times the Checkers Game.',
        '33333333-3333-3333-3333-333333333333', 'https://picsum.photos/seed/picsum/720/480', 'Play 10 Games'),
       ('74706643-9d78-4591-9bc7-a156a95a2eb6', null, TRUE, '7701935d-4efc-4f53-81f1-1010dce8a943', 'MOVEPIECE', 'You need to move any piece in the game.',
        null, 'https://picsum.photos/seed/picsum/720/480', 'Move Your Piece'),
       ('ad4c99a5-1184-4e80-a087-05e2c36d0435', 10, TRUE, '5c035da0-3a05-4cd7-8e0f-d9cae488de8b', 'PLAYNGAMES', 'You need to play 10 times the Checkers Game.',
        '33333333-3333-3333-3333-333333333333', 'https://picsum.photos/seed/picsum/720/480', 'Play 10 Games'),
       ('74706643-9d78-4591-9bc7-a156a95a2eb6', null, TRUE, '5c035da0-3a05-4cd7-8e0f-d9cae488de8b', 'MOVEPIECE', 'You need to move any piece in the game.',
        null, 'https://picsum.photos/seed/picsum/720/480', 'Move Your Piece');

