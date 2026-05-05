package com.github.swim_developer.validator.ed254.provider.domain.model;

public record ArrivalSequenceEvent(
    String aerodromeDesignator,
    String messageType,
    int sequenceEntryCount,
    String callsigns,
    String publicationTime
) {}
