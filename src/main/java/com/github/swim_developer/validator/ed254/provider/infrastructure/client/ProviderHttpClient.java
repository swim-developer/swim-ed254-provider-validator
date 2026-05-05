package com.github.swim_developer.validator.ed254.provider.infrastructure.client;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ProviderHttpClient {

    private static final Logger LOG = Logger.getLogger(ProviderHttpClient.class);
    private static final int TIMEOUT_MS = 30000;

    @Inject
    Vertx vertx;

    @ConfigProperty(name = "proxy.mtls.keystore.path", defaultValue = "certs/keystore.p12")
    String keystorePath;

    @ConfigProperty(name = "proxy.mtls.keystore.password", defaultValue = "changeit")
    String keystorePassword;

    @ConfigProperty(name = "proxy.mtls.keystore.type", defaultValue = "PKCS12")
    String keystoreType;

    @ConfigProperty(name = "proxy.mtls.truststore.path", defaultValue = "certs/truststore.p12")
    String truststorePath;

    @ConfigProperty(name = "proxy.mtls.truststore.password", defaultValue = "changeit")
    String truststorePassword;

    @ConfigProperty(name = "proxy.mtls.truststore.type", defaultValue = "PKCS12")
    String truststoreType;

    private WebClient client;

    @PostConstruct
    void init() {
        WebClientOptions options = new WebClientOptions()
            .setSsl(true)
            .setTrustAll(false)
            .setVerifyHost(false)
            .setConnectTimeout(TIMEOUT_MS)
            .setIdleTimeout(TIMEOUT_MS);

        if ("PKCS12".equalsIgnoreCase(keystoreType)) {
            options.setPfxKeyCertOptions(new PfxOptions().setPath(keystorePath).setPassword(keystorePassword));
            options.setPfxTrustOptions(new PfxOptions().setPath(truststorePath).setPassword(truststorePassword));
        } else {
            options.setKeyStoreOptions(new JksOptions().setPath(keystorePath).setPassword(keystorePassword));
            options.setTrustStoreOptions(new JksOptions().setPath(truststorePath).setPassword(truststorePassword));
        }

        this.client = WebClient.create(vertx, options);
    }

    public Response get(String baseUrl, String path, String bearerToken) {
        return executeRequest("GET", baseUrl, path, bearerToken, null);
    }

    public Response post(String baseUrl, String path, String bearerToken, String body) {
        return executeRequest("POST", baseUrl, path, bearerToken, body);
    }

    public Response put(String baseUrl, String path, String bearerToken, String body) {
        return executeRequest("PUT", baseUrl, path, bearerToken, body);
    }

    public Response delete(String baseUrl, String path, String bearerToken) {
        return executeRequest("DELETE", baseUrl, path, bearerToken, null);
    }

    private Response executeRequest(String method, String baseUrl, String path, String bearerToken, String body) {
        try {
            URI uri = URI.create(baseUrl);
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equals(uri.getScheme()) ? 443 : 80);
            String fullPath = uri.getPath() + path;

            CompletableFuture<HttpResponse<Buffer>> future = new CompletableFuture<>();

            var request = switch (method) {
                case "POST" -> client.post(port, uri.getHost(), fullPath);
                case "PUT" -> client.put(port, uri.getHost(), fullPath);
                case "DELETE" -> client.delete(port, uri.getHost(), fullPath);
                default -> client.get(port, uri.getHost(), fullPath);
            };

            request.putHeader("Authorization", "Bearer " + bearerToken)
                   .putHeader("Content-Type", "application/json")
                   .putHeader("Accept", "application/json");

            if (body != null) {
                request.sendBuffer(Buffer.buffer(body), ar -> {
                    if (ar.succeeded()) future.complete(ar.result());
                    else future.completeExceptionally(ar.cause());
                });
            } else {
                request.send(ar -> {
                    if (ar.succeeded()) future.complete(ar.result());
                    else future.completeExceptionally(ar.cause());
                });
            }

            HttpResponse<Buffer> response = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            return Response.status(response.statusCode())
                .entity(response.bodyAsString())
                .build();
        } catch (Exception e) {
            LOG.errorf("HTTP %s %s%s failed: %s", method, baseUrl, path, e.getMessage());
            return Response.status(502).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
