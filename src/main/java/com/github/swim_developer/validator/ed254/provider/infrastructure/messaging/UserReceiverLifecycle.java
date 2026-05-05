package com.github.swim_developer.validator.ed254.provider.infrastructure.messaging;

import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConsoleNotificationPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.MessagePersistencePort;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonClientOptions;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonReceiver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserReceiverLifecycle {

    private static final Logger LOG = Logger.getLogger(UserReceiverLifecycle.class);

    @Inject
    Vertx vertx;

    @Inject
    AmqpSslConfigurator sslConfigurator;

    @Inject
    MessagePersistencePort messagePersistence;

    @Inject
    ConsoleNotificationPort console;

    private final Map<String, Map<String, ProtonReceiver>> receivers = new ConcurrentHashMap<>();
    private final Map<String, ProtonConnection> connections = new ConcurrentHashMap<>();

    public void createReceiversForUser(String userId, String host, int port, String username, String password) {
        LOG.infof("Creating AMQP connection for user %s to %s:%d", userId, host, port);
    }

    public void createReceiver(String userId, String queueName, String host, int port, String username, String password) {
        ProtonClientOptions options = sslConfigurator.configureSsl(new ProtonClientOptions());
        ProtonClient client = ProtonClient.create(vertx);

        client.connect(options, host, port, username, password, connectResult -> {
            if (connectResult.succeeded()) {
                ProtonConnection connection = connectResult.result();
                connection.open();
                connections.put(userId + ":" + queueName, connection);

                ProtonReceiver receiver = connection.createReceiver(queueName);
                receiver.handler((delivery, message) -> {
                    try {
                        String body = extractBody(message);
                        ReceivedMessage msg = new ReceivedMessage();
                        msg.setSubscriptionId(userId);
                        msg.setQueueName(queueName);
                        msg.setMessageId(message.getMessageId() != null ? message.getMessageId().toString() : null);
                        msg.setContentType(message.getContentType());
                        msg.setBody(body);
                        messagePersistence.save(msg);
                        delivery.disposition(org.apache.qpid.proton.amqp.messaging.Accepted.getInstance(), true);
                    } catch (Exception e) {
                        LOG.errorf("Error processing message on queue %s: %s", queueName, e.getMessage());
                    }
                });
                receiver.open();
                receivers.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(queueName, receiver);
                console.info("Receiver created on queue " + queueName);
            } else {
                console.error("Failed to connect to AMQP for queue " + queueName + ": " + connectResult.cause().getMessage());
            }
        });
    }

    public void closeReceiver(String userId, String queueName) {
        Map<String, ProtonReceiver> userReceivers = receivers.get(userId);
        if (userReceivers != null) {
            ProtonReceiver receiver = userReceivers.remove(queueName);
            if (receiver != null) receiver.close();
        }
        ProtonConnection conn = connections.remove(userId + ":" + queueName);
        if (conn != null) conn.close();
    }

    public void closeAllForUser(String userId) {
        Map<String, ProtonReceiver> userReceivers = receivers.remove(userId);
        if (userReceivers != null) {
            userReceivers.values().forEach(ProtonReceiver::close);
        }
        connections.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(userId + ":")) {
                entry.getValue().close();
                return true;
            }
            return false;
        });
    }

    public Map<String, String> getReceiverStatus(String userId) {
        Map<String, ProtonReceiver> userReceivers = receivers.getOrDefault(userId, Map.of());
        Map<String, String> status = new ConcurrentHashMap<>();
        userReceivers.forEach((queue, receiver) -> status.put(queue, receiver.isOpen() ? "ACTIVE" : "CLOSED"));
        return status;
    }

    public boolean testQueueAccess(String userId, String queueName, String host, int port, String username, String password) {
        return true;
    }

    private String extractBody(org.apache.qpid.proton.message.Message message) {
        if (message.getBody() instanceof Data data) {
            return new String(data.getValue().getArray());
        } else if (message.getBody() instanceof AmqpValue value) {
            return value.getValue() != null ? value.getValue().toString() : "";
        }
        return "";
    }
}
