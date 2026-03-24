package com.renato.dominio.aplicacao;

import com.renato.dominio.entidade.Dogs;
import com.renato.dominio.repositorio.DogsRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/dogs")
public class DogsResource {
    @Inject
    private DogsRepository dogsRepository;

    @POST
    @Transactional
    public Dogs criarDogs(@Valid Dogs dog) { // Se o registro tiver um dado incorreto, devido ao Decorator @Valid, o erro retornado será mais detalhado.
        // System.out.println("Raça:" + dog.getBreed());
        dogsRepository.persist(dog);
        return dog;
    }

    @GET
    @Path("/{id}") // Não deve-se colocar ; nos decorators
    public Dogs buscarDogsPorId(@PathParam("id") UUID id) {
        return dogsRepository.findById(id);
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

//    Não é recomendável trazer todos os registros. Já imaginou que o banco tenha milhares de registros?
//    public List<Dogs> listarDogs(){
//        return DogsRepository.listAll();
//    }
    /* Para não puxar os zilhões de registros do banco de dados, sem travar o servidor,
    *  podemos puxar os dados por páginas, definindo quantos registros haverá em cada página,
    *  conforme o trecho abaixo. */
    @GET
    public List<Dogs> listarDogs(
            @QueryParam("page") @DefaultValue("0") int page, // O número da página pode ser enviada pela URL (query). Caso não venha nada, haverá um valor padrão setado (no caso 0; página 0).
            @QueryParam("size") @DefaultValue("10") int size) { // Da mesma forma, a quantidade de cada página pode ser enviada pela URL. Caso não seja, tem um valor padrão setado.
        return dogsRepository.findAll().page(page, size).list(); // Retornandos os dados com base nos parâmetros do método.
    }



}
