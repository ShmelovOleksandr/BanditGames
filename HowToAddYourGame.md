

# Integration of your game on `Bandit Games` platform

This document describes how a new game should be integrated with the platform.
Below you will find different topic and frequently asked questions regarding adding your `board game`.

**⚠️ Please read carefully! This is important.**


## Table of Contents

1. [General information](#general-information)
2. [Explore our infrastructure](#explore-our-infrastructure)
3. [Adding a game flow](#adding-a-game-flow)
4. [Backend configuration](#backend-configuration)
5. [Frontend configuration](#frontend-configuration)

## General information
Welcome to Bandit Games. Here you will find everything you need to know on how to add your board game onto our platform. 

In order to begin your journey, you have emailed us with your request and description of your game.
Email: [bandit-games@mail.com](bandit-games@mail.com)

Your application can be deployed anywhere in the world on any provider you like.
But there are some requirements to integrate with our platform:

* Messaging communication between our and yours platforms will be set up using **RabbitMQ**.
* The backend and frontend of your solution must be configured with specific APIs and message structures to capture correct analytics and playing experience.


## Explore our infrastructure

Our platform consists of several blocks:
1. Main platform:
   * A place where players of Bandit Games are matched in a lobby.
   * Players Achievements are displayed.
   * Handles Game registration process.
2. Statistics/Analytics:
   * Collects events from every Game session process:
     1. Game session started event.
     2. Player made a move event.
     3. Game session finished event.
   * Process those events and predict related metrics for the registered player such as:
     1. Churn: how likely a player to return to our platform.
     2. First move win probability: how likely a player to win next match if he is first player in the lobby.
     3. Player classification: a classification of a player on how he interacts with the platform (e.g. Beginner, Frequent player)
3. Chatbot:
   * Provide information about utilization of platform and how to play all games on our platform

Additionally, our platform utilizes `SSO` for main platform and your board game.

## Adding a game flow

Here is an example flow of how should you add your game to our platform.

1. Connect your Message consumer to our `RabbitMQ` instance.
2. Connect your backend application to our `Keycloak` instance.
3. Prepare a set of rules of how to play your board game.
4. Set-up all events schemas and test if they align with provided schemas in the section at the end of this document.
5. Configure that your Frontend and Backend are communicating properly.
6. Create a method that will send to our `newgame_queue` to get registered on our platform.
7. Start your backend application, and if all the requirements above are configured correctly, your board game should appear on our platform!


## Backend configuration

### RabbitMQ messaging configuration
All the necessary url's for configuring the connection to our RabbitMQ instance are provided in the email.

Here is a list of all the necessary queues and exchanges that you have to connect:
1. For publishing:
   1. Publish event of your game information for registration:
      - Exchange name: "platform_game_exchange";
      - Routing key: "newgame.#.registered";
      - Description: Functionality to register a board game on Bandit Games Platform;
      - When to send: after backend application is up and running;
      - JSON event schema name: [game_added_event_schema](#game_added_event_schema);
   2. Publish event of game session start:
       - Exchange name: "game_events_exchange";
       - Routing key: "game.#.started";
       - Description: Register that a match/session has started for players in a lobby. Players in a lobby are supplied by `lobby_queue`;
       - Specification: Id for the session should be `the same` as **lobby_id** in [lobby_created_event_schema](#lobby_created_event_schema);
       - When to send: After receiving an event from `lobby_queue` directed towards your game;
       - JSON event schema name: [start_game_session_event_schema](#start_game_session_event_schema);
   3. Publish event of player move:
       - Exchange name: "game_events_exchange";
       - Routing key: "player.#.moved";
       - Specification: Id for the session should be `the same` as **lobby_id** in [lobby_created_event_schema](#lobby_created_event_schema);
       - Description: Register that a player has performed a move. The ID of next player must be supplied;
       - When to send: After a player has finished a move within his session;
       - JSON event schema name: [player_move_event_schema](#player_move_event_schema);
   4. Publish event of game session finish:
       - Exchange name: "game_events_exchange";
       - Routing key: "game.#.finished";
       - Description: Register that a match/session has finished. Only 1 player can become a winner;
       - When to send: After match/session has fulfilled all the conditions of a board game to end the game.;
       - JSON event schema name: [finish_game_session_event_schema](#finish_game_session_event_schema);

2. For subscribing:
    1. Subscribe to receive event of lobby created
       - Queue name: "lobby_queue";
       - Description: An event that contains a lobby of players, id of the lobby and the id of who is the owner/first player in the lobby;
       - JSON event schema name: [lobby_created_event_schema](#lobby_created_event_schema)

### API configuration

If your game contains achievements, you have to create an API to retrieve the information about achievements and which players unlocked them.
Here are the rules you must follow:

* API name must be: "/api/v1/achievements'
* JSON Event schema for achievement DTO: [achievement_schema](#achievement_schema)


## Frontend configuration

After all players in lobby will be ready to play your game, they will get redirected to your frontend URL.
The URL they will get redirected to is: http://<your-frontend-url>/<lobby-id>.
You will retrieve the lobby id from the received `lobby_created_event` (schema: [lobby_created_event_schema](#lobby_created_event_schema)).

Keycloak resource server URLs are provided in email. When players are redirected, they must remain authenticated.

## JSON schema of event objects:

### Game_added_event_schema

```
{
  "type": "object",
  "properties": {
    "gameName": {
      "type": "string"
    },
    "description": {
      "type": "string"
    },
    "price": {
      "type": "number"
    },
    "currency": {
      "type": "string"
    },
    "maxLobbyPlayersAmount": {
      "type": "integer"
    },
    "frontendUrl": {
      "type": "string",
      "format": "uri"
    },
    "backendApiUrl": {
      "type": "string",
      "format": "uri"
    },
    "gameImageUrl": {
      "type": "string",
      "format": "uri"
    },
    "rules": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "rule": {
            "type": "string"
          },
          "description": {
            "type": "string"
          }
        },
        "required": ["rule", "description"],
        "additionalProperties": false
      }
    }
  },
  "required": [
    "gameName",
    "description",
    "price",
    "currency",
    "maxLobbyPlayersAmount",
    "frontendUrl",
    "backendApiUrl",
    "gameImageUrl",
    "rules"
  ],
  "additionalProperties": false
}

```

### Start_game_session_event_schema

```
{
  "type": "object",
  "properties": {
    "gameName": {
      "type": "string"
    },
    "lobbyId": {
      "type": "string",
      "format": "uuid"
    },
    "firstPlayerId": {
      "type": "string",
      "format": "uuid"
    },
    "playerIds": {
      "type": "array",
      "items": {
        "type": "string",
        "format": "uuid"
      }
    },
    "timestamp": {
      "type": "string",
      "format": "date-time"
    }
  },
  "required": ["gameName", "lobbyId", "firstPlayerId", "playerIds", "timestamp"],
  "additionalProperties": false,
  "allOf": [
    {
      "properties": {
        "playerIds": {
          "contains": {
            "const": {
              "$data": "1/firstPlayerId"
            }
          }
        }
      }
    }
  ]
}

```
### Player_move_event_schema

```
{
  "type": "object",
  "properties": {
    "lobbyId": {
      "type": "string",
      "format": "uuid"
    },
    "playerId": {
      "type": "string",
      "format": "uuid"
    },
    "nextPlayerId": {
      "type": "string",
      "format": "uuid"
    },
    "timestamp": {
      "type": "string",
      "format": "date-time"
    }
  },
  "required": ["lobbyId", "playerId", "nextPlayerId", "timestamp"],
  "additionalProperties": false
}

```

### Finish_game_session_event_schema

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "gameId": {
      "type": "string",
      "format": "uuid"
    },
    "winnerId": {
      "type": ["string", "null"],
      "format": "uuid"
    },
    "timestamp": {
      "type": "string",
      "format": "date-time"
    },
    "isDraw": {
      "type": "boolean"
    }
  },
  "required": ["gameId", "timestamp", "isDraw"],
  "additionalProperties": false,
  "if": {
    "properties": {
      "isDraw": {
        "const": true
      }
    }
  },
  "then": {
    "properties": {
      "winnerId": {
        "type": "null"
      }
    }
  }
}

```

### Lobby_created_event_schema

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "lobbyId": {
      "type": "string",
      "format": "uuid"
    },
    "firstPlayerId": {
      "type": "string",
      "format": "uuid"
    },
    "players": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "playerId": {
            "type": "string",
            "format": "uuid"
          },
          "username": {
            "type": "string"
          }
        },
        "required": ["playerId", "username"],
        "additionalProperties": false
      }
    }
  },
  "required": ["lobbyId", "firstPlayerId", "players"],
  "additionalProperties": false
}

```

### Achievement_schema

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "achievementId": {
      "type": "string",
      "format": "uuid",
      "description": "Unique identifier for the achievement."
    },
    "name": {
      "type": "string",
      "description": "The name of the achievement."
    },
    "description": {
      "type": "string",
      "description": "A description of the achievement."
    },
    "imageUrl": {
      "type": "string",
      "format": "uri",
      "description": "A URL to an image representing the achievement."
    },
    "achievementType": {
      "type": "string",
      "description": "The type/category of the achievement."
    },
    "isAchieved": {
      "type": "boolean",
      "description": "Indicates whether the achievement has been achieved."
    }
  },
  "required": [
    "achievementId",
    "name",
    "description",
    "imageUrl",
    "achievementType",
    "isAchieved"
  ],
  "additionalProperties": false
}

```