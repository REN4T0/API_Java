package com.renato.dominio.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@RegisterRestClient(configKey = "dog-ceo-api")
@Path("/api/breeds")
public interface DogBreedsAPI {

    @GET
    @Path("/list/all")
    Map<String, Object> searchExistingBreeds();
}
