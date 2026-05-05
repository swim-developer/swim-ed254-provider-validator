package com.github.swim_developer.validator.ed254.provider.domain.port.in;

import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;
import java.util.List;
import java.util.Optional;

public interface MessagePort {
    Optional<ReceivedMessage> findById(Long id);
    List<ReceivedMessage> findBySubscriptionId(String subscriptionId);
    List<ReceivedMessage> findRecentMessages(int limit);
    List<ReceivedMessage> findBySubscriptionIds(List<String> subscriptionIds, int minutesBack);
    long countBySubscriptionId(String subscriptionId);
}
