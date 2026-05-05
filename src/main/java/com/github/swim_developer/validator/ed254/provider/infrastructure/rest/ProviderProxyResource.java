package com.github.swim_developer.validator.ed254.provider.infrastructure.rest;

import com.github.swim_developer.validator.ed254.provider.infrastructure.client.ProviderHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/provider")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProviderProxyResource {

    private static final String BASE_PATH = "/arrivalSequenceInformation/v1";

    @Inject
    ProviderHttpClient providerClient;

    @ConfigProperty(name = "swim.provider.api.urls")
    String providerUrls;

    @POST
    @Path("/subscriptions")
    public Response createSubscription(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url, String body) {
        return providerClient.post(resolveUrl(url), BASE_PATH + "/subscriptions", extractToken(auth), body);
    }

    @GET
    @Path("/subscriptions")
    public Response listSubscriptions(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url) {
        return providerClient.get(resolveUrl(url), BASE_PATH + "/subscriptions", extractToken(auth));
    }

    @GET
    @Path("/subscriptions/{id}")
    public Response getSubscription(@PathParam("id") String id, @HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url) {
        return providerClient.get(resolveUrl(url), BASE_PATH + "/subscriptions/" + id, extractToken(auth));
    }

    @PUT
    @Path("/subscriptions/{id}")
    public Response updateSubscription(@PathParam("id") String id, @HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url, String body) {
        return providerClient.put(resolveUrl(url), BASE_PATH + "/subscriptions/" + id, extractToken(auth), body);
    }

    @DELETE
    @Path("/subscriptions")
    public Response deleteSubscription(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url, @QueryParam("subscriptionReference") String ref) {
        return providerClient.delete(resolveUrl(url), BASE_PATH + "/subscriptions?subscriptionReference=" + ref, extractToken(auth));
    }

    @GET
    @Path("/topics")
    public Response getTopics(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url) {
        return providerClient.get(resolveUrl(url), BASE_PATH + "/topics", extractToken(auth));
    }

    @GET
    @Path("/topics/{id}")
    public Response getTopic(@PathParam("id") String id, @HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url) {
        return providerClient.get(resolveUrl(url), BASE_PATH + "/topics/" + id, extractToken(auth));
    }

    @GET
    @Path("/features")
    public Response getFeatures(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url) {
        return providerClient.get(resolveUrl(url), "/swim/v1/features", extractToken(auth));
    }

    @POST
    @Path("/features")
    public Response queryFeatures(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url, String body) {
        return providerClient.post(resolveUrl(url), "/swim/v1/features", extractToken(auth), body);
    }

    @POST
    @Path("/problems")
    public Response reportProblem(@HeaderParam("Authorization") String auth, @QueryParam("providerUrl") String url, String body) {
        return providerClient.post(resolveUrl(url), BASE_PATH + "/problems", extractToken(auth), body);
    }

    private String resolveUrl(String url) {
        return (url != null && !url.isBlank()) ? url : providerUrls.split(",")[0].trim();
    }

    private String extractToken(String auth) {
        return auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
    }
}
