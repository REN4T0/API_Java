package com.renato.dominio.client;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ValidDogBreedsList {
    @RestClient
    DogBreedsAPI DogBreedsAPI;

    public ArrayList<String> makeBreedArray() {
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

        return breedList;
    }
}
