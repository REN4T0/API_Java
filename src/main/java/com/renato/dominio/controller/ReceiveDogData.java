package com.renato.dominio.controller;

import com.renato.dominio.application.DogsApp;
import com.renato.dominio.dto.DogsDTO;
import com.renato.dominio.entity.Dogs;
import com.renato.dominio.client.ValidDogBreedsList;
import com.renato.dominio.exceptions.BreedNotFoundException;
import com.renato.dominio.exceptions.RecordAlreadyExistsException;
import com.renato.dominio.exceptions.RecordNotFoundException;
import com.renato.dominio.telemetry.BackendTelemetry;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.logging.Log;

import java.util.*;

@Path("/dogs")
public class ReceiveDogData{

    // 1. Instancie o Logger
    //private static final Logger LOG = Logger.getLogger(ReceiveDogData.class);

    @Inject
    DogsApp dogsService;

    @Inject
    ValidDogBreedsList validateBreed;

    @Inject
    BackendTelemetry telemetry;


    @POST
    public Response receiveToPost(@Valid DogsDTO DogDtoObj) {
        Status RESPONSE;

        try {
            if (validateBreed.makeBreedArray().contains(DogDtoObj.getBreed())) {
                RESPONSE = new Status("200", "success", "O registro foi cadastrado com sucesso.");
                RESPONSE.more_info = dogsService.createDogs(DogDtoObj);
                return Response.ok(RESPONSE).build();
            }

            RESPONSE = new Status("404", "not_found", "A raça (breed) enviada é inválida/inexistente.");
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        }
        catch (RecordAlreadyExistsException exRecord) {
            RESPONSE = new Status("409", "conflict", "O registro enviado já existe no banco de dados.");
            RESPONSE.more_info = exRecord;

            return Response.status(Response.Status.CONFLICT).entity(RESPONSE).build();
        }
        catch (Exception err) {
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

            if(validateBreed.makeBreedArray().contains(DogDtoObj.getBreed())){
                RESPONSE = new Status("200", "success", "O registro foi atualizado com êxito.");
                RESPONSE.more_info = result;
                return Response.ok(RESPONSE).build();
            }

            throw new BreedNotFoundException();

        }
        catch (BreedNotFoundException exBreedNotFound) {
            RESPONSE = new Status("404", "not_found", "A raça (breed) enviada é inválida/inexistente.");
            RESPONSE.more_info = exBreedNotFound;
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();
        }
        catch (RecordAlreadyExistsException exRecord) {
            Log.infof("REGISTRO NAO ENCONTRADO: %s", id, exRecord);
            RESPONSE = new Status("409", "conflict", "Nenhuma alteração foi feita no registro ou o registro já existe no banco de dados.");
            RESPONSE.more_info = exRecord;
            return Response.status(Response.Status.CONFLICT).entity(RESPONSE).build();
        }
        catch (RecordNotFoundException exNotFound) {
            RESPONSE = new Status("404", "not_found", "O Id informado nao foi encontrado na base de dados.");
            RESPONSE.more_info = exNotFound;
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();
        }
        catch (Exception err) {
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de cadastrar o registro.");
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

    @GET
    @Path("/breeds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAvailableBreeds() {
        try {
            return Response.ok(validateBreed.makeBreedArray()).build();

        } catch (Exception err) {
            Status RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com a API externa no momento de buscar a lista de raças existentes/válidas.");
            RESPONSE.more_info = err;
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();

        }
    }

    @GET
    @Path("/telemetry")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveToGetAppTelemetry(){
        return Response.ok(telemetry.getTelemetry()).build();
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
    }}