package br.com.seudominio.filmesfavoritos.data.dao

import br.com.seudominio.filmesfavoritos.model.Filme

/**
 * DAO (Data Access Object): define COMO acessar os filmes, sem dizer ONDE ficam guardados.
 * Hoje a implementação guarda em memória; amanhã poderia ser Room, sem mexer no ViewModel.
 */
interface FilmeDao {
    fun listar(): List<Filme>
    fun buscarPorId(id: Long): Filme?

    /** Insere o filme e devolve a versão já com o id gerado. */
    fun inserir(filme: Filme): Filme

    fun atualizar(filme: Filme)
    fun remover(id: Long)
}
