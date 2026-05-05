package com.github.swim_developer.validator.ed254.provider.infrastructure.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/ui")
@Produces(MediaType.TEXT_HTML)
public class UiResource {

    @GET
    @Path("/")
    public String dashboard() {
        return page("Dashboard", "<p>SWIM ED-254 Provider Validator — Dashboard</p>");
    }

    @GET
    @Path("/api")
    public String apiExplorer() {
        return page("API Explorer", "<p>Provider API proxy and explorer</p>");
    }

    @GET
    @Path("/subscriptions")
    public String subscriptions() {
        return page("Subscriptions", "<p>Subscription management</p>");
    }

    @GET
    @Path("/console")
    public String console() {
        return page("Console", "<p>Real-time console output</p>");
    }

    @GET
    @Path("/messages")
    public String messages() {
        return page("Messages", "<p>Received arrival sequence messages</p>");
    }

    @GET
    @Path("/test-scenarios")
    public String testScenarios() {
        return page("Conformance Tests", "<p>ED-254 conformance test scenarios</p>");
    }

    private String page(String title, String content) {
        return """
            <!DOCTYPE html>
            <html>
            <head><title>%s — ED-254 Provider Validator</title></head>
            <body><h1>%s</h1>%s</body>
            </html>""".formatted(title, title, content);
    }
}
