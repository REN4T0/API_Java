package com.renato.dominio.controller;

import com.renato.dominio.application.DogsApp;

import com.renato.dominio.dto.DogsDTO;
import com.renato.dominio.entity.Dogs;
import io.micrometer.observation.annotation.ObservationKeyValue;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Path("/dogs")
public class ReceiveDogData{

    @Inject
    DogsApp dogsService;

    @POST
    public Response receiveToPost(@Valid DogsDTO DogDtoObj) {
        Status RESPONSE;

        try {
            RESPONSE = new Status("200", "success", "O registro foi cadastrado com sucesso.");
            RESPONSE.more_info = dogsService.createDogs(DogDtoObj);
            return Response.ok(RESPONSE).build();

        } catch (Exception err) {
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de cadastrar o registro.");
            RESPONSE.more_info = err;
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response receiveToUpdate(@PathParam("id") UUID id, @Valid DogsDTO DogDtoObj) {
        // Validando a existência do cachorro no banco de dados para, posteriormente, poder atualizar.
        Status RESPONSE;

        try {
            HashMap <String, Object> result = dogsService.updateDogs(id, DogDtoObj);

            if(result != null){
                RESPONSE = new Status("200", "success", "O registro foi atualizado com êxito.");
                RESPONSE.more_info = result;
                return Response.ok(RESPONSE).build();
            }

            RESPONSE = new Status("404", "not_found", "Não foi possível atualizar o registro, porque ele não existe no banco.");
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        } catch (Exception err) {
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de atualizar o registro.");
            RESPONSE.more_info = err;
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();
        }
    }

    @GET
    public Response receiveToGetAll(){
        try {
            final List<Dogs> RESPONSE = dogsService.getAllDogs();
            return Response.ok(RESPONSE).build();

        } catch (Exception err) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de buscar os registros.").more_info = err).build();
        }
    }

    @GET
    @Path("/apelido/{surname}")
    public Response receiveToSearchSurname(@PathParam("surname") String surname){
        try {
            final List<Dogs> RESPONSE = dogsService.searchDogsPerSurname(surname);
            return Response.ok(RESPONSE).build();

        } catch (Exception err) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de buscar o registro com base no campo \"apelido\" (surname).").more_info = err).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response receiveToDelete(@PathParam("id") UUID id){
        Status RESPONSE;

        try {
            if(dogsService.deleteDogs(id)){
                RESPONSE = new Status("200", "success", "O registro foi deletado com sucesso.");
                return Response.ok(RESPONSE).build();
            }

            RESPONSE = new Status("404", "not_found", "O registro não foi deletado, porque ele não existe no banco de dados.");
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        } catch (Exception err){
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de deletar o registro.");
            RESPONSE.more_info = err;
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();
        }
    }
}