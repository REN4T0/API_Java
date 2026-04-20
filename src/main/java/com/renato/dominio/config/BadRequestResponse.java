package com.renato.dominio.config;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.renato.dominio.controller.Status;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class BadRequestResponse {

    @ServerExceptionMapper
    public Response getErrorType(MismatchedInputException exception) {

        // Erro personalizado e incluindo detalhes nativos do erro
        Status errorAnswer = new Status("400", "bad_request", "Os atributos do objeto enviado deve conter exclusivamente dados do tipo String");
        errorAnswer.more_info = exception.getOriginalMessage();

        return Response.status(Response.Status.BAD_REQUEST).entity(errorAnswer).build();
    }
}