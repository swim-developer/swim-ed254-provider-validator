package com.github.swim_developer.validator.ed254.provider.infrastructure.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "received_messages")
public class ReceivedMessageEntity extends PanacheEntity {

    @Column(name = "subscription_id")
    public String subscriptionId;

    @Column(name = "queue_name")
    public String queueName;

    @Column(name = "message_id")
    public String messageId;

    @Column(name = "content_type")
    public String contentType;

    @Column(name = "message_type")
    public String messageType;

    @Column(name = "aerodrome_designator")
    public String aerodromeDesignator;

    @Column(name = "sequence_entry_count")
    public int sequenceEntryCount;

    @Column(columnDefinition = "TEXT")
    public String callsigns;

    @Column(name = "publication_time")
    public String publicationTime;

    @Column(columnDefinition = "LONGTEXT")
    public String body;

    @Column(name = "received_at")
    public LocalDateTime receivedAt;
}
