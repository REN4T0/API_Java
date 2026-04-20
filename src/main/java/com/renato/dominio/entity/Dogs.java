package com.renato.dominio.entity;

// Importando pacotes Java para utilizar Decorators (Jakarta)
/* Podemos definir decorator como códigos acrescentados a determinada parte do meu
   código, adicionando uma nova funcionalidade a esse determinado trecho do meu código,
   otimizando a programação. Estou aproveitando algorítimos já existentes para aumentar
   minha eficiência. */
import jakarta.persistence.*;
import java.util.UUID; // O que é UUID? **Pesquisar**

// A entidade será a classe que eu criei agora
@Entity
@Table(name = "dogs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"breed", "surname", "gender"}) // Definindo que cada registro é único e não pode se repetir
})
public class Dogs {
    // Definindo os atributos da minha classe
    /* Meu primeiro atributo receberá um decorator chamado Id, que, em 'background'
    'rodará' uma série de algorítimo que definirão esse primeiro atributo como um Id,
    principalmente quando for armazenado no banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gerando um valor aleatório para o Id
    private UUID id;

    @Column(name = "breed")
    private String breed;

    @Column(name = "surname")
    private String surname;

    @Column(name = "gender")
    private String gender;

    /* Como todas as variáveis receberam o modificador "private", os atributos só
    * podem ser alterados diretamente dentro da classe a qual pertencem. Para que
    * outras classes alterem esse atributos, precisaremos de getters e setters. */

    //Getters
    public UUID getId() {
        return id;
    }

    public String getBreed() {
        return breed;
    }

    public String getSurname() {
        return surname;
    }

    public String getGender() {
        return gender;
    }

    //Setters
    /* O id não terá um setter, afinal, seu valor será setado direto no banco e esse
     *  recurso também não pode ser alterado. */
    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
