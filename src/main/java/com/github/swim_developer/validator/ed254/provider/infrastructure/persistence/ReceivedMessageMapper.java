package com.github.swim_developer.validator.ed254.provider.infrastructure.persistence;

import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;
import com.github.swim_developer.validator.ed254.provider.infrastructure.persistence.entity.ReceivedMessageEntity;

public class ReceivedMessageMapper {

    private ReceivedMessageMapper() {}

    public static ReceivedMessageEntity toEntity(ReceivedMessage model) {
        ReceivedMessageEntity entity = new ReceivedMessageEntity();
        entity.subscriptionId = model.getSubscriptionId();
        entity.queueName = model.getQueueName();
        entity.messageId = model.getMessageId();
        entity.contentType = model.getContentType();
        entity.messageType = model.getMessageType();
        entity.aerodromeDesignator = model.getAerodromeDesignator();
        entity.sequenceEntryCount = model.getSequenceEntryCount();
        entity.callsigns = model.getCallsigns();
        entity.publicationTime = model.getPublicationTime();
        entity.body = model.getBody();
        entity.receivedAt = model.getReceivedAt();
        return entity;
    }

    public static ReceivedMessage toDomain(ReceivedMessageEntity entity) {
        ReceivedMessage model = new ReceivedMessage();
        model.setId(entity.id);
        model.setSubscriptionId(entity.subscriptionId);
        model.setQueueName(entity.queueName);
        model.setMessageId(entity.messageId);
        model.setContentType(entity.contentType);
        model.setMessageType(entity.messageType);
        model.setAerodromeDesignator(entity.aerodromeDesignator);
        model.setSequenceEntryCount(entity.sequenceEntryCount);
        model.setCallsigns(entity.callsigns);
        model.setPublicationTime(entity.publicationTime);
        model.setBody(entity.body);
        model.setReceivedAt(entity.receivedAt);
        return model;
    }
}
