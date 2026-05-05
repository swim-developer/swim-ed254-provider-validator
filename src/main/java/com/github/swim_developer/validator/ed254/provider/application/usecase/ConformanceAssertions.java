package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.domain.model.HttpResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConformanceAssertions {

    private final List<Map<String, Object>> results = new ArrayList<>();
    private boolean allPassed = true;

    public void assertStatusCode(String label, HttpResult result, int expected) {
        boolean passed = result.statusCode() == expected;
        if (!passed) allPassed = false;
        results.add(Map.of("check", label, "passed", passed,
            "expected", expected, "actual", result.statusCode()));
    }

    public void assertFieldPresent(String label, String json, String fieldName) {
        boolean passed = json != null && json.contains("\"" + fieldName + "\"");
        if (!passed) allPassed = false;
        results.add(Map.of("check", label, "passed", passed, "field", fieldName));
    }

    public void assertFieldEquals(String label, String json, String fieldName, String expectedValue) {
        boolean passed = json != null && json.contains("\"" + fieldName + "\":\"" + expectedValue + "\"");
        if (!passed) allPassed = false;
        results.add(Map.of("check", label, "passed", passed,
            "field", fieldName, "expected", expectedValue));
    }

    public boolean isAllPassed() { return allPassed; }
    public List<Map<String, Object>> getResults() { return results; }
}
