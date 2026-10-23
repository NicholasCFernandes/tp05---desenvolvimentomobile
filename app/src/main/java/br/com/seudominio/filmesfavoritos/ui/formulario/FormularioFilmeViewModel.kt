package br.com.seudominio.filmesfavoritos.ui.formulario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.seudominio.filmesfavoritos.R
import br.com.seudominio.filmesfavoritos.data.repository.FilmeRepository
import br.com.seudominio.filmesfavoritos.model.Filme
import br.com.seudominio.filmesfavoritos.util.Constantes

/**
 * ViewModel do formulário (criar e editar). Concentra TODA a lógica: validação,
 * decidir entre inserir e atualizar, e decidir quando a tela deve fechar.
 *
 * Os erros são expostos como o id do recurso de string (Int), não como texto: assim o
 * ViewModel não precisa de Context, e a Activity traduz o id para texto com getString().
 *
 * Alternativa para injetar o repository (útil em testes): criar uma
 * ViewModelProvider.Factory que receba o FilmeRepository no construtor.
 */
class FormularioFilmeViewModel : ViewModel() {

    private val repository = FilmeRepository()

    // Guarda para não recarregar os dados ao girar a tela (o ViewModel sobrevive à rotação)
    private var carregado = false
    private var filmeAtual: Filme? = null

    private val _filme = MutableLiveData<Filme?>()
    val filme: LiveData<Filme?> = _filme

    private val _modoEdicao = MutableLiveData(false)
    val modoEdicao: LiveData<Boolean> = _modoEdicao

    private val _erroNome = MutableLiveData<Int?>()
    val erroNome: LiveData<Int?> = _erroNome

    private val _erroUrl = MutableLiveData<Int?>()
    val erroUrl: LiveData<Int?> = _erroUrl

    private val _urlPreview = MutableLiveData<String?>()
    val urlPreview: LiveData<String?> = _urlPreview

    // Mensagem (Toast) mostrada ao fechar: "Filme salvo" ou "Filme excluído"
    private val _mensagemFinal = MutableLiveData<Int?>()
    val mensagemFinal: LiveData<Int?> = _mensagemFinal

    private val _fecharTela = MutableLiveData(false)
    val fecharTela: LiveData<Boolean> = _fecharTela

    /** Carrega o filme a editar. Com id == ID_NOVO_FILME, o formulário fica em modo criação. */
    fun carregar(id: Long) {
        if (carregado) return
        carregado = true

        if (id == Constantes.ID_NOVO_FILME) {
            _modoEdicao.value = false
            return
        }

        val existente = repository.buscarPorId(id)
        filmeAtual = existente
        _modoEdicao.value = existente != null
        _filme.value = existente
        _urlPreview.value = existente?.fotoUrl
    }

    /** Atualiza a pré-visualização. URL inválida ou vazia volta para o placeholder. */
    fun atualizarPreview(url: String) {
        val urlLimpa = url.trim()
        _urlPreview.value = if (urlLimpa.isNotEmpty() && urlValida(urlLimpa)) urlLimpa else null
    }

    // Quando o usuário volta a digitar, o erro daquele campo some (Nielsen, heurística 9).
    // O estado do erro mora aqui no ViewModel, senão ele reapareceria ao girar a tela.
    fun aoDigitarNome() {
        if (_erroNome.value != null) _erroNome.value = null
    }

    fun aoDigitarUrl() {
        if (_erroUrl.value != null) _erroUrl.value = null
    }

    fun salvar(nome: String, nota: Float, url: String) {
        if (_fecharTela.value == true) return // evita salvar duas vezes com toques rápidos

        val nomeLimpo = nome.trim()
        val urlLimpa = url.trim()

        val nomeValido = nomeLimpo.isNotEmpty()
        val urlOk = urlLimpa.isEmpty() || urlValida(urlLimpa) // a URL é opcional

        _erroNome.value = if (nomeValido) null else R.string.erro_nome_obrigatorio
        _erroUrl.value = if (urlOk) null else R.string.erro_url_invalida
        if (!nomeValido || !urlOk) return

        val fotoUrl = urlLimpa.ifEmpty { null }
        val notaValida = nota.coerceIn(0f, 5f)
        val atual = filmeAtual

        if (_modoEdicao.value == true && atual != null) {
            // copy() mantém o id e troca só o que mudou
            repository.atualizar(atual.copy(nome = nomeLimpo, nota = notaValida, fotoUrl = fotoUrl))
        } else {
            repository.inserir(Filme(nome = nomeLimpo, nota = notaValida, fotoUrl = fotoUrl))
        }
        finalizar(R.string.msg_filme_salvo)
    }

    /** Só faz sentido no modo edição (no modo criação ainda não existe filme para excluir). */
    fun excluir() {
        val atual = filmeAtual ?: return
        if (_modoEdicao.value != true) return
        if (_fecharTela.value == true) return

        repository.remover(atual.id)
        finalizar(R.string.msg_filme_excluido)
    }

    private fun finalizar(mensagem: Int) {
        _mensagemFinal.value = mensagem
        _fecharTela.value = true
    }

    private fun urlValida(url: String): Boolean {
        val temEsquema = url.startsWith("http://", ignoreCase = true) ||
            url.startsWith("https://", ignoreCase = true)
        val temEndereco = url.substringAfter("://").isNotEmpty()
        return temEsquema && temEndereco && !url.contains(' ')
    }
}
