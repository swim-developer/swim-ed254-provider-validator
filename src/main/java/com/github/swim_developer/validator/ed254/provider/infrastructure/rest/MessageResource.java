package com.github.swim_developer.validator.ed254.provider.infrastructure.rest;

import com.github.swim_developer.validator.ed254.provider.domain.port.in.MessagePort;
import com.github.swim_developer.validator.ed254.provider.infrastructure.rest.dto.ReceivedMessageDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject
    MessagePort messagePort;

    @GET
    @Path("/recent")
    public Response recentMessages(@QueryParam("limit") @DefaultValue("50") int limit) {
        var messages = messagePort.findRecentMessages(limit)
            .stream().map(ReceivedMessageDto::from).toList();
        return Response.ok(messages).build();
    }

    @GET
    @Path("/{id}")
    public Response getMessage(@PathParam("id") Long id) {
        return messagePort.findById(id)
            .map(m -> Response.ok(ReceivedMessageDto.from(m)).build())
            .orElse(Response.status(404).build());
    }

    @GET
    @Path("/{id}/xml")
    @Produces(MediaType.APPLICATION_XML)
    public Response getMessageXml(@PathParam("id") Long id) {
        return messagePort.findById(id)
            .map(m -> Response.ok(m.getBody()).build())
            .orElse(Response.status(404).build());
    }
}
