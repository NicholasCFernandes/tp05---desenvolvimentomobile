package br.com.seudominio.filmesfavoritos.data.repository

import br.com.seudominio.filmesfavoritos.data.dao.FilmeDao
import br.com.seudominio.filmesfavoritos.data.dao.FilmeDaoImpl
import br.com.seudominio.filmesfavoritos.model.Filme

/**
 * Camada entre o ViewModel e o DAO. Hoje só repassa as chamadas, mas é AQUI que entraria
 * uma API remota (cache + rede) ou o Room, sem que o ViewModel precise saber disso.
 */
class FilmeRepository(private val dao: FilmeDao = FilmeDaoImpl()) {

    fun listar(): List<Filme> = dao.listar()

    fun buscarPorId(id: Long): Filme? = dao.buscarPorId(id)

    fun inserir(filme: Filme): Filme = dao.inserir(filme)

    fun atualizar(filme: Filme) = dao.atualizar(filme)

    fun remover(id: Long) = dao.remover(id)
}
