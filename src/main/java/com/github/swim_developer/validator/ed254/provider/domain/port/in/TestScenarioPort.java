package com.github.swim_developer.validator.ed254.provider.domain.port.in;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TestScenarioPort {
    List<Map<String, Object>> listScenarios(String category);
    Optional<Map<String, Object>> getScenario(String id);
}
