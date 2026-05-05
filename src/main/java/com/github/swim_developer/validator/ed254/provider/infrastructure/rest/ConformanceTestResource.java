package com.github.swim_developer.validator.ed254.provider.infrastructure.rest;

import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConformanceTestPort;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.TestScenarioPort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api/conformance")
@Produces(MediaType.APPLICATION_JSON)
public class ConformanceTestResource {

    @Inject
    TestScenarioPort scenarioPort;

    @Inject
    ConformanceTestPort testPort;

    @GET
    @Path("/scenarios")
    public List<Map<String, Object>> listScenarios(@QueryParam("category") String category) {
        return scenarioPort.listScenarios(category);
    }

    @GET
    @Path("/scenarios/{id}")
    public Response getScenario(@PathParam("id") String id) {
        return scenarioPort.getScenario(id)
            .map(s -> Response.ok(s).build())
            .orElse(Response.status(404).build());
    }

    @POST
    @Path("/test")
    public Map<String, Object> executeTest(@QueryParam("scenarioId") String scenarioId,
                                           @QueryParam("providerUrl") String providerUrl,
                                           @HeaderParam("Authorization") String auth) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        return testPort.executeTest(scenarioId, providerUrl, token);
    }

    @POST
    @Path("/test/suite")
    public Response executeSuite(@QueryParam("suite") @DefaultValue("implemented") String suite,
                                @QueryParam("providerUrl") String providerUrl,
                                @HeaderParam("Authorization") String auth) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        List<Map<String, Object>> scenarios = scenarioPort.listScenarios(null);
        List<Map<String, Object>> results = scenarios.stream()
            .filter(s -> "implemented".equals(s.get("status")))
            .map(s -> testPort.executeTest((String) s.get("id"), providerUrl, token))
            .toList();
        return Response.ok(results).build();
    }
}
