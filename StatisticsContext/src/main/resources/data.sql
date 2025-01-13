insert into games(game_id, name)
values ('7a0ff880-adf8-4b6c-821f-607f3d6d5895', 'game');


insert into players(player_id, age, gender, player_name, country, city)
values ('6a1d9b5a-e2e2-4060-9025-3175acd06e3c', 18, 'FEMALE', 'new_player', 'Spain', 'Madrid'),
       ('f7c56b6e-d7dc-4bde-bdf9-e9b0ecec8bf5', 18, 'MALE', 'mike', 'Finland', 'Helsinki');


insert into player_metrics(avg_game_duration, avg_move_amount, avg_move_duration, total_afternoon_plays, total_draws, total_evening_plays, total_games_played, total_is_first, total_losses, total_morning_plays, total_night_plays, total_weekdays_played, total_weekends_played, total_wins, player_player_id)
values (0, 0, 0 , 0 ,0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 'f7c56b6e-d7dc-4bde-bdf9-e9b0ecec8bf5'),
       (0, 0, 0 , 0 ,0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, '6a1d9b5a-e2e2-4060-9025-3175acd06e3c');

insert into player_predicitons(churn, first_move_win_probability, player_player_id, player_class)
values (0, 0, 'f7c56b6e-d7dc-4bde-bdf9-e9b0ecec8bf5', 'BEGINNER'),
       (0, 0, '6a1d9b5a-e2e2-4060-9025-3175acd06e3c', 'BEGINNER');