package br.com.seudominio.filmesfavoritos.data.dao

import br.com.seudominio.filmesfavoritos.model.Filme

/**
 * Implementação em memória do DAO, no mesmo padrão do UsuarioDaoImpl e do LivroDaoImpl da aula.
 *
 * A lista fica no `companion object` para ser compartilhada: a MainActivity e a
 * FormularioFilmeActivity criam instâncias diferentes do DAO, mas enxergam os mesmos dados.
 */
class FilmeDaoImpl : FilmeDao {

    companion object {
        private val filmes = mutableListOf<Filme>()
        private var proximoId = 1L

        // O bloco init do companion roda uma única vez, quando a classe é carregada.
        // Serve para popular 3 filmes de exemplo (as fotos são imagens aleatórias do picsum).
        init {
            filmes.add(
                Filme(
                    id = proximoId++,
                    nome = "Interestelar",
                    nota = 5.0f,
                    fotoUrl = "https://picsum.photos/seed/interestelar/300/450"
                )
            )
            filmes.add(
                Filme(
                    id = proximoId++,
                    nome = "O Poderoso Chefão",
                    nota = 4.5f,
                    fotoUrl = "https://picsum.photos/seed/poderoso/300/450"
                )
            )
            filmes.add(
                Filme(
                    id = proximoId++,
                    nome = "Matrix",
                    nota = 4.0f,
                    fotoUrl = "https://picsum.photos/seed/matrix/300/450"
                )
            )
        }
    }

    // Devolvemos uma CÓPIA (toList). Se devolvêssemos a própria lista mutável, o DiffUtil
    // receberia sempre a mesma referência (e não perceberia mudanças) e qualquer classe
    // de fora poderia alterar os dados sem passar pelo DAO.
    override fun listar(): List<Filme> {
        return filmes.toList()
    }

    override fun buscarPorId(id: Long): Filme? {
        return filmes.find { it.id == id }
    }

    override fun inserir(filme: Filme): Filme {
        val filmeComId = filme.copy(id = proximoId++)
        filmes.add(filmeComId)
        return filmeComId
    }

    override fun atualizar(filme: Filme) {
        val indice = filmes.indexOfFirst { it.id == filme.id }
        if (indice >= 0) {
            filmes[indice] = filme
        }
    }

    override fun remover(id: Long) {
        filmes.removeAll { it.id == id }
    }
}
