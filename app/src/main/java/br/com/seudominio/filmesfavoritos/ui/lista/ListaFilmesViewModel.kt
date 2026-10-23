package br.com.seudominio.filmesfavoritos.ui.lista

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import br.com.seudominio.filmesfavoritos.data.repository.FilmeRepository
import br.com.seudominio.filmesfavoritos.model.Filme

/**
 * ViewModel da tela de lista. Não conhece Activity, View nem Context:
 * só guarda o estado (a lista) e o expõe para a tela observar.
 */
class ListaFilmesViewModel : ViewModel() {

    // Padrão MVVM: o MutableLiveData é privado (só o ViewModel escreve);
    // a tela enxerga apenas o LiveData somente-leitura.
    private val repository = FilmeRepository()

    private val _filmes = MutableLiveData<List<Filme>>()
    val filmes: LiveData<List<Filme>> = _filmes

    // Derivado da lista: a tela só precisa saber "está vazia?" para mostrar o estado vazio.
    // Como _filmes começa sem valor, nada é exibido antes da primeira carga (evita "piscar").
    val listaVazia: LiveData<Boolean> = _filmes.map { it.isEmpty() }

    fun carregarFilmes() {
        _filmes.value = repository.listar()
    }
}
