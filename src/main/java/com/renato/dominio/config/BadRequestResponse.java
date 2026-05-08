package com.renato.dominio.config;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.renato.dominio.controller.Status;
import io.quarkus.logging.Log;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.stream.Collectors;

public class BadRequestResponse {

    @ServerExceptionMapper
    public Response getMismatchedInputError(MismatchedInputException exMismatchedInput) {
        Log.warnf("| O TIPO DO(S) VALOR(ES) É INVALIDO. OS ATRIBUTOS DEVEM SER EXCLUSIVAMENTE DO TIPO STRING.\nCÓDIGO: 400 - BAD REQUEST\nDETALHES: %s\n", exMismatchedInput);

        // Erro personalizado e incluindo detalhes nativos do erro
        Status errorAnswer = new Status("400", "bad_request", "Os atributos do objeto enviado deve conter exclusivamente dados do tipo String");
        errorAnswer.more_info = exMismatchedInput.getOriginalMessage();

        return Response.status(Response.Status.BAD_REQUEST).entity(errorAnswer).build();
    }

    @ServerExceptionMapper
    public Response getConstraintViolationError(ConstraintViolationException exConstraintViolation) {
        String errorMsg = exConstraintViolation.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" | "));
        Log.warnf("| A REQUISIÇÃO FOI REJEITADA PELOS DECORATORS. ALGUM DADO INVÁLIDO FOI ENVIADO.\nCÓDIGO: 400 - BAD REQUEST\nDETALHES: %s\n", errorMsg);
        Status errorAnswer = new Status("400", "bad_request", errorMsg);

        return Response.status(Response.Status.BAD_REQUEST).entity(errorAnswer).build();
    }
}