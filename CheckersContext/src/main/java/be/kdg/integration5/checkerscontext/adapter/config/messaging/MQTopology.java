package be.kdg.integration5.checkerscontext.adapter.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQTopology {


    private static final String CHECKERS_EVENTS_EXCHANGE = "checkers_events";
    private static final String CHECKERS_MOVE_MADE_QUEUE = "checkers_move_made_queue";
    private static final String CHECKERS_GAME_STARTED_QUEUE = "checkers_game_started_queue";
    private static final String CHECKERS_GAME_FINISHED_QUEUE = "checkers_game_finished_queue";

    @Bean
    TopicExchange checkersEventsExchange() {
        return new TopicExchange(CHECKERS_EVENTS_EXCHANGE);
    }

    @Bean
    Queue checkersMovesQueue() {
        return new Queue(CHECKERS_MOVE_MADE_QUEUE, true);
    }

    @Bean
    Binding checkersMoveMadeBinding(TopicExchange checkersEventsExchange, Queue checkersMovesQueue) {
        return BindingBuilder
                .bind(checkersMovesQueue)
                .to(checkersEventsExchange)
                .with("checkers.#.move.made");
    }

    @Bean
    Queue checkersGameStartedQueue() {
        return new Queue(CHECKERS_GAME_STARTED_QUEUE, true);
    }

    @Bean
    Binding checkersGameStartedBinding(TopicExchange checkersEventsExchange, Queue checkersGameStartedQueue) {
        return BindingBuilder
                .bind(checkersGameStartedQueue)
                .to(checkersEventsExchange)
                .with("checkers.game.#.started");
    }

    @Bean
    Queue checkersGameFinishedQueue() {
        return new Queue(CHECKERS_GAME_FINISHED_QUEUE, true);
    }

    @Bean
    Binding checkersGameFinishedBinding(TopicExchange checkersEventsExchange, Queue checkersGameFinishedQueue) {
        return BindingBuilder
                .bind(checkersGameFinishedQueue)
                .to(checkersEventsExchange)
                .with("checkers.game.#.finished");
    }



    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;

    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
