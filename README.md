# Filmes Favoritos

App Android (Kotlin + XML) para visualizar, adicionar, editar e excluir filmes favoritos, com nome, nota e foto.

**Tecnologias:** View Binding, MVVM (ViewModel + LiveData), RecyclerView (ListAdapter + DiffUtil), DAO em memória, Material Design 3 e Coil para carregar as fotos.

## Como abrir

1. No Android Studio: **File > Open** e escolha a pasta `FilmesFavoritos`.
2. Aguarde o **Sync**. Se pedir, aceite baixar o Gradle 8.9. Se o Studio sugerir atualizar o Android Gradle Plugin, pode aceitar.
3. Rode no emulador ou no celular (minSdk 24).

> O projeto não inclui `gradlew` nem `gradle-wrapper.jar`. O Android Studio usa o Gradle indicado em `gradle/wrapper/gradle-wrapper.properties`. Para gerar o wrapper pelo terminal, rode `gradle wrapper` (com o Gradle instalado) dentro da pasta do projeto.

## Trocar o pacote

O pacote padrão é `br.com.seudominio.filmesfavoritos`. Para trocar, use no Android Studio **Refactor > Rename** sobre cada pasta do pacote e depois ajuste `namespace` e `applicationId` em `app/build.gradle.kts`.

## Estrutura

```
app/src/main/java/br/com/seudominio/filmesfavoritos
├── model/Filme.kt
├── data/dao/            FilmeDao.kt, FilmeDaoImpl.kt
├── data/repository/     FilmeRepository.kt
├── ui/lista/            MainActivity, ListaFilmesViewModel, FilmeAdapter
├── ui/formulario/       FormularioFilmeActivity, FormularioFilmeViewModel
└── util/                Constantes.kt, Formatadores.kt
```

## Fluxo MVVM

```
  ┌─────────────────────┐   eventos (cliques, texto)   ┌──────────────────────┐
  │  View               │ ───────────────────────────► │  ViewModel           │
  │  Activity + XML     │                              │  validação, estado   │
  │  (View Binding)     │ ◄─────────────────────────── │  (MutableLiveData)   │
  └─────────────────────┘   LiveData.observe()         └──────────┬───────────┘
                                                                  │ chamadas
                                                       ┌──────────▼───────────┐
                                                       │  FilmeRepository     │
                                                       └──────────┬───────────┘
                                                                  │
                                                       ┌──────────▼───────────┐
                                                       │  FilmeDao (interface)│
                                                       │  FilmeDaoImpl        │
                                                       │  lista em memória    │
                                                       └──────────────────────┘
```

- A **Activity** só observa o estado, desenha a tela e repassa os eventos.
- O **ViewModel** decide tudo: valida, insere ou atualiza, e manda fechar a tela. Não guarda `Context`, `View` nem binding.
- O **DAO** é uma interface, então dá para trocar a implementação em memória por Room sem mexer no ViewModel.

## Decisões de projeto

- **Dados de exemplo:** as 3 fotos iniciais vêm do `picsum.photos` (imagens aleatórias, não os pôsteres reais).
- **Dados em memória:** os filmes somem quando o processo do app é encerrado. Persistência (Room) ficou fora do escopo.
- **Lista recarrega no `onResume`:** cada Activity tem o próprio ViewModel, e a fonte compartilhada é o DAO.
- **`targetSdk = 34`:** a partir do 35 o Android 15 força o modo edge-to-edge, que exige tratar insets das barras do sistema (assunto fora do conteúdo das aulas). O `compileSdk` é 35.
- **Heurísticas de Nielsen:** os comentários nos XML e no Kotlin indicam qual heurística cada parte atende (1, 3, 4, 5 e 9).

## Erros comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| `ActivityMainBinding` não encontrado | Binding ainda não gerado | **File > Sync Project with Gradle Files** e depois **Build > Rebuild Project**. Confira `viewBinding = true` em `app/build.gradle.kts`. |
| Foto não carrega e aparece o ícone de erro | Falta a permissão INTERNET ou a URL é `http://` sem suporte do servidor | Confira `<uses-permission android:name="android.permission.INTERNET" />` no manifest e prefira URLs `https://`. |
| A lista não atualiza ao voltar do formulário | `carregarFilmes()` fora do `onResume`, ou o DAO devolvendo a própria lista mutável | Mantenha a chamada no `onResume` e o `toList()` no `listar()`. |
| Erro de pacote ao rodar após renomear | `namespace` e `applicationId` diferentes do pacote das pastas | Alinhe os três em `app/build.gradle.kts` e nas pastas. |
