package com.github.swim_developer.validator.ed254.provider.infrastructure.messaging;

import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConnectionTrackerPort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AmqpConnectionCleanupScheduler {

    @Inject
    ConnectionTrackerPort connectionTracker;

    @Scheduled(every = "30s")
    void cleanup() {
        connectionTracker.performCleanup();
    }
}
