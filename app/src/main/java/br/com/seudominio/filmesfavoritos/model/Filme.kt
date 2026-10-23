package br.com.seudominio.filmesfavoritos.model

/**
 * Um filme favorito do usuário.
 *
 * Todos os campos são `val` (imutáveis): para editar, criamos uma cópia com `copy()`.
 * Isso ajuda o DiffUtil do RecyclerView, que compara o objeto antigo com o novo para
 * saber o que mudou. Se alterássemos o mesmo objeto "por dentro", o antigo e o novo
 * seriam iguais e a lista não perceberia a mudança.
 */
data class Filme(
    val id: Long = 0L,
    val nome: String,
    val nota: Float, // de 0.0 a 5.0, em passos de 0.5
    val fotoUrl: String? = null // URL http(s) da foto, ou null quando não há foto
)
