package com.github.swim_developer.validator.ed254.provider.domain.port.in;

import java.util.Map;

public interface ConnectionTrackerPort {
    void connect(String userId, String token, String amqpHost, int amqpPort, String username, String password);
    void disconnect(String userId);
    void createReceiver(String userId, String queueName);
    void closeReceiverForQueue(String userId, String queueName);
    boolean isConnected(String userId);
    Map<String, String> getReceiverStatus(String userId);
    void heartbeat(String userId, String token);
    void performCleanup();
    boolean testQueueAccess(String userId, String queueName);
}
