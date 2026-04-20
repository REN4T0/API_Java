package com.renato.dominio.telemetry;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class BackendTelemetry {
    @Inject
    MeterRegistry registry;

    public Map<String, Object> getTelemetry() {
        // 2. Criando o "pacote" que vai virar o nosso JSON
        Map<String, Object> metricas = new HashMap<>();

        // 3. Lendo a Memória RAM da Máquina Virtual Java
        Gauge memoriaUsada = registry.find("jvm.memory.used").gauge();
        if (memoriaUsada != null) {
            // A memória vem em Bytes. Dividimos para converter para Megabytes.
            double megabytes = memoriaUsada.value() / (1024 * 1024);
            metricas.put("memoria_usada_mb", String.format("%.2f", megabytes));
        }

        // 4. Lendo o tempo em que a API está ligada (Uptime)
        Gauge tempoAtivo = registry.find("process.uptime").gauge();
        if (tempoAtivo != null) {
            metricas.put("tempo_online_segundos", tempoAtivo.value());
        }

        // 5. Lendo o tráfego da API (Requisições HTTP)
        Timer requisicoesHttp = registry.find("http.server.requests").timer();
        if (requisicoesHttp != null) {
            metricas.put("total_requisicoes_recebidas", requisicoesHttp.count());
            metricas.put("tempo_total_processamento_ms", requisicoesHttp.totalTime(TimeUnit.MILLISECONDS));

        } else {
            // Se ninguém acessou a API ainda, essa métrica não existe. Tratamos isso para não dar erro.
            metricas.put("aviso_trafego", "Faça pelo menos uma requisição (GET ou POST) para gerar dados de tráfego.");
        }

        // O Quarkus converte esse Map automaticamente para um formato JSON perfeito!
        return metricas;
    }
}
