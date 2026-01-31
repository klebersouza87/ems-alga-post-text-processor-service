package com.ems.alga.posts.text.processor.service.infrastructure.rabbitmq;

import com.ems.alga.posts.text.processor.service.api.model.PostInput;
import com.ems.alga.posts.text.processor.service.api.model.PostOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private static final String QUEUE_TEXT_PROCESSOR_SERVICE = "text-processor-service.post-processing.v1.q";
    private static final String QUEUE_PROCESSED_MESSAGE = "post-service.post-processing-result.v1.q";

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = QUEUE_TEXT_PROCESSOR_SERVICE)
    public void handlePostMessage(@Payload PostInput postInput) {
        log.info("Received post message from RabbitMQ. PostId: {}, Body: {}", postInput.getPostId(), postInput.getPostBody());

        //TODO if below is only for testing purpose, remove it later. Used to simulate error and test Dead Letter Queue functionality.
        if (postInput.getPostBody().contains("ERROR")) {
            throw new RuntimeException("Simulated error during post creation for testing Dead Letter Queue purposes.");
        }

        int wordCount = postInput.getPostBody().split(" ").length;
        double calculatedValue = BigDecimal.valueOf(wordCount * 0.10).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        PostOutput postOutput = PostOutput.builder()
                .postId(postInput.getPostId())
                .wordCount(wordCount)
                .calculatedValue(calculatedValue)
                .build();

        log.info("Sending processed post message to RabbitMQ. PostId: {}, WordCount: {}, CalculatedValue: {}",
                 postOutput.getPostId(), postOutput.getWordCount(), postOutput.getCalculatedValue());

        rabbitTemplate.convertAndSend(QUEUE_PROCESSED_MESSAGE, postOutput);
    }

}
