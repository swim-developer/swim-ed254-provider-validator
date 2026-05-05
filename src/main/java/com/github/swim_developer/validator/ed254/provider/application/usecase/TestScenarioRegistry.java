package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.domain.port.in.TestScenarioPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class TestScenarioRegistry implements TestScenarioPort {

    private static final List<Map<String, Object>> SCENARIOS = List.of(
        scenario("API-01", "REST API", "Subscribe — Happy Path (REQ 0100)",
            "Creates a subscription with aerodrome filter and validates the response", "implemented"),
        scenario("API-02", "REST API", "List Subscriptions (REQ 0120)",
            "Lists all subscriptions for the authenticated user", "implemented"),
        scenario("API-03", "REST API", "Get Topics (REQ 0110)",
            "Retrieves available topics from the provider", "implemented"),
        scenario("API-04", "REST API", "Unsubscribe (REQ 0150/0155)",
            "Creates and then deletes a subscription", "implemented"),
        scenario("DM-01", "Data Model", "Required Fields in Response",
            "Validates all mandatory fields in subscription response", "implemented"),
        scenario("DM-02", "Data Model", "Initial PAUSED Status",
            "Verifies new subscriptions start in PAUSED status", "implemented"),
        scenario("DM-03", "Data Model", "Topics Returns ArrivalSequenceService",
            "Verifies the topics endpoint returns ArrivalSequenceService", "implemented"),
        scenario("WFS-01", "WFS", "WFS GetFeature Query",
            "Tests OGC WFS 2.0 GetFeature endpoint for arrival sequences", "implemented"),
        scenario("API-05", "REST API", "Pause Subscription (REQ 0130)",
            "Pauses an active subscription", "roadmap"),
        scenario("API-06", "REST API", "Resume Subscription (REQ 0135)",
            "Resumes a paused subscription", "roadmap"),
        scenario("API-07", "REST API", "Renew Subscription (REQ 0140)",
            "Extends subscription TTL", "roadmap"),
        scenario("PROB-01", "Problem Reporting", "Report Data Quality Issue (REQ 0165)",
            "Reports a data validation problem to the provider", "roadmap")
    );

    @Override
    public List<Map<String, Object>> listScenarios(String category) {
        if (category == null || category.isBlank()) return SCENARIOS;
        return SCENARIOS.stream()
            .filter(s -> category.equalsIgnoreCase((String) s.get("category")))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Map<String, Object>> getScenario(String id) {
        return SCENARIOS.stream().filter(s -> id.equals(s.get("id"))).findFirst();
    }

    private static Map<String, Object> scenario(String id, String category, String name, String description, String status) {
        return Map.of("id", id, "category", category, "name", name, "description", description, "status", status);
    }
}
