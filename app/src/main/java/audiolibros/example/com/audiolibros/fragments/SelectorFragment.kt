package audiolibros.example.com.audiolibros.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import audiolibros.example.com.audiolibros.AdaptadorLibrosFiltro
import audiolibros.example.com.audiolibros.Aplicacion
import audiolibros.example.com.audiolibros.Libro
import audiolibros.example.com.audiolibros.MainActivity
import audiolibros.example.com.audiolibros.R

/**
 * Created by Miguel Á. Núñez on 29/01/2018.
 */

class SelectorFragment : Fragment(), Animator.AnimatorListener {
    private var actividad: Activity? = null
    private var recyclerView: RecyclerView? = null
    private var adaptador: AdaptadorLibrosFiltro? = null
    private var listaLibros: List<Libro>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.actividad = activity
        val app = actividad!!.application as Aplicacion
        adaptador = app.adaptador
        listaLibros = app.listaLibros
    }

    override fun onCreateView(inflador: LayoutInflater, contenedor: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vista = inflador.inflate(R.layout.fragment_selector, contenedor, false)
        setHasOptionsMenu(true)
        recyclerView = vista.findViewById(R.id.recycler_view)
        val animator = DefaultItemAnimator()
        animator.addDuration = 2000
        animator.moveDuration = 2000
        recyclerView!!.itemAnimator = animator
        recyclerView!!.layoutManager = GridLayoutManager(actividad, 2)
        recyclerView!!.adapter = adaptador
        adaptador!!.setOnItemClickListener(View.OnClickListener { v ->
            (actividad as MainActivity).mostrarDetalle(
                    adaptador!!.getItemId(recyclerView!!.getChildAdapterPosition(v)).toInt())
        })

        adaptador!!.setOnItemLongClickListener(View.OnLongClickListener { v ->
            val id = recyclerView!!.getChildAdapterPosition(v)
            val menu = AlertDialog.Builder(actividad)
            val opciones = arrayOf<CharSequence>("Compartir", "Borrar ", "Insertar")
            menu.setItems(opciones) { dialog, opcion ->
                when (opcion) {
                    //Compartir
                    0 -> {
                        val libro = listaLibros!![id]
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "text/plain"
                        i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo)
                        i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio)
                        startActivity(Intent.createChooser(i, "Compartir"))
                    }
                    //Borrar
                    1 -> Snackbar.make(v, "¿Estás seguro?", Snackbar.LENGTH_LONG).setAction("SI") {
                        val anim = AnimatorInflater.loadAnimator(actividad, R.animator.menguar)
                        anim.addListener(this@SelectorFragment)
                        anim.setTarget(v)
                        anim.start()
                        adaptador!!.borrar(id)
                        //adaptador.notifyDataSetChanged();
                    }.show()
                    //Insertar
                    2 -> {
                        val posicion = recyclerView!!.getChildLayoutPosition(v)
                        adaptador!!.insertar(adaptador!!.getItem(posicion))
                        adaptador!!.notifyItemInserted(0)
                        //adaptador.notifyDataSetChanged();
                        Snackbar.make(v, "Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK") { }
                                .show()
                    }
                }
            }
            menu.create().show()
            true
        })

        return vista
    }

    override fun onResume() {
        (activity as MainActivity).mostrarElementos(true)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_selector, menu)
        val searchItem = menu.findItem(R.id.menu_buscar)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                adaptador!!.setBusqueda(query)
                adaptador!!.notifyDataSetChanged()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                adaptador!!.setBusqueda("")
                adaptador!!.notifyDataSetChanged()
                return true // Para permitir cierre
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true // Para permitir expansión
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_ultimo) {
            (actividad as MainActivity).irUltimoVisitado()
            return true
        } else if (id == R.id.menu_buscar) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAnimationStart(animation: Animator) {

    }

    override fun onAnimationEnd(animation: Animator) {
        adaptador!!.notifyDataSetChanged()
    }

    override fun onAnimationCancel(animation: Animator) {

    }

    override fun onAnimationRepeat(animation: Animator) {

    }
}
