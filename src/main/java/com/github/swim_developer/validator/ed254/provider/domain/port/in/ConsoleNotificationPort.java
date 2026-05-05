package com.github.swim_developer.validator.ed254.provider.domain.port.in;

public interface ConsoleNotificationPort {
    void info(String message);
    void success(String message);
    void warning(String message);
    void error(String message);
    void messageReceived(String subscriptionId, String queue, String messageType, String aerodrome);
    void amqpConnected(String userId);
    void amqpDisconnected(String userId);
}
