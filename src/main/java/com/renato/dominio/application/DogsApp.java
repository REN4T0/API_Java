package com.renato.dominio.application;

import com.renato.dominio.dto.DogsDTO;
import com.renato.dominio.entity.Dogs;
import com.renato.dominio.exceptions.RecordAlreadyExistsException;
import com.renato.dominio.exceptions.RecordNotFoundException;
import com.renato.dominio.repository.DogsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DogsApp {

    @Inject
    private DogsRepository dogsRepository;

    private long dogRegisterExist(DogsDTO dogRegister) {
        return dogsRepository.count(
                "breed = ?1 and surname = ?2 and gender = ?3",
                dogRegister.getBreed(), dogRegister.getSurname(), dogRegister.getGender()
        );
    }

    private long dogRegisterExistForUpdate(DogsDTO dogRegister, UUID id) {
        return dogsRepository.count(
                "breed = ?1 and surname = ?2 and gender = ?3 and id != ?4",
                dogRegister.getBreed(), dogRegister.getSurname(), dogRegister.getGender(), id
        );
    }

    @Transactional
    public HashMap<String, Object> createDogs(DogsDTO dogDtoObj) throws RecordAlreadyExistsException { // Se o registro tiver um dado incorreto, devido ao Decorator @Valid, o erro retornado será mais detalhado.
        // Um HashMap é um tipo de array que permite que eu atribua chaves a cada valor armazenado, ao invés de selecioná-los por índices.
        /* Note que estou dizendo o seguinte:
         * HashMap<String, String> - Tanto a chave quanto o valor armazenado nela devem ser do tipo String
         * RES - nome da variável (em maiúscula, visto ser uma constante)
         * new HashMap<String, String>() - Estou chamando a construtora do HashMap, para criar um novo HashMap que estou armazenando aqui, além de esclarecer que tanto as chaves quanto os valores serão em strings.*/

        if (this.dogRegisterExist(dogDtoObj) > 0) {
            throw new RecordAlreadyExistsException(); //ordAlreadyExists IllegalArgumentException("409");
        }

        HashMap<String, Object> RESPONSE = new HashMap<String, Object>();
        long startTime = System.currentTimeMillis();

        Dogs newDog = new Dogs();
        newDog.setBreed(dogDtoObj.getBreed());
        newDog.setSurname(dogDtoObj.getSurname());
        newDog.setGender(dogDtoObj.getGender());
        dogsRepository.persist(newDog);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        RESPONSE.put("total_time", totalTime);
        return RESPONSE;
    }

//    public Dogs searchDogsPerId(UUID id) {
//        return dogsRepository.findById(id);
//    }

    // Buscando cachorro por apelido
    public List<Dogs> searchDogsPerSurname(String surname) {
        return dogsRepository.find("surname like ?1", "%" + surname + "%").list(); // Vai retornar todos os cachorros que tenham nomes inicialmente semelhantes, não sendo necessário escrever o nome completo do cachorro.
    }

    @Transactional
    public Boolean deleteDogs(UUID id) {
        return dogsRepository.deleteById(id);
    }

    @Transactional
    public HashMap<String, Object> updateDogs(UUID id, DogsDTO dogDtoObj) throws RecordNotFoundException, RecordAlreadyExistsException {
        HashMap<String, Object> RESPONSE = new HashMap<String, Object>();
        long endTime;
        long totalTime;

        Dogs dogExist = dogsRepository.findById(id);

        if (dogExist == null) {
            throw new RecordNotFoundException();
        }

        if (this.dogRegisterExistForUpdate(dogDtoObj, id) > 0) {
            throw new RecordAlreadyExistsException();
        }

        long startTime = System.currentTimeMillis();

        dogExist.setBreed(dogDtoObj.getBreed());
        dogExist.setSurname(dogDtoObj.getSurname());
        dogExist.setGender(dogDtoObj.getGender());

        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;

        RESPONSE.put("sended_id", id);
        RESPONSE.put("total_time", totalTime);
        return RESPONSE;
    }

    // Não é recomendável trazer todos os registros. Já imaginou que o banco tenha milhares de registros
    public List<Dogs> getAllDogs() {
        return dogsRepository.listAll();
    }

    /* Para não puxar os zilhões de registros do banco de dados, sem travar o servidor,
     * podemos puxar os dados por páginas, definindo quantos registros haverá em cada página,
     * conforme o trecho abaixo.
    @GET
    public List<Dogs> listarDogs(
            @QueryParam("page") @DefaultValue("0") int page, // O número da página pode ser enviada pela URL (query). Caso não venha nada, haverá um valor padrão setado (no caso 0; página 0).
            @QueryParam("size") @DefaultValue("10") int size) { // Da mesma forma, a quantidade de cada página pode ser enviada pela URL. Caso não seja, tem um valor padrão setado.
       return dogsRepository.findAll().page(page, size).list(); // Retornandos os dados com base nos parâmetros do método.
    } */
}