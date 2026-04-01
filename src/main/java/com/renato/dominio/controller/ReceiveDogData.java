package com.renato.dominio.controller;

import com.renato.dominio.application.DogsApp;

import com.renato.dominio.dto.DogsDTO;
import com.renato.dominio.entity.Dogs;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Path("/dogs")
public class ReceiveDogData {

    @Inject
    DogsApp dogsService;

    @POST
    public Response receiveToPost(@Valid DogsDTO DogDtoObj) {
        return dogsService.createDogs(DogDtoObj);
    }

    @PUT
    @Path("/{id}")
    public Response receiveToUpdate(@PathParam("id") UUID id, @Valid DogsDTO DogDtoObj) {
        // Validando a existência do cachorro no banco de dados para, posteriormente, poder atualizar.
        final Dogs dogExist = dogsService.searchDogsPerId(id);
        if(dogExist != null){
            return dogsService.updateDogs(id, DogDtoObj, dogExist);
        }

        final HashMap<String, String> RESPONSE = new HashMap<String, String>();
        RESPONSE.put("code", "404");
        RESPONSE.put("status", "success");
        RESPONSE.put("msg", "Não foi possível atualizar o registro, porque ele não existe no banco");

        return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();
    }

    @GET
    public Response receiveToGetAll(){
       final List<Dogs> RESPONSE = dogsService.getAllDogs();
       return Response.ok(RESPONSE).build();
    }
}
