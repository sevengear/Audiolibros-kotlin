package audiolibros.example.com.audiolibros.fragments

import android.os.Bundle
import android.preference.PreferenceFragment

import audiolibros.example.com.audiolibros.R

/**
 * Created by Miguel Á. Núñez on 15/02/2018.
 */

class PreferenciasFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }
}
