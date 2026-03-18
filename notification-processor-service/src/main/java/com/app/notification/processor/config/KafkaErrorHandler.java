package com.app.notification.processor.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;

@Component
public class KafkaErrorHandler implements ConsumerRecordRecoverer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaErrorHandler.class);

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        logger.error("Error processing record: {}", consumerRecord, exception);
        
        if (exception instanceof ListenerExecutionFailedException && 
            exception.getCause() instanceof DeserializationException) {
            
            DeserializationException deserializationException = (DeserializationException) exception.getCause();
            logger.error("Deserialization error for topic: {}, partition: {}, offset: {}, key: {}", 
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                consumerRecord.key(),
                deserializationException);
            
            // Note: We cannot seek to next record here as we don't have access to the consumer
            // This handler should be used with a DeadLetterTopicErrorHandler or similar
            logger.warn("Cannot skip record - consumer not available in this interface. Consider using a different error handling strategy.");
        }
    }
}
