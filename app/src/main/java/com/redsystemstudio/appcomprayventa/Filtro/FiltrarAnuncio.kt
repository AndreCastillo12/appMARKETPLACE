package com.redsystemstudio.appcomprayventa.Filtro

import android.widget.Filter
import com.redsystemstudio.appcomprayventa.Adaptadores.AdaptadorAnuncio
import com.redsystemstudio.appcomprayventa.Modelo.ModeloAnuncio
import java.util.Locale

class FiltrarAnuncio(
    private val adaptador: AdaptadorAnuncio,
    private val filtroLista: ArrayList<ModeloAnuncio>
) : Filter() {
    override fun performFiltering(filtro: CharSequence?): FilterResults {
        val resultados = FilterResults()

        if (!filtro.isNullOrEmpty()) {
            val filtroMayuscula = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelo = filtroLista.filter {
                it.marca.uppercase(Locale.getDefault()).contains(filtroMayuscula) ||
                        it.categoria.uppercase(Locale.getDefault()).contains(filtroMayuscula) ||
                        it.condicion.uppercase(Locale.getDefault()).contains(filtroMayuscula) ||
                        it.titulo.uppercase(Locale.getDefault()).contains(filtroMayuscula)
            }
            resultados.count = filtroModelo.size
            resultados.values = filtroModelo
        } else {
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults) {
        @Suppress("UNCHECKED_CAST")
        adaptador.anuncioArrayList = resultados.values as ArrayList<ModeloAnuncio>
        adaptador.notifyDataSetChanged()
    }
}
