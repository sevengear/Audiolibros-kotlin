package audiolibros.example.com.audiolibros

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

/**
 * Created by Miguel Á. Núñez on 23/04/2018.
 */
class LibroStorage(private val context: Context) {

    private val preference: SharedPreferences
        get() = context.getSharedPreferences(PREF_AUDIOLIBROS, Context.MODE_PRIVATE)

    val lastBook: Int
        get() = preference.getInt(KEY_ULTIMO_LIBRO, -1)

    fun hasLastBook(): Boolean {
        return preference.contains(KEY_ULTIMO_LIBRO)
    }

    companion object {
        val PREF_AUDIOLIBROS = "com.example.audiolibros_internal"
        val KEY_ULTIMO_LIBRO = "ultimo"
    }
} // No sería necesario añadir método setLastBook()

