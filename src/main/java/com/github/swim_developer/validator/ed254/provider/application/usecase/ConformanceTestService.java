package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.domain.model.HttpResult;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConformanceHttpPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConformanceTestPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class ConformanceTestService implements ConformanceTestPort {

    private static final String BASE_PATH = "/arrivalSequenceInformation/v1";
    private static final String SUBSCRIBE_BODY = """
        {"subscriptionFilters":{"destinationAerodrome":[{"aerodromeDesignator":"LPPT"}]},"qos":"AT_LEAST_ONCE"}""";

    @Inject
    ConformanceHttpPort httpClient;

    @Override
    public Map<String, Object> executeTest(String scenarioId, String providerUrl, String bearerToken) {
        return switch (scenarioId) {
            case "API-01" -> testSubscribeHappyPath(providerUrl, bearerToken);
            case "API-02" -> testListSubscriptions(providerUrl, bearerToken);
            case "API-03" -> testGetTopics(providerUrl, bearerToken);
            case "API-04" -> testUnsubscribe(providerUrl, bearerToken);
            case "DM-01" -> testResponseRequiredFields(providerUrl, bearerToken);
            case "DM-02" -> testInitialPausedStatus(providerUrl, bearerToken);
            case "DM-03" -> testTopicReturnsArrivalSequenceService(providerUrl, bearerToken);
            case "WFS-01" -> testWfsGetFeature(providerUrl, bearerToken);
            default -> Map.of("error", "Unknown scenario: " + scenarioId);
        };
    }

    private Map<String, Object> testSubscribeHappyPath(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        assertions.assertStatusCode("POST /subscriptions returns 201", result, 201);
        assertions.assertFieldPresent("Response contains subscriptionId", result.body(), "subscriptionId");
        assertions.assertFieldPresent("Response contains queueName", result.body(), "queueName");
        return buildResult("API-01", "Subscribe — Happy Path (REQ 0100)", assertions);
    }

    private Map<String, Object> testListSubscriptions(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.get(url, BASE_PATH + "/subscriptions", token);
        assertions.assertStatusCode("GET /subscriptions returns 200", result, 200);
        return buildResult("API-02", "List Subscriptions (REQ 0120)", assertions);
    }

    private Map<String, Object> testGetTopics(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.get(url, BASE_PATH + "/topics", token);
        assertions.assertStatusCode("GET /topics returns 200", result, 200);
        assertions.assertFieldPresent("Response contains topics", result.body(), "topics");
        return buildResult("API-03", "Get Topics (REQ 0110)", assertions);
    }

    private Map<String, Object> testUnsubscribe(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult createResult = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        if (createResult.statusCode() == 201 && createResult.body() != null) {
            String subId = extractField(createResult.body(), "subscriptionId");
            if (subId != null) {
                HttpResult deleteResult = httpClient.delete(url,
                    BASE_PATH + "/subscriptions?subscriptionReference=" + subId, token);
                assertions.assertStatusCode("DELETE /subscriptions returns 200", deleteResult, 200);
                assertions.assertFieldPresent("Response contains unsubscriptionResult", deleteResult.body(), "unsubscriptionResult");
            }
        }
        return buildResult("API-04", "Unsubscribe (REQ 0150/0155)", assertions);
    }

    private Map<String, Object> testResponseRequiredFields(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        assertions.assertStatusCode("POST /subscriptions returns 201", result, 201);
        assertions.assertFieldPresent("subscriptionId present", result.body(), "subscriptionId");
        assertions.assertFieldPresent("subscriptionResult present", result.body(), "subscriptionResult");
        assertions.assertFieldPresent("queueName present", result.body(), "queueName");
        assertions.assertFieldPresent("subscriptionStatus present", result.body(), "subscriptionStatus");
        assertions.assertFieldPresent("subscriptionEnd present", result.body(), "subscriptionEnd");
        return buildResult("DM-01", "Required Fields in Response", assertions);
    }

    private Map<String, Object> testInitialPausedStatus(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        assertions.assertStatusCode("POST /subscriptions returns 201", result, 201);
        assertions.assertFieldEquals("Initial status is PAUSED", result.body(), "subscriptionStatus", "PAUSED");
        return buildResult("DM-02", "Initial PAUSED Status", assertions);
    }

    private Map<String, Object> testTopicReturnsArrivalSequenceService(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.get(url, BASE_PATH + "/topics", token);
        assertions.assertStatusCode("GET /topics returns 200", result, 200);
        boolean hasTopic = result.body() != null && result.body().contains("ArrivalSequenceService");
        assertions.assertFieldPresent("Topics contain ArrivalSequenceService", result.body(), "ArrivalSequenceService");
        return buildResult("DM-03", "Topics Returns ArrivalSequenceService", assertions);
    }

    private Map<String, Object> testWfsGetFeature(String url, String token) {
        ConformanceAssertions assertions = new ConformanceAssertions();
        HttpResult result = httpClient.get(url, "/swim/v1/features?typeName=arrivalSequence:ArrivalSequence&count=1", token);
        assertions.assertStatusCode("GET /features returns 200", result, 200);
        return buildResult("WFS-01", "WFS GetFeature Query", assertions);
    }

    private Map<String, Object> buildResult(String id, String name, ConformanceAssertions assertions) {
        return Map.of("scenarioId", id, "scenarioName", name,
            "passed", assertions.isAllPassed(), "checks", assertions.getResults());
    }

    private String extractField(String json, String field) {
        int idx = json.indexOf("\"" + field + "\":\"");
        if (idx < 0) return null;
        int start = idx + field.length() + 4;
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : null;
    }
}
