package com.renato.dominio.aplicacao;

import com.renato.dominio.entidade.Dogs;
import com.renato.dominio.repositorio.DogsRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Path("/dogs")
public class DogsResource {
    @Inject
    MeterRegistry registry;

    @Inject
    private DogsRepository dogsRepository;

    @POST
    @Transactional
    public Dogs criarDogs(@Valid Dogs dog) { // Se o registro tiver um dado incorreto, devido ao Decorator @Valid, o erro retornado será mais detalhado.
        dogsRepository.persist(dog);
        return dog;
    }

    @GET
    @Path("/{id}") // Não deve-se colocar ; nos decorators
    public Dogs buscarDogsPorId(@PathParam("id") UUID id) {
        return dogsRepository.findById(id);
    }

    // Buscando cachorro por apelido
    @GET
    @Path("/apelido/{surname}")
    public List<Dogs> buscarDogsPorApelido(@PathParam("surname") String surname){
        return dogsRepository.find("surname like ?1", "%" + surname + "%").list(); // Vai retornar todos os cachorros que tenham nomes inicialmente semelhantes, não sendo necessário escrever o nome completo do cachorro.
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void deletarDogs(@PathParam("id") UUID id){
        dogsRepository.deleteById(id);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Dogs atualizarDogs(@PathParam("id") UUID id, Dogs dogs) {
        Dogs dogsExistente = dogsRepository.findById(id);

        if (dogsExistente != null) {
            dogsExistente.setBreed(dogs.getBreed());
            dogsExistente.setSurname(dogs.getSurname());
            dogsExistente.setGender(dogs.getGender());
            // dogsRepository.persist(dogsExistente);
            return dogsExistente;
        }

        return null;
    }

    // Não é recomendável trazer todos os registros. Já imaginou que o banco tenha milhares de registros
    @GET
    public List<Dogs> listarDogs(){
       return dogsRepository.listAll();
    }

    /* Para não puxar os zilhões de registros do banco de dados, sem travar o servidor,
    *  podemos puxar os dados por páginas, definindo quantos registros haverá em cada página,
    *  conforme o trecho abaixo. */
//    @GET
//    public List<Dogs> listarDogs(
//            @QueryParam("page") @DefaultValue("0") int page, // O número da página pode ser enviada pela URL (query). Caso não venha nada, haverá um valor padrão setado (no caso 0; página 0).
//            @QueryParam("size") @DefaultValue("10") int size) { // Da mesma forma, a quantidade de cada página pode ser enviada pela URL. Caso não seja, tem um valor padrão setado.
//        return dogsRepository.findAll().page(page, size).list(); // Retornandos os dados com base nos parâmetros do método.
//    }

    @GET
    @Path("/telemetria")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> obterTelemetria() {
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
