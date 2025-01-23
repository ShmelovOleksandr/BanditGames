# BanditGames

## Description

### About 
BanditGames is a group project developed during our college coursework. It is an online gaming platform that enables users to purchase and play games with other players.

### Architecture
The project employs a hexagonal architecture and utilizes a microservices approach.

Bounded contexts:
1. **GamePlatform:** Core platform functionalities, such as user management, game discovery, and matchmaking.
2. **Checkers**: Implementation of the Checkers game, including game logic, player interactions, and game state management.
3. **CheckersAchievement**: System for tracking and awarding achievements within the Checkers game.
4. **GuessIt**:  Implementation of the GuessIt game, including game logic, player interactions, and game state management.
5. **Statistics**: Centralized service for collecting and storing game statistics and user performance data across all games.

### Technologies used:
* Spring Boot
* Websockets
* REST API
* RabbitMQ
* Docker
* PostgreSQL

### Team members:
* Anna Deineko
* Oleksandr Shmelov
* Sofiia Hmyria
* Mykhailo Kruts
* Yevhen Zinenko



## Personal contribution (Oleksandr Shmelov)
My contributions focused on the GamePlatform context and included the development of nearly all aspects of the Checkers context, including the entire CheckersAchievement context.

## How to run:
```shell
docker-compose up -d
```

For the initial run, it is recommended to start the services in the following order to ensure proper initialization of RabbitMQ queues:
1. GamePlatform
2. Statistics
3. Checkers
4. CheckersAchievement
5. GuessIt