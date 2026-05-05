package com.github.swim_developer.validator.ed254.provider.infrastructure.messaging;

import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConnectionTrackerPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConsoleNotificationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserConnectionTracker implements ConnectionTrackerPort {

    private static final Logger LOG = Logger.getLogger(UserConnectionTracker.class);
    private static final long STALE_THRESHOLD_SECONDS = 90;

    @Inject
    UserReceiverLifecycle receiverLifecycle;

    @Inject
    ConsoleNotificationPort console;

    private final Map<String, ConnectionState> connections = new ConcurrentHashMap<>();

    @Override
    public void connect(String userId, String token, String amqpHost, int amqpPort, String username, String password) {
        LOG.infof("Connecting user %s to AMQP %s:%d", userId, amqpHost, amqpPort);
        ConnectionState state = new ConnectionState(userId, token, amqpHost, amqpPort, username, password);
        connections.put(userId, state);
        receiverLifecycle.createReceiversForUser(userId, amqpHost, amqpPort, username, password);
        console.amqpConnected(userId);
    }

    @Override
    public void disconnect(String userId) {
        ConnectionState state = connections.remove(userId);
        if (state != null) {
            receiverLifecycle.closeAllForUser(userId);
            console.amqpDisconnected(userId);
        }
    }

    @Override
    public void createReceiver(String userId, String queueName) {
        ConnectionState state = connections.get(userId);
        if (state != null) {
            receiverLifecycle.createReceiver(userId, queueName, state.amqpHost, state.amqpPort, state.username, state.password);
        }
    }

    @Override
    public void closeReceiverForQueue(String userId, String queueName) {
        receiverLifecycle.closeReceiver(userId, queueName);
    }

    @Override
    public boolean isConnected(String userId) {
        return connections.containsKey(userId);
    }

    @Override
    public Map<String, String> getReceiverStatus(String userId) {
        return receiverLifecycle.getReceiverStatus(userId);
    }

    @Override
    public void heartbeat(String userId, String token) {
        ConnectionState state = connections.get(userId);
        if (state != null) {
            state.lastHeartbeat = Instant.now();
            state.token = token;
        }
    }

    @Override
    public void performCleanup() {
        Instant threshold = Instant.now().minusSeconds(STALE_THRESHOLD_SECONDS);
        connections.entrySet().removeIf(entry -> {
            if (entry.getValue().lastHeartbeat.isBefore(threshold)) {
                LOG.infof("Cleaning up stale connection for user %s", entry.getKey());
                receiverLifecycle.closeAllForUser(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean testQueueAccess(String userId, String queueName) {
        ConnectionState state = connections.get(userId);
        if (state == null) return false;
        return receiverLifecycle.testQueueAccess(userId, queueName, state.amqpHost, state.amqpPort, state.username, state.password);
    }

    private static class ConnectionState {
        final String userId;
        String token;
        final String amqpHost;
        final int amqpPort;
        final String username;
        final String password;
        Instant lastHeartbeat;

        ConnectionState(String userId, String token, String amqpHost, int amqpPort, String username, String password) {
            this.userId = userId;
            this.token = token;
            this.amqpHost = amqpHost;
            this.amqpPort = amqpPort;
            this.username = username;
            this.password = password;
            this.lastHeartbeat = Instant.now();
        }
    }
}
