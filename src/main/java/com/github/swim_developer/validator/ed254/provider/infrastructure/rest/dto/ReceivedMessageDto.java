package com.github.swim_developer.validator.ed254.provider.infrastructure.rest.dto;

import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;

public record ReceivedMessageDto(
    Long id,
    String subscriptionId,
    String queueName,
    String messageId,
    String messageType,
    String aerodromeDesignator,
    int sequenceEntryCount,
    String callsigns,
    String publicationTime,
    String receivedAt
) {
    public static ReceivedMessageDto from(ReceivedMessage m) {
        return new ReceivedMessageDto(
            m.getId(), m.getSubscriptionId(), m.getQueueName(), m.getMessageId(),
            m.getMessageType(), m.getAerodromeDesignator(), m.getSequenceEntryCount(),
            m.getCallsigns(), m.getPublicationTime(),
            m.getReceivedAt() != null ? m.getReceivedAt().toString() : null
        );
    }
}
