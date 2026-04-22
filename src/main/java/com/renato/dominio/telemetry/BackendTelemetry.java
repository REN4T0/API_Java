package com.renato.dominio.telemetry;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class BackendTelemetry {

    @Inject
    MeterRegistry registry;

    public Map<String, Object> getTelemetry() {
        Map<String, Object> metricas = new HashMap<>();

        // 1. Memória
        Gauge memoriaUsada = registry.find("jvm.memory.used").gauge();
        if (memoriaUsada != null) {
            double megabytes = memoriaUsada.value() / (1024 * 1024);
            metricas.put("memoria_usada_mb", String.format("%.2f", megabytes));
        }

        // 2. Uptime
        Gauge tempoAtivo = registry.find("process.uptime").gauge();
        if (tempoAtivo != null) {
            metricas.put("tempo_online_segundos", tempoAtivo.value());
        }

        // 3. O SEGREDO ESTÁ AQUI: Pegando a COLEÇÃO de cronômetros
        Collection<Timer> todosOsTimersHttp = registry.find("http.server.requests").timers();

        if (todosOsTimersHttp != null && !todosOsTimersHttp.isEmpty()) {
            long totalRequisicoes = 0;
            double tempoTotalProcessamentoMs = 0;

            // Somamos as requisições de TODAS as rotas (POST, GET, PUT, DELETE)
            for (Timer timerDaRota : todosOsTimersHttp) {
                totalRequisicoes += timerDaRota.count();
                tempoTotalProcessamentoMs += timerDaRota.totalTime(TimeUnit.MILLISECONDS);
            }

            metricas.put("total_requisicoes_recebidas", totalRequisicoes);
            metricas.put("tempo_total_processamento_ms", tempoTotalProcessamentoMs);

        } else {
            metricas.put("aviso_trafego", "Faça pelo menos uma requisição para gerar dados de tráfego.");
        }

        return metricas;
    }
}