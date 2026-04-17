package com.renato.dominio.validator;

import com.renato.dominio.client.DogBreedsAPI;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DogBreedValidator {
    @RestClient
    DogBreedsAPI DogBreedsAPI;



//    public Map <String, Object> getExistingBreeds() {
//        return DogBreedsAPI.searchExistingBreeds();
//    }

    public boolean validateBreed(String breed) {
        ArrayList<String> breedList = new ArrayList<>();

        Map<String, List<String>> existingBreedsResponse = (Map<String, List<String>>) DogBreedsAPI.searchExistingBreeds().get("message");

        for(String key : existingBreedsResponse.keySet()){
            breedList.add(key);

            if(!existingBreedsResponse.get(key).isEmpty()){
                for(String index : existingBreedsResponse.get(key)){
                    breedList.add(key + " " + index);
                }
            }
        }

        boolean exists = breedList.contains(breed);
        System.out.println(exists);
    }
}
