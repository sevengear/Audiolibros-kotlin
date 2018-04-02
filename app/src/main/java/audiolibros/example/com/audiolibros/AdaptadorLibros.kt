package audiolibros.example.com.audiolibros

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader

/**
 * Created by Miguel Á. Núñez on 29/01/2018.
 */

open class AdaptadorLibros(contexto: Context, var listaLibros: MutableList<Libro> //Lista de libros a visualizar
) : RecyclerView.Adapter<AdaptadorLibros.ViewHolder>() {
    var inflador: LayoutInflater = contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //Crea Layouts a partir del XML protected
    private var onClickListener: View.OnClickListener? = null
    private var onLongClickListener: View.OnLongClickListener? = null

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var portada: ImageView = itemView.findViewById(R.id.portada)
        var titulo: TextView = itemView.findViewById(R.id.titulo)

    } // Creamos el ViewHolder con las vista de un elemento sin personalizar

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // Inflamos la vista desde el xml
        val v = inflador.inflate(R.layout.elemento_selector, null)
        v.setOnClickListener(onClickListener)
        v.setOnLongClickListener(onLongClickListener)
        return ViewHolder(v)
    } // Usando como base el ViewHolder y lo personalizamos

    override fun onBindViewHolder(holder: ViewHolder, posicion: Int) {
        val libro = listaLibros[posicion]
        //holder.portada.setImageResource(libro.recursoImagen);
        holder.titulo.text = libro.titulo
        //Aplicacion aplicacion = (Aplicacion) contexto.getApplicationContext();
        Aplicacion.lectorImagenes?.get(libro.urlImagen, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                val bitmap = response.bitmap
                if (bitmap != null) {
                    holder.portada.setImageBitmap(bitmap)
                    val palette = Palette.from(bitmap).generate()
                    holder.itemView.setBackgroundColor(palette.getLightMutedColor(0))
                    holder.titulo.setBackgroundColor(palette.getLightVibrantColor(0))
                    holder.portada.invalidate()
                }
            }

            override fun onErrorResponse(error: VolleyError) {
                holder.portada.setImageResource(R.drawable.books)
            }
        })
        holder.itemView.scaleX = 1f
        holder.itemView.scaleY = 1f
    }

    // Indicamos el número de elementos de la lista
    override fun getItemCount(): Int {
        return listaLibros.size
    }

    fun setOnItemClickListener(onClickListener: View.OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOnItemLongClickListener(onLongClickListener: View.OnLongClickListener) {
        this.onLongClickListener = onLongClickListener
    }
}