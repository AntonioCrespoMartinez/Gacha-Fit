package com.example.gacha_fit.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gacha_fit.fragmentos.PantallaLogin;
import com.example.gacha_fit.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Si es la primera vez que se crea la actividad, se carga el fragmento de login
        if (savedInstanceState == null) {
            cambiarFragmento(new PantallaLogin());
        }
    }
    /**
     * Reemplaza el fragmento actual con el fragmento proporcionado.
     *
     * @param fragment Fragmento que se desea mostrar en el contenedor.
     */
    public void cambiarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }
}