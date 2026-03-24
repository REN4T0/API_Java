package com.renato.dominio.repositorio;

import com.renato.dominio.entidade.Dogs;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Singleton;

import java.util.UUID;

// Uma classe para o repositório de pessoas
// ??? Entender necessidade
// Usará um "escopo" (???) chamado "Singleton" (que também é o nome de um padrão de projeto).
// ***Pesquisar""" - Padrão de projeto Singleton; Singleton
@Singleton
public class DogsRepository implements PanacheRepositoryBase<Dogs, UUID> { //Implements, ou seja, implementar. Fica subtendido que essa classe implementrá recursos adicionais a minha classe Pessoa. Resta descobrir o que, por quê e como. (Pelo visto, fará algo relacionado ao id de cada usuário)

}
