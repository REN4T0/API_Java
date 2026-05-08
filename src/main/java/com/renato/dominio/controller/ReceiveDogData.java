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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.logging.Log;
import io.vertx.core.http.HttpServerRequest;
import java.util.*;

@Path("/dogs")
public class ReceiveDogData {

    @Inject
    DogsApp dogsService;

    @Inject
    ValidDogBreedsList validateBreed;

    @Inject
    BackendTelemetry telemetry;

    @Context
    HttpServerRequest request;

    private String getClientIP() {
        return request.remoteAddress().host();
    }

    @POST
    public Response receiveToPost(@Valid DogsDTO DogDtoObj) {
        Log.infof("| REQUISIÇÃO DE CADASTRO DE UM NOVO REGISTRO RECEBIDA. INICIANDO PROCEDIMENTO...\nDADOS RECEBIDOS -> Raça: %s, Apelido: %s, Sexo: %s\nIP QUE REQUISITOU A OPERAÇÃO: %s\n", DogDtoObj.getBreed(), DogDtoObj.getSurname(), DogDtoObj.getGender(), getClientIP());
        Status RESPONSE;

        try {
            if (validateBreed.makeBreedArray().contains(DogDtoObj.getBreed())) {
                HashMap<String, Object> result = dogsService.createDogs(DogDtoObj);
                RESPONSE = new Status("200", "success", "O registro foi cadastrado com sucesso.");
                RESPONSE.more_info = result;

                Log.infof("| CADASTRO DE NOVO REGISTRO REALIZADO COM SUCESSO.\nDETALHES: %s\n", result);
                return Response.ok(RESPONSE).build();
            }

            throw new BreedNotFoundException();

        } catch (RecordAlreadyExistsException exRecord) {
            RESPONSE = new Status("409", "conflict", "O registro enviado já existe no banco de dados.");
            RESPONSE.more_info = exRecord;
            Log.warnf("| NÃO FOI POSSÍVEL CADASTRAR O REGISTRO RECEBIDO, PORQUE ELE JÁ EXISTE NO BANCO DE DADOS.\nCÓDIGO: 409 - CONFLICT\nDADOS QUE NÃO FOI POSSÍVEL CADASTRAR -> Raça: %s, Apelido: %s, Sexo: %s\n", DogDtoObj.getBreed(), DogDtoObj.getSurname(), DogDtoObj.getGender());
            return Response.status(Response.Status.CONFLICT).entity(RESPONSE).build();

        } catch (BreedNotFoundException exBreedNotFound) {
            RESPONSE = new Status("404", "not_found", "A raça (breed) enviada é inválida/inexistente.");
            Log.warnf("| NÃO FOI POSSÍVEL CADASTRAR O REGISTRO RECEBIDO, PORQUE A RAÇA ENVIADA É INVÁLIDA OU INEXISTENTE.\nCÓDIGO: 404 - NOT FOUND\nDADO COM CONTEÚDO INVÁLIDO/INEXISTENTE -> Raça: %s\n", DogDtoObj.getBreed());
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        } catch (Exception ex) {
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de cadastrar o registro.");
            RESPONSE.more_info = ex;
            Log.errorf("| OCORREU UM ERRO INESPERADO ENVOLVENDO A COMUNICAÇÃO COM O BANCO DE DADOS NO MOMENTO DE CADASTRAR UM NOVO REGISTRO.\nCÓDIGO: 500 - INTERNAL SERVER ERROR\nDETALHES - %s\n", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response receiveToUpdate(@PathParam("id") UUID id, @Valid DogsDTO DogDtoObj) {
        Log.infof("| REQUISIÇÃO DE ATUALIZAÇÃO DE REGISTRO RECEBIDA PARA O ID: %s \nDADOS RECEBIDOS -> Raça: %s, Apelido: %s, Sexo: %s\nIP QUE REQUISITOU A OPERAÇÃO: %s\n", id, DogDtoObj.getBreed(), DogDtoObj.getSurname(), DogDtoObj.getGender(), getClientIP());
        Status RESPONSE;

        try {
            if (validateBreed.makeBreedArray().contains(DogDtoObj.getBreed())) {
                HashMap<String, Object> result = dogsService.updateDogs(id, DogDtoObj);
                RESPONSE = new Status("200", "success", "O registro foi atualizado com êxito.");
                RESPONSE.more_info = result;

                Log.infof("| O REGISTRO DE ID %s FOI ATUALIZADO COM SUCESSO.\nDETALHES: %s\n", id, result);
                return Response.ok(RESPONSE).build();
            }

            throw new BreedNotFoundException();
        } catch (BreedNotFoundException exBreedNotFound) {
            RESPONSE = new Status("404", "not_found", "A raça (breed) enviada é inválida/inexistente.");
            RESPONSE.more_info = exBreedNotFound;
            Log.warnf("| O REGISTRO DE ID %s NÃO FOI ATUALIZADO, PORQUE A RAÇA ENVIADA É INVÁLIDA OU INEXISTENTE.\nCÓDIGO: 404 - NOT FOUND\nDADO COM CONTEÚDO INVÁLIDO/INEXISTENTE -> Raça: %s\n", id, DogDtoObj.getBreed());
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        } catch (RecordAlreadyExistsException exRecord) {
            Log.warnf("| O REGISTRO DE ID %s NÃO FOI ATUALIZADO, PORQUE A COMBINAÇÃO DOS DADOS RECEBIDOS JÁ EXISTEM EM OUTRO REGISTRO NO BANCO DE DADOS.\nCÓDIGO: 409 - CONFLICT\nNÃO FOI POSSÍVEL ATUALIZAR OS SEGUINTES DADOS -> Raça: %s, Apelido: %s, Sexo: %s\n", id, DogDtoObj.getBreed(), DogDtoObj.getSurname(), DogDtoObj.getGender());
            RESPONSE = new Status("409", "conflict", "O registro já existe no banco de dados.");
            RESPONSE.more_info = exRecord;
            return Response.status(Response.Status.CONFLICT).entity(RESPONSE).build();

        } catch (RecordNotFoundException exNotFound) {
            RESPONSE = new Status("404", "not_found", "O Id informado nao foi encontrado na base de dados.");
            RESPONSE.more_info = exNotFound;
            Log.warnf("| NÃO FOI POSSÍVEL ATUALIZAR, PORQUE O REGISTRO DE ID %s NÃO EXISTE NO BANCO DE DADOS.\nCÓDIGO: 404 - NOT FOUND\n", id);
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        } catch (Exception ex) {
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de cadastrar o registro.");
            RESPONSE.more_info = ex;
            Log.errorf("| OCORREU UM ERRO INESPERADO ENVOLVENDO A COMUNICAÇÃO COM O BANCO DE DADOS NO MOMENTO DE ATUALIZAR O REGISTRO DE ID: %s\nCÓDIGO: 500 - INTERNAL SERVER ERROR\nDETALHES - %s\n", id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();
        }
    }


    @GET
    public Response receiveToGetAll() {
        Log.infof("| REQUISIÇÃO DE CONSULTA DE REGISTROS CADASTRADOS NO BANCO DE DADOS RECEBIDA.\nIP QUE REQUISITOU A OPERAÇÃO: %s\n", getClientIP());

        try {
            final List<Dogs> RESPONSE = dogsService.getAllDogs();
            Log.info("| CONSULTA DE REGISTROS CADASTRADOS CONCLUÍDA. RETORNANDO DADOS OBTIDOS PARA O CLIENTE...\n");
            return Response.ok(RESPONSE).build();

        } catch (Exception ex) {
            Log.errorf("| OCORREU UM ERRO INESPERADO ENVOLVENDO A COMUNICAÇÃO COM O BANCO DE DADOS NO MOMENTO DE CONSULTAR OS REGISTROS CADASTRADOS.\nCÓDIGO: 500 - INTERNAL SERVER ERROR\n DETALHES - %s\n", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de buscar os registros.").more_info = ex).build();
        }
    }

    @GET
    @Path("/apelido/{surname}")
    public Response receiveToSearchSurname(@PathParam("surname") String surname) {
        Log.infof("| REQUISIÇÃO DE BUSCA POR REGISTROS QUE CONTENHAM '%s' NO APELIDO RECEBIDA.\nIP QUE REQUISITOU A OPERAÇÃO: %s\n", surname, getClientIP());

        try {
            final List<Dogs> RESPONSE = dogsService.searchDogsPerSurname(surname);
            Log.infof("| BUSCA POR REGISTROS QUE CONTENHAM '%s' NO APELIDO CONCLUÍDA. RETORNANDO DADOS OBTIDOS PARA O CLIENTE...", surname);
            return Response.ok(RESPONSE).build();

        } catch (Exception ex) {
            Log.errorf("| OCORREU UM ERRO INESPERADO ENVOLVENDO A COMUNICAÇÃO COM O BANCO DE DADOS NO MOMENTO DE BUSCAR UM REGISTRO PELO APELIDO '%s'.\nCÓDIGO: 500 - INTERNAL SERVER ERROR\n DETALHES - %s\n", surname, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de buscar o registro com base no campo \"apelido\" (surname).").more_info = ex).build();
        }
    }

    @GET
    @Path("/breeds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAvailableBreeds() {
        Log.infof("| BUSCANDO RAÇAS EXISTENTES/VÁLIDAS ATRAVÉS DE UMA REQUISIÇÃO EFETUADA NA API 'DOG CEO'...\nIP QUE REQUISITOU A OPERAÇÃO: %s\n", getClientIP());

        try {
            final ArrayList<String> BREED_ARRAY_RESPONSE = validateBreed.makeBreedArray();
            Log.info("| BUSCA POR RAÇAS VÁLIDAS DA API EXTERNA 'DOG CEO' CONCLUÍDA. RETORNANDO DADOS OBTIDOS PARA O CLIENTE...\n");
            return Response.ok(BREED_ARRAY_RESPONSE).build();

        } catch (Exception ex) {
            Status RESPONSE = new Status("503", "service_unavailable", "Houve um problema na comunicação com a API externa no momento de buscar a lista de raças existentes/válidas.");
            RESPONSE.more_info = ex;
            Log.errorf("| OCORREU UM ERRO INESPERADO ENVOLVENDO A COMUNICAÇÃO COM A API 'DOG CEO' NO MOMENTO DE CONSULTAR RAÇAS VÁLIDAS PARA A APLICAÇÃO.\nCÓDIGO: 503 - SERVICE UNAVAILABLE\n DETALHES - %s\n", ex);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(RESPONSE).build();
        }
    }

    @GET
    @Path("/telemetry")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveToGetAppTelemetry() {
        return Response.ok(telemetry.getTelemetry()).build();
    }

    @DELETE
    @Path("/{id}")
    public Response receiveToDelete(@PathParam("id") UUID id) {
        Log.infof("| REQUISIÇÃO DE EXCLUSÃO RECEBIDA PARA O REGISTRO DE ID: %s | INICIANDO PROCEDIMENTO...\nIP QUE REQUISITOU A OPERAÇÃO: %s\n", id, getClientIP());
        Status RESPONSE;

        try {
            if (dogsService.deleteDogs(id)) {
                RESPONSE = new Status("200", "success", "O registro foi deletado com sucesso.");
                Log.infof("| REGISTRO DE ID %s DELETADO COM SUCESSO.\n", id);
                return Response.ok(RESPONSE).build();
            }

            throw new RecordNotFoundException();

        } catch (RecordNotFoundException exRecordNotFound) {
            RESPONSE = new Status("404", "not_found", "O registro não foi deletado, porque ele não existe no banco de dados.");
            Log.warnf("| NÃO FOI POSSÍVEL DELETAR, PORQUE O REGISTRO DE ID %s NÃO EXISTE NO BANCO DE DADOS.\nCÓDIGO: 404 - NOT FOUND\n", id);
            return Response.status(Response.Status.NOT_FOUND).entity(RESPONSE).build();

        } catch (Exception ex) {
            RESPONSE = new Status("500", "server_error", "Houve um problema na comunicação com o banco de dados no momento de deletar o registro.");
            RESPONSE.more_info = ex;
            Log.errorf("| OCORREU UM ERRO INESPERADO ENVOLVENDO A COMUNICAÇÃO COM O BANCO DE DADOS NO MOMENTO DELETAR O REGISTRO DE ID: %s\nCÓDIGO: 500 - INTERNAL SERVER ERROR\n DETALHES - %s\n", id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RESPONSE).build();
        }
    }
}