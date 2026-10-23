// Arquivo de build do projeto (build.gradle → Project, como visto na aula).
// Aqui só declaramos os plugins; quem aplica de fato é o módulo :app.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
