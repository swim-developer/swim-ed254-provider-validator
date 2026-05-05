package com.github.swim_developer.validator.ed254.provider.infrastructure.client;

import com.github.swim_developer.validator.ed254.provider.domain.model.HttpResult;
import com.github.swim_developer.validator.ed254.provider.domain.port.in.ConformanceHttpPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ConformanceHttpClient implements ConformanceHttpPort {

    @Inject
    ProviderHttpClient providerClient;

    @Override
    public HttpResult get(String providerUrl, String path, String bearerToken) {
        return toResult(providerClient.get(providerUrl, path, bearerToken));
    }

    @Override
    public HttpResult post(String providerUrl, String path, String bearerToken, String jsonBody) {
        return toResult(providerClient.post(providerUrl, path, bearerToken, jsonBody));
    }

    @Override
    public HttpResult put(String providerUrl, String path, String bearerToken, String jsonBody) {
        return toResult(providerClient.put(providerUrl, path, bearerToken, jsonBody));
    }

    @Override
    public HttpResult delete(String providerUrl, String path, String bearerToken) {
        return toResult(providerClient.delete(providerUrl, path, bearerToken));
    }

    private HttpResult toResult(Response response) {
        return new HttpResult(response.getStatus(), response.hasEntity() ? response.readEntity(String.class) : null);
    }
}
