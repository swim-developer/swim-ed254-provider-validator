package com.github.swim_developer.validator.ed254.provider.infrastructure.config;

import com.github.swim_developer.validator.ed254.provider.domain.model.ArrivalSequenceEvent;
import com.github.swim_developer.validator.ed254.provider.domain.model.MessageData;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = {ArrivalSequenceEvent.class, MessageData.class})
public class ReflectionConfiguration {}
