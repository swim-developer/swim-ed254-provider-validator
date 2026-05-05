package com.github.swim_developer.validator.ed254.provider.application.port.in;

import io.smallrye.mutiny.Multi;

public interface ConsoleStreamPort {
    Multi<String> stream();
}
