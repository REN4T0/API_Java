package com.renato.dominio.aplicacao;

import com.renato.dominio.entidade.Pessoa;
import com.renato.dominio.repositorio.RepositorioPessoas;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;

import java.util.List;
import java.util.UUID;

@Path("/pessoas")
public class PessoasResource {
    @Inject
    private RepositorioPessoas repositorioPessoas;

    @POST
    @Transactional
    public Pessoa criarPessoa(@Valid Pessoa pessoa) { // Se o registro tiver um dado incorreto, devido ao Decorator @Valid, o erro retornado será mais detalhado.
        repositorioPessoas.persist(pessoa);
        return pessoa;
    }

    @GET
    @Path("/{id}") // Não deve-se colocar ; nos decorators
    public Pessoa buscarPessoaPorId(@PathParam("id") UUID id) {
        return repositorioPessoas.findById(id);
    }

    @DELETE
    @Path("/{id}")
    //@Transactional
    public void deletarPessoa(UUID id){
        repositorioPessoas.deleteById(id);
    }

    @PUT
    @Path("/{id}")
    public Pessoa atualizarPessoa(@PathParam("id") UUID id, Pessoa pessoa) {
        Pessoa pessoaExistente = repositorioPessoas.findById(id);

        if (pessoaExistente != null) {
            pessoaExistente.setNome(pessoa.getNome());
            pessoaExistente.setNome(pessoa.getEmail());
            pessoaExistente.setNascimento(pessoa.getNascimento());
            repositorioPessoas.persist(pessoaExistente);
            return pessoaExistente;
        }

        return null;
    }

//    Não é recomendável trazer todos os registros. Já imaginou que o banco tenha milhares de registros?
//    public List<Pessoa> listarPessoa(){
//        return repositorioPessoas.listAll();
//    }
    /* Para não puxar os zilhões de registros do banco de dados, sem travar o servidor,
    *  podemos puxar os dados por páginas, definindo quantos registros haverá em cada página,
    *  conforme o trecho abaixo. */
    @GET
    public List<Pessoa> listarPessoas(
            @QueryParam("page") @DefaultValue("0") int page, // O número da página pode ser enviada pela URL (query). Caso não venha nada, haverá um valor padrão setado (no caso 0; página 0).
            @QueryParam("size") @DefaultValue("10") int size) { // Da mesma forma, a quantidade de cada página pode ser enviada pela URL. Caso não seja, tem um valor padrão setado.
        return repositorioPessoas.findAll().page(page, size).list(); // Retornandos os dados com base nos parâmetros do método.
    }



}
