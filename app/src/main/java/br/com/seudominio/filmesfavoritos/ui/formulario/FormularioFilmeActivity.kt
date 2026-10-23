package br.com.seudominio.filmesfavoritos.ui.formulario

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import br.com.seudominio.filmesfavoritos.R
import br.com.seudominio.filmesfavoritos.databinding.ActivityFormularioFilmeBinding
import br.com.seudominio.filmesfavoritos.util.Constantes
import br.com.seudominio.filmesfavoritos.util.Formatadores
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Tela de formulário (criar e editar). A Activity só desenha o estado do ViewModel
 * e repassa os eventos do usuário; a lógica (validação, salvar, excluir) está no ViewModel.
 */
class FormularioFilmeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioFilmeBinding
    private val viewModel: FormularioFilmeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioFilmeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        // Heurística 3 (controle e liberdade): seta de voltar para sair sem salvar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Sem extra no Intent (clique no botão +) usamos ID_NOVO_FILME = modo criação
        val filmeId = intent.getLongExtra(Constantes.EXTRA_FILME_ID, Constantes.ID_NOVO_FILME)
        viewModel.carregar(filmeId)

        atualizarTextoNota(binding.ratingBarNota.rating)
        configurarEventos()
        observarEstado()
    }

    private fun configurarEventos() {
        // Atualiza o número ao lado das estrelas. É só exibição, por isso fica na Activity.
        binding.ratingBarNota.setOnRatingBarChangeListener { _, nota, _ ->
            atualizarTextoNota(nota)
        }

        // Ao digitar, o erro daquele campo some
        binding.editTextNome.doAfterTextChanged { viewModel.aoDigitarNome() }
        binding.editTextUrl.doAfterTextChanged { viewModel.aoDigitarUrl() }

        // O preview só é atualizado quando o campo perde o foco. Se atualizasse a cada letra,
        // o Coil tentaria baixar URLs incompletas enquanto o usuário ainda digita.
        binding.editTextUrl.setOnFocusChangeListener { _, temFoco ->
            if (!temFoco) {
                viewModel.atualizarPreview(binding.editTextUrl.text.toString())
            }
        }

        // Ao definir um listener próprio no ícone final, perdemos o comportamento padrão de
        // "limpar texto". Então limpamos manualmente e avisamos o ViewModel.
        binding.textInputLayoutUrl.setEndIconOnClickListener {
            binding.editTextUrl.text?.clear()
            viewModel.atualizarPreview("")
        }

        binding.buttonSalvar.setOnClickListener {
            viewModel.salvar(
                nome = binding.editTextNome.text.toString(),
                nota = binding.ratingBarNota.rating,
                url = binding.editTextUrl.text.toString()
            )
        }
    }

    private fun observarEstado() {
        // Modo edição: preenche os campos. Só preenche se o campo de nome ainda estiver vazio,
        // para não sobrescrever o que o usuário digitou (ex.: depois de girar a tela).
        viewModel.filme.observe(this) { filme ->
            if (filme != null && binding.editTextNome.text.isNullOrEmpty()) {
                binding.editTextNome.setText(filme.nome)
                binding.ratingBarNota.rating = filme.nota
                binding.editTextUrl.setText(filme.fotoUrl.orEmpty())
            }
        }

        // Heurística 1 (visibilidade do estado): o título diz se é "Novo" ou "Editar"
        viewModel.modoEdicao.observe(this) { emEdicao ->
            supportActionBar?.title = getString(
                if (emEdicao) R.string.titulo_editar_filme else R.string.titulo_novo_filme
            )
            invalidateOptionsMenu() // reavalia a visibilidade do botão excluir
        }

        viewModel.urlPreview.observe(this) { url ->
            binding.imageViewPreview.load(url) {
                placeholder(R.drawable.ic_placeholder_filme)
                error(R.drawable.ic_erro_imagem)
                fallback(R.drawable.ic_placeholder_filme)
                crossfade(true)
            }
        }

        // O ViewModel entrega o id da string de erro; aqui traduzimos para texto
        viewModel.erroNome.observe(this) { erro ->
            binding.textInputLayoutNome.error = erro?.let { getString(it) }
        }

        viewModel.erroUrl.observe(this) { erro ->
            binding.textInputLayoutUrl.error = erro?.let { getString(it) }
        }

        viewModel.fecharTela.observe(this) { fechar ->
            if (fechar) {
                viewModel.mensagemFinal.value?.let { mensagem ->
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun atualizarTextoNota(nota: Float) {
        binding.textViewValorNota.text = Formatadores.formatarNota(nota)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_formulario, menu)
        return true
    }

    // O botão excluir só aparece no modo edição (no modo criação não há o que excluir)
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_excluir)?.isVisible = viewModel.modoEdicao.value == true
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_excluir -> {
                confirmarExclusao()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // Heurística 5 (prevenção de erros): pede confirmação antes de uma ação sem volta
    private fun confirmarExclusao() {
        val nome = viewModel.filme.value?.nome.orEmpty()
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialogo_excluir_titulo)
            .setMessage(getString(R.string.dialogo_excluir_mensagem, nome))
            .setNegativeButton(R.string.dialogo_cancelar, null)
            .setPositiveButton(R.string.dialogo_excluir_confirmar) { _, _ -> viewModel.excluir() }
            .show()
    }
}
