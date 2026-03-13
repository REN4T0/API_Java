package com.renato.dominio.entidade;

// Importando pacotes Java para utilizar Decorators (Jakarta)
/* Podemos definir decorator como códigos acrescentados a determinada parte do meu
   código, adicionando uma nova funcionalidade a esse determinado trecho do meu código,
   otimizando a programação. Estou aproveitando algorítimos já existentes para aumentar
   minha eficiência. */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID; // O que é UUID? **Pesquisar**

// A entidade será a classe que eu criei agora
@Entity
public class Pessoa {
    // Definindo os atributos da minha classe
    /* Meu primeiro atributo receberá um decorator chamado Id, que, em 'background'
    'rodará' uma série de algorítimo que definião esse primeiro atributo como um Id,
    principalmente quando for armazenado no banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gerando um valor aleatório para o Id
    private UUID id;

    // Decorator que define qual o mínimo e máximo de caracteres a ser armazenados no atributo nome
    @Size(min = 3, max = 100)
    @NotBlank // O valor não pode ser nulo
    private String nome;

    @Email // Decorator que valida se o E-mail informado é valido.
    @NotBlank
    private String email;

    @Past // Só permite a entrada de datas do passado.
    private LocalDate nascimento;

    /* Como todas as variáveis receberam o modificador "private", os atributos só
    * podem ser alterados diretamente dentro da classe a qual pertencem. Para que
    * outras classes alterem esse atributos, precisaremos de getters e setters. */

    //Getters
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    //Setters
    /* O id não terá um setter, afinal, seu valor será setado direto no banco e esse
     *  recurso também não pode ser alterado. */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

}
