package com.github.swim_developer.validator.ed254.provider.infrastructure.rest;

import com.github.swim_developer.validator.ed254.provider.application.port.in.ConsoleStreamPort;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/api/console")
public class ConsoleResource {

    @Inject
    ConsoleStreamPort consoleStream;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<String> stream() {
        return consoleStream.stream();
    }
}
