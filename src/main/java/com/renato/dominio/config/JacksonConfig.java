package com.renato.dominio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {

        // Configurando a regra rígida para campos que esperam Texto (String)
        mapper.coercionConfigFor(LogicalType.Textual)
                // Se chegar um número inteiro (ex: 400), FALHE.
                .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                // Se chegar um número decimal (ex: 400.5), FALHE.
                .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                // Se chegar um booleano (ex: true/false), FALHE.
                .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);
    }
}