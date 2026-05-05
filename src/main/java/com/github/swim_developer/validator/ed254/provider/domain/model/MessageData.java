package com.github.swim_developer.validator.ed254.provider.domain.model;

public record MessageData(
    String subscriptionId,
    String queueName,
    String messageType,
    String aerodromeDesignator,
    int sequenceEntryCount,
    String callsigns
) {}
