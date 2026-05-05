package com.github.swim_developer.validator.ed254.provider.domain.port.in;

import com.github.swim_developer.validator.ed254.provider.domain.model.HttpResult;
import java.util.Map;

public interface ConformanceTestPort {
    Map<String, Object> executeTest(String scenarioId, String providerUrl, String bearerToken);
}
