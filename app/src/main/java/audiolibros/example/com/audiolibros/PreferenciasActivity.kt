package audiolibros.example.com.audiolibros

import android.app.Activity
import android.os.Bundle

import audiolibros.example.com.audiolibros.fragments.PreferenciasFragment

/**
 * Created by Miguel Á. Núñez on 15/02/2018.
 */

class PreferenciasActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, PreferenciasFragment()).commit()
    }
}
