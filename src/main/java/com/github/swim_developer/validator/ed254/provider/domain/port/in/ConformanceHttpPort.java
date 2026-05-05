package com.github.swim_developer.validator.ed254.provider.domain.port.in;

import com.github.swim_developer.validator.ed254.provider.domain.model.HttpResult;

public interface ConformanceHttpPort {
    HttpResult get(String providerUrl, String path, String bearerToken);
    HttpResult post(String providerUrl, String path, String bearerToken, String jsonBody);
    HttpResult put(String providerUrl, String path, String bearerToken, String jsonBody);
    HttpResult delete(String providerUrl, String path, String bearerToken);
}
