package com.github.swim_developer.validator.ed254.provider.domain.port.out;

import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceivedMessageRepository {
    ReceivedMessage insert(ReceivedMessage message);
    Optional<ReceivedMessage> findMessageById(Long id);
    List<ReceivedMessage> findBySubscriptionId(String subscriptionId);
    List<ReceivedMessage> findByQueueName(String queueName);
    long countBySubscriptionId(String subscriptionId);
    List<ReceivedMessage> findBySubscriptionIds(List<String> ids);
    List<ReceivedMessage> findBySubscriptionIdsAfter(List<String> ids, LocalDateTime after);
    List<ReceivedMessage> findRecentMessages(int limit);
    List<ReceivedMessage> findMessagesAfter(LocalDateTime after);
}
