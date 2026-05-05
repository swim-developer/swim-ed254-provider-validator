package com.github.swim_developer.validator.ed254.provider.domain.port.in;

import com.github.swim_developer.validator.ed254.provider.domain.model.ReceivedMessage;

public interface MessagePersistencePort {
    ReceivedMessage save(ReceivedMessage message);
}
