package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConnectionTrackerPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConsoleNotificationPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ProviderSubscriptionPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SubscriptionService implements ProviderSubscriptionPort {

    @Inject
    ConnectionTrackerPort connectionTracker;

    @Inject
    ConsoleNotificationPort console;

    @Override
    public void onSubscriptionActivated(String userId, String subscriptionId, String queueName) {
        console.info("Subscription " + subscriptionId + " activated — creating receiver on " + queueName);
        if (connectionTracker.isConnected(userId)) {
            connectionTracker.createReceiver(userId, queueName);
        }
    }

    @Override
    public void onSubscriptionPaused(String userId, String subscriptionId, String queueName) {
        console.info("Subscription " + subscriptionId + " paused — closing receiver on " + queueName);
        connectionTracker.closeReceiverForQueue(userId, queueName);
    }

    @Override
    public void onSubscriptionDeleted(String userId, String subscriptionId, String queueName) {
        console.info("Subscription " + subscriptionId + " deleted — closing receiver on " + queueName);
        connectionTracker.closeReceiverForQueue(userId, queueName);
    }
}
