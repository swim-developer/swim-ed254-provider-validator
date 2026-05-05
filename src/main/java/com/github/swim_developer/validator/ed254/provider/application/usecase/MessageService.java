package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.domain.model.ArrivalSequenceEvent;
import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConsoleNotificationPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.MessagePersistencePort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.MessagePort;
import com.github.swim_developer.validator.ed254.provider.domain.port.out.ReceivedMessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MessageService implements MessagePersistencePort, MessagePort {

    @Inject
    ReceivedMessageRepository repository;

    @Inject
    Ed254ArrivalSequenceExtractor extractor;

    @Inject
    ConsoleNotificationPort console;

    @Override
    @Transactional
    public ReceivedMessage save(ReceivedMessage message) {
        ArrivalSequenceEvent event = extractor.extract(message.getBody());
        message.setMessageType(event.messageType());
        message.setAerodromeDesignator(event.aerodromeDesignator());
        message.setSequenceEntryCount(event.sequenceEntryCount());
        message.setCallsigns(event.callsigns());
        message.setPublicationTime(event.publicationTime());
        message.setReceivedAt(LocalDateTime.now());

        ReceivedMessage saved = repository.insert(message);
        console.messageReceived(message.getSubscriptionId(), message.getQueueName(),
            event.messageType(), event.aerodromeDesignator());
        return saved;
    }

    @Override
    public Optional<ReceivedMessage> findById(Long id) {
        return repository.findMessageById(id);
    }

    @Override
    public List<ReceivedMessage> findBySubscriptionId(String subscriptionId) {
        return repository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public List<ReceivedMessage> findRecentMessages(int limit) {
        return repository.findRecentMessages(limit);
    }

    @Override
    public List<ReceivedMessage> findBySubscriptionIds(List<String> subscriptionIds, int minutesBack) {
        LocalDateTime after = LocalDateTime.now().minusMinutes(minutesBack);
        return repository.findBySubscriptionIdsAfter(subscriptionIds, after);
    }

    @Override
    public long countBySubscriptionId(String subscriptionId) {
        return repository.countBySubscriptionId(subscriptionId);
    }
}
