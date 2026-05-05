package com.github.swim_developer.validator.ed254.provider.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReceivedMessage {
    private Long id;
    private String subscriptionId;
    private String queueName;
    private String messageId;
    private String contentType;
    private String messageType;
    private String aerodromeDesignator;
    private int sequenceEntryCount;
    private String callsigns;
    private String publicationTime;
    private String body;
    private LocalDateTime receivedAt;
}
