package com.renato.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DogsDTO {
    // Decorator que valida se o atributo está vazio.
    @NotBlank(message = "A raça não deve conter valor nulo.")
    private String breed;

    @NotBlank(message = "O apelido não deve conter valor nulo.")
    @Size(min = 3, max = 30, message = "O apelido deve ter entre 3 e 30 caracteres.")// Decorator que define qual o mínimo e máximo de caracteres a ser armazenados no atributo
    private String surname;

    @NotBlank(message = "O gênero não deve conter valor nulo.")
    @Size(min = 1, max = 1, message = "O gênero deve conter apenas 1 caractere.")
    @Pattern(regexp = "^[MF]$", message = "Os valores aceitos no campo 'Gênero' (gender) são apenas 'M' para macho e 'F' para fêmea.")// Definindo, através do Regex, que esse atributo só receberá dois valores diferentes: "M" e "F".
    private String gender;

    public String getBreed(){
        return this.breed;
    }
    public String getSurname(){
        return this.surname;
    }
    public String getGender(){
        return this.gender;
    }
}
