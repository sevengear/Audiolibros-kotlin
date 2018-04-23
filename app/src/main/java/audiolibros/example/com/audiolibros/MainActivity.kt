package audiolibros.example.com.audiolibros

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Menu
import android.view.MenuItem
import audiolibros.example.com.audiolibros.R.id.*

import audiolibros.example.com.audiolibros.fragments.DetalleFragment
import audiolibros.example.com.audiolibros.fragments.PreferenciasFragment
import audiolibros.example.com.audiolibros.fragments.SelectorFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var adaptador: AdaptadorLibrosFiltro? = null
    //private var appBarLayout: AppBarLayout? = null
    //private var tabs: TabLayout? = null
    //private var drawer: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val idContenedor = if (findViewById<View>(R.id.contenedor_pequeno) != null)
            R.id.contenedor_pequeno
        else
            R.id.contenedor_izquierdo

        val primerFragment = SelectorFragment()
        fragmentManager.beginTransaction().add(idContenedor, primerFragment)
                .commit()

        //val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //appBarLayout = findViewById(R.id.appBarLayout)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Navigation Drawer
        //drawer_layout = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawer_layout.addDrawerListener(toggle!!)
        toggle!!.syncState()
        toggle!!.toolbarNavigationClickListener = View.OnClickListener { onBackPressed() }
        //val navigationView = findViewById<NavigationView>(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)

        //val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.onClick { irUltimoVisitado() }

        adaptador = (applicationContext as Aplicacion).adaptador

        //Pestañas
        //tabs = findViewById(R.id.tabs)
        tabs!!.addTab(tabs!!.newTab().setText("Todos"))
        tabs!!.addTab(tabs!!.newTab().setText("Nuevos"))
        tabs!!.addTab(tabs!!.newTab().setText("Leidos"))
        tabs!!.tabMode = TabLayout.MODE_SCROLLABLE
        tabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                //Todos
                    0 -> {
                        adaptador!!.setNovedad(false)
                        adaptador!!.setLeido(false)
                    }
                //Nuevos
                    1 -> {
                        adaptador!!.setNovedad(true)
                        adaptador!!.setLeido(false)
                    }
                //Leidos
                    2 -> {
                        adaptador!!.setNovedad(false)
                        adaptador!!.setLeido(true)
                    }
                }
                adaptador!!.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun mostrarDetalle(id: Int) {
        val detalleFragment = fragmentManager.findFragmentById(R.id.detalle_fragment) as? DetalleFragment
        if (detalleFragment != null) {
            detalleFragment.ponInfoLibro(id)
        } else {
            val nuevoFragment = DetalleFragment()
            val args = Bundle()
            args.putInt(DetalleFragment.ARG_ID_LIBRO, id)
            nuevoFragment.arguments = args
            val transaccion = fragmentManager.beginTransaction()
            transaccion.replace(R.id.contenedor_pequeno, nuevoFragment)
            transaccion.addToBackStack(null)
            transaccion.commit()
        }

        val pref = getSharedPreferences(
                "com.example.audiolibros_internal", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("ultimo", id)
        editor.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_preferencias) {
            abrePreferencias()
            return true
        } else if (id == R.id.menu_acerca) {
            alert(Appcompat, "Mensaje de Acerca De") {
                positiveButton(android.R.string.ok) {}
            }.show()
            /*val builder = AlertDialog.Builder(this)
            builder.setMessage("Mensaje de Acerca De")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.create().show()*/
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun irUltimoVisitado() {
        val pref = getSharedPreferences(
                "com.example.audiolibros_internal", Context.MODE_PRIVATE)
        val id = pref.getInt("ultimo", -1)
        if (id >= 0) {
            mostrarDetalle(id)
        } else {
            toast("Sin última vista")
            //Toast.makeText(this, "Sin última vista", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            nav_todos -> {
                adaptador!!.setGenero("")
                adaptador!!.notifyDataSetChanged()
            }
            nav_epico -> {
                adaptador!!.setGenero(Libro.G_EPICO)
                adaptador!!.notifyDataSetChanged()
            }
            nav_XIX -> {
                adaptador!!.setGenero(Libro.G_S_XIX)
                adaptador!!.notifyDataSetChanged()
            }
            nav_suspense -> {
                adaptador!!.setGenero(Libro.G_SUSPENSE)
                adaptador!!.notifyDataSetChanged()
            }
        }
        /*if (id == R.id.nav_todos) {
            adaptador!!.setGenero("")
            adaptador!!.notifyDataSetChanged()
        } else if (id == R.id.nav_epico) {
            adaptador!!.setGenero(Libro.G_EPICO)
            adaptador!!.notifyDataSetChanged()
        } else if (id == R.id.nav_XIX) {
            adaptador!!.setGenero(Libro.G_S_XIX)
            adaptador!!.notifyDataSetChanged()
        } else if (id == R.id.nav_suspense) {
            adaptador!!.setGenero(Libro.G_SUSPENSE)
            adaptador!!.notifyDataSetChanged()
        }*/
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        //drawer_layout = findViewById(R.id.drawer_layout)
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun mostrarElementos(mostrar: Boolean) {
        appBarLayout!!.setExpanded(mostrar)
        toggle!!.isDrawerIndicatorEnabled = mostrar
        if (mostrar) {
            drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            tabs!!.visibility = View.VISIBLE
        } else {
            tabs!!.visibility = View.GONE
            drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    fun abrePreferencias() {
        val idContenedor = if (findViewById<View>(R.id.contenedor_pequeno) != null)
            R.id.contenedor_pequeno
        else
            R.id.contenedor_izquierdo
        val prefFragment = PreferenciasFragment()
        fragmentManager.beginTransaction()
                .replace(idContenedor, prefFragment)
                .addToBackStack(null)
                .commit()
    }
}
