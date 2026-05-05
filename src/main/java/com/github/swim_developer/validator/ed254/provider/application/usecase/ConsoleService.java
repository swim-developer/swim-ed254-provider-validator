package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.application.port.in.ConsoleStreamPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConsoleNotificationPort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class ConsoleService implements ConsoleNotificationPort, ConsoleStreamPort {

    private final BroadcastProcessor<String> processor = BroadcastProcessor.create();

    @Override
    public Multi<String> stream() {
        return processor;
    }

    @Override
    public void info(String message) {
        emit("info", message);
    }

    @Override
    public void success(String message) {
        emit("success", message);
    }

    @Override
    public void warning(String message) {
        emit("warning", message);
    }

    @Override
    public void error(String message) {
        emit("error", message);
    }

    @Override
    public void messageReceived(String subscriptionId, String queue, String messageType, String aerodrome) {
        emit("message_received", String.format("[%s] %s from %s on queue %s", messageType, aerodrome, subscriptionId, queue));
    }

    @Override
    public void amqpConnected(String userId) {
        emit("amqp_connected", "AMQP connected for user " + userId);
    }

    @Override
    public void amqpDisconnected(String userId) {
        emit("amqp_disconnected", "AMQP disconnected for user " + userId);
    }

    private void emit(String type, String message) {
        processor.onNext(String.format("{\"type\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}", type, escape(message), Instant.now()));
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
