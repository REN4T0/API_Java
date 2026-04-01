package com.renato.dominio.application;

import com.renato.dominio.dto.DogsDTO;
import com.renato.dominio.entity.Dogs;
import com.renato.dominio.repositorio.DogsRepository;
import com.renato.dominio.controller.Status;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//@Path("/dogs")
@ApplicationScoped
public class DogsApp {
    @Inject
    MeterRegistry registry;

    @Inject
    private DogsRepository dogsRepository;

//    @POST
    @Transactional
    public Response createDogs(DogsDTO dogDtoObj) { // Se o registro tiver um dado incorreto, devido ao Decorator @Valid, o erro retornado será mais detalhado.
        Status RES;
        long startTime = System.currentTimeMillis();

        Dogs newDog = new Dogs();
        newDog.setBreed(dogDtoObj.getBreed());
        newDog.setSurname(dogDtoObj.getSurname());
        newDog.setGender(dogDtoObj.getGender());

        dogsRepository.persist(newDog);

        // Um HashMap é um tipo de array que permite que eu atribua chaves a cada valor armazenado, ao invés de selecioná-los por índices.
        /* Note que estou dizendo o seguinte:
        *  final - A variável será uma constante;
        *  HashMap<String, String> - Tanto a chave quanto o valor armazenado nela devem ser do tipo String
        *  RES - nome da variávle (em maiúscula, visto ser uma constante)
        *  new HashMap<String, String>() - Estou chamando a construtora do HashMap, para criar um novo HashMap que estou armazenando aqui, além de esclarecer que tanto as chaves quanto os valores serão em strings.*/
//        final HashMap<String, String> NEW_DOG_DATA = new HashMap<String, String>();
//        NEW_DOG_DATA.put("raca", dog.getBreed());
//        NEW_DOG_DATA.put("apelido", dog.getSurname());
//        NEW_DOG_DATA.put("genero", dog.getGender());

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        RES = new Status("200", "success", "Registro cadastrado com sucesso", totalTime, newDog);

        return Response.status(Response.Status.ACCEPTED).entity(RES).build();
    }

//    @GET
//    @Path("/{id}") // Não deve-se colocar ; nos decorators
    public Dogs searchDogsPerId(UUID id) {
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
    public Response deletarDogs(@PathParam("id") UUID id){
        HashMap<String, Object> ID = new HashMap<String, Object>();
        ID.put("sended_id", id);

        Status RES;
        long totalTime;
        long endTime;
        long startTime = System.currentTimeMillis();

        boolean isDeleted = dogsRepository.deleteById(id);

        if(isDeleted){
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;

            RES = new Status ("200", "success", "Registro removido com êxito.", totalTime, ID);
            return Response.status(Response.Status.OK).entity(RES).build();
        }

        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;

        RES = new Status("404", "not_found", "Não foi possível excluir esse registro, porque ele não existe no banco de dados.", totalTime, ID);
        return Response.status(Response.Status.NOT_FOUND).entity(RES).build();
    }

//    @PUT
//    @Path("/{id}")
    @Transactional
    public Response updateDogs(UUID id, DogsDTO dogDtoObj, Dogs dogsExisting) {
        HashMap <String, Object> ID = new HashMap<String, Object>();
        ID.put("sended_id", id);

        Status RES;
        long endTime;
        long totalTime;
        long startTime = System.currentTimeMillis();

//        Dogs dogExist = dogsRepository.findById(id);

//        if (dogExist != null) {
        dogsExisting.setBreed(dogDtoObj.getBreed());
        dogsExisting.setSurname(dogDtoObj.getSurname());
        dogsExisting.setGender(dogDtoObj.getGender());

        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;

        RES = new Status("200", "success", "O registro foi atualizado com êxito no banco de dados", totalTime, ID);

        return Response.status(Response.Status.OK).entity(RES).build();


//        endTime = System.currentTimeMillis();
//        totalTime = endTime - startTime;

//        RES = new Status("404", "not_found", "Não foi possível atualizar, porque o registro não foi encontrado no banco de dados.", totalTime, ID);

//        return Response.status(Response.Status.NOT_FOUND).entity(RES).build();
    }

    // Não é recomendável trazer todos os registros. Já imaginou que o banco tenha milhares de registros
//    @GET
    public List<Dogs> getAllDogs(){
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
