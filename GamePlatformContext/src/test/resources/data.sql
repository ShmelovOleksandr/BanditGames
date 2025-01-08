insert into games(game_id, title, description, price_amount, currency_code, max_lobby_player_amount, frontend_url, backend_url, game_image_url)
values ('7a0ff880-adf8-4b6c-821f-607f3d6d5895', 'Game', 'The best game', 10, 'USD', 2, 'http://localhost:9090/frontend', 'http://localhost:9090/backend', 'http://localhost:9090/image'),
       ('203028d1-4ce6-426e-9b4b-de9ec03f12be', 'International Checkers', 'Normal checkers game', 5.99, 'EUR', 2, 'http://localhost:9090/frontend', 'http://localhost:9090/backend', 'http://localhost:9090/image');


insert into players(player_id, age, gender, username)
values ('f7c56b6e-d7dc-4bde-bdf9-e9b0ecec8bf5', 18, 'MALE', 'user'),
       ('6a1d9b5a-e2e2-4060-9025-3175acd06e3c', 18, 'FEMALE', 'new_player'),
       ('3d8a9e74-8534-43af-99a0-d60593acb4a6', 18, 'MALE', 'mike')