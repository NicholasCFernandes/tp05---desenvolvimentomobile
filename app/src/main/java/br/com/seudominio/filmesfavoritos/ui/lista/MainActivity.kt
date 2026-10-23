package br.com.seudominio.filmesfavoritos.ui.lista

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.seudominio.filmesfavoritos.R
import br.com.seudominio.filmesfavoritos.databinding.ActivityMainBinding
import br.com.seudominio.filmesfavoritos.ui.formulario.FormularioFilmeActivity
import br.com.seudominio.filmesfavoritos.util.Constantes

/**
 * Tela da lista. No MVVM, a Activity só desenha o estado e repassa os eventos:
 * não tem regra de negócio nem acessa o DAO ou o Repository.
 */
class MainActivity : AppCompatActivity() {

    // View Binding: classe gerada a partir do activity_main.xml (substitui o findViewById)
    private lateinit var binding: ActivityMainBinding

    // by viewModels() cria o ViewModel e o mantém vivo mesmo ao girar a tela
    private val viewModel: ListaFilmesViewModel by viewModels()

    private val adapter = FilmeAdapter { filme -> abrirFormulario(filme.id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.titulo_lista)

        // LayoutManager define a disposição dos itens: aqui, lista vertical
        binding.recyclerViewFilmes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFilmes.adapter = adapter

        binding.fabAdicionar.setOnClickListener { abrirFormulario() }

        viewModel.filmes.observe(this) { filmes -> adapter.submitList(filmes) }

        viewModel.listaVazia.observe(this) { vazia ->
            binding.layoutVazio.visibility = if (vazia) View.VISIBLE else View.GONE
            binding.recyclerViewFilmes.visibility = if (vazia) View.GONE else View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        // Cada Activity tem o seu próprio ViewModel, e a fonte da verdade compartilhada é o DAO.
        // Por isso recarregamos a lista sempre que a tela volta a aparecer (ex.: ao voltar do formulário).
        viewModel.carregarFilmes()
    }

    /** Sem id = modo criação. Com id = modo edição daquele filme. */
    private fun abrirFormulario(filmeId: Long? = null) {
        val intent = Intent(this, FormularioFilmeActivity::class.java)
        if (filmeId != null) {
            intent.putExtra(Constantes.EXTRA_FILME_ID, filmeId)
        }
        startActivity(intent)
    }
}
