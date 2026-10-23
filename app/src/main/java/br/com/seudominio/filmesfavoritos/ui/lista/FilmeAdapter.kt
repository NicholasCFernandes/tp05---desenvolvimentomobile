package br.com.seudominio.filmesfavoritos.ui.lista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.seudominio.filmesfavoritos.R
import br.com.seudominio.filmesfavoritos.databinding.ItemFilmeBinding
import br.com.seudominio.filmesfavoritos.model.Filme
import br.com.seudominio.filmesfavoritos.util.Formatadores
import coil.load

/**
 * Adapter da lista. É uma evolução do RecyclerView.Adapter visto na aula (UsuarioAdapter):
 * o ListAdapter compara a lista antiga com a nova (via DiffUtil) e anima só o que mudou,
 * usando o DefaultItemAnimator. Por isso não precisamos escrever getItemCount() nem guardar
 * a lista: o próprio ListAdapter faz isso (basta chamar submitList).
 */
class FilmeAdapter(
    private val aoClicar: (Filme) -> Unit
) : ListAdapter<Filme, FilmeAdapter.ViewHolder>(DIFERENCA) {

    /**
     * O ViewHolder fica DENTRO do Adapter por encapsulamento e organização, como na aula:
     * ele é específico deste Adapter e só faz sentido existir neste contexto.
     * Guarda o binding (as referências das views) para reaproveitar o item ao rolar a lista,
     * sem inflar o layout de novo.
     */
    class ViewHolder(private val binding: ItemFilmeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun vincular(filme: Filme) {
            val contexto = binding.root.context
            val notaFormatada = Formatadores.formatarNota(filme.nota)

            binding.textViewNomeFilme.text = filme.nome
            binding.ratingBarNotaFilme.rating = filme.nota
            binding.textViewNotaFilme.text = contexto.getString(R.string.formato_nota, notaFormatada)

            // Coil baixa a imagem em segundo plano e cancela o pedido se o item for reciclado.
            binding.imageViewFotoFilme.load(filme.fotoUrl) {
                placeholder(R.drawable.ic_placeholder_filme) // enquanto carrega
                error(R.drawable.ic_erro_imagem) // se a URL falhar
                fallback(R.drawable.ic_placeholder_filme) // se o filme não tiver foto (url nula)
                crossfade(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Infla o layout item_filme.xml uma vez por ViewHolder criado (não por item exibido)
        val binding = ItemFilmeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filme = getItem(position)
        holder.vincular(filme)
        holder.itemView.setOnClickListener { aoClicar(filme) }
    }

    private companion object {
        // Mesmo item = mesmo id. Mesmo conteúdo = data class igual (equals gerado pelo Kotlin).
        val DIFERENCA = object : DiffUtil.ItemCallback<Filme>() {
            override fun areItemsTheSame(antigo: Filme, novo: Filme): Boolean = antigo.id == novo.id
            override fun areContentsTheSame(antigo: Filme, novo: Filme): Boolean = antigo == novo
        }
    }
}
