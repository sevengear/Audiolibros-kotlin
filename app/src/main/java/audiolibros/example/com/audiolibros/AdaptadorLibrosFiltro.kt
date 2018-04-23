package audiolibros.example.com.audiolibros

import android.content.Context

import java.util.ArrayList

/**
 * Created by Miguel Á. Núñez on 29/01/2018.
 */

class AdaptadorLibrosFiltro(contexto: Context, private val listaSinFiltro: MutableList<Libro> // Lista con todos los libros
) : AdaptadorLibros(contexto, listaSinFiltro) {
    private var indiceFiltro: MutableList<Int>? = null // Índice en listaSinFiltro de
    // Cada elemento de listaLibros
    private var busqueda = "" // Búsqueda sobre autor o título
    private var genero = "" // Género seleccionado
    private var novedad = false // Si queremos ver solo novedades
    private var leido = false // Si queremos ver solo leidos

    init {
        recalculaFiltro()
    }

    fun setBusqueda(busqueda: String) {
        this.busqueda = busqueda.toLowerCase()
        recalculaFiltro()
    }

    fun setGenero(genero: String) {
        this.genero = genero
        recalculaFiltro()
    }

    fun setNovedad(novedad: Boolean) {
        this.novedad = novedad
        recalculaFiltro()
    }

    fun setLeido(leido: Boolean) {
        this.leido = leido
        recalculaFiltro()
    }

    fun recalculaFiltro() {
        listaLibros = ArrayList()
        indiceFiltro = ArrayList()
        for (i in listaSinFiltro.indices) {
            val libro = listaSinFiltro[i]
            if ((libro.titulo.toLowerCase().contains(busqueda) || libro.autor.toLowerCase().contains(busqueda))
                    && libro.genero.startsWith(genero)
                    && (!novedad || novedad && libro.novedad!!)
                    && (!leido || leido && libro.leido!!)) {
                listaLibros.add(libro)
                indiceFiltro!!.add(i)
            }
        }
    }

    fun getItem(posicion: Int): Libro {
        return listaSinFiltro[indiceFiltro!![posicion]]
    }

    override fun getItemId(posicion: Int): Long {
        return indiceFiltro!![posicion].toLong()
    }

    fun borrar(posicion: Int) {
        listaSinFiltro.removeAt(getItemId(posicion).toInt())
        recalculaFiltro()
    }

    fun insertar(libro: Libro) {
        listaSinFiltro.add(0, libro)
        recalculaFiltro()
    }
}