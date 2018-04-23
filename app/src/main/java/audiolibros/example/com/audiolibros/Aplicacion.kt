package audiolibros.example.com.audiolibros

import android.app.Application
import android.graphics.Bitmap
import android.support.v4.util.LruCache

import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * Created by Miguel Á. Núñez on 29/01/2018.
 */

class Aplicacion : Application() {
    var listaLibros: MutableList<Libro>? = null
    var adaptador: AdaptadorLibrosFiltro? = null

    override fun onCreate() {
        super.onCreate()
        listaLibros = Libro.ejemploLibros()
        adaptador = AdaptadorLibrosFiltro(this, listaLibros!!)
        colaPeticiones = Volley.newRequestQueue(this)
        lectorImagenes = ImageLoader(colaPeticiones, object : ImageLoader.ImageCache {
            private val cache = LruCache<String, Bitmap>(10)

            override fun putBitmap(url: String, bitmap: Bitmap) {
                cache.put(url, bitmap)
            }

            override fun getBitmap(url: String): Bitmap? {
                return cache.get(url)
            }
        })
    }

    fun getLectorImagenes(): ImageLoader? {
        return lectorImagenes
    }


    companion object {
        var colaPeticiones: RequestQueue? = null
            private set
        var lectorImagenes: ImageLoader? = null
            private set
    }
}
