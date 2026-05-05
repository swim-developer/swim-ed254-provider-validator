package com.github.swim_developer.validator.ed254.provider.infrastructure.messaging;

import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.proton.ProtonClientOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AmqpSslConfigurator {

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

    public ProtonClientOptions configureSsl(ProtonClientOptions options) {
        options.setSsl(true);
        options.setHostnameVerificationAlgorithm("");

        if ("PKCS12".equalsIgnoreCase(keystoreType)) {
            options.setPfxKeyCertOptions(new PfxOptions().setPath(keystorePath).setPassword(keystorePassword));
            options.setPfxTrustOptions(new PfxOptions().setPath(truststorePath).setPassword(truststorePassword));
        } else {
            options.setKeyStoreOptions(new JksOptions().setPath(keystorePath).setPassword(keystorePassword));
            options.setTrustStoreOptions(new JksOptions().setPath(truststorePath).setPassword(truststorePassword));
        }

        return options;
    }
}
