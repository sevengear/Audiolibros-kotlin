package audiolibros.example.com.audiolibros;

import android.app.Activity;
import android.os.Bundle;

import audiolibros.example.com.audiolibros.fragments.PreferenciasFragment;

/**
 * Created by Miguel Á. Núñez on 15/02/2018.
 */

public class PreferenciasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id. content, new PreferenciasFragment()).commit();
    }
}
