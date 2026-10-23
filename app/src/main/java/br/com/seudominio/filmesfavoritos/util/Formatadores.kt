package br.com.seudominio.filmesfavoritos.util

import java.text.NumberFormat
import java.util.Locale

object Formatadores {

    /**
     * Formata a nota no padrão brasileiro, com 1 casa decimal e vírgula (ex.: 4.5 vira "4,5").
     * Mesma ideia do NumberFormat visto na aula para moeda, só que com getNumberInstance.
     * Criamos o NumberFormat a cada chamada porque ele não é seguro para uso em várias threads.
     */
    fun formatarNota(nota: Float): String {
        val formato = NumberFormat.getNumberInstance(Locale("pt", "BR"))
        formato.minimumFractionDigits = 1
        formato.maximumFractionDigits = 1
        return formato.format(nota)
    }
}
