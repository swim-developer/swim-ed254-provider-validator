package com.github.swim_developer.validator.ed254.provider.infrastructure.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Map;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {

    @ConfigProperty(name = "keycloak.url")
    String keycloakUrl;

    @ConfigProperty(name = "keycloak.realm")
    String keycloakRealm;

    @ConfigProperty(name = "keycloak.client-id")
    String keycloakClientId;

    @ConfigProperty(name = "swim.provider.api.urls")
    String providerApiUrls;

    @GET
    @Path("/config/keycloak")
    public Map<String, String> keycloakConfig() {
        return Map.of("url", keycloakUrl, "realm", keycloakRealm, "clientId", keycloakClientId);
    }

    @GET
    @Path("/config/provider")
    public Map<String, Object> providerConfig() {
        return Map.of("urls", providerApiUrls.split(","));
    }

    @GET
    @Path("/status")
    public Map<String, String> status() {
        return Map.of("service", "swim-ed254-provider-validator", "status", "UP");
    }
}
