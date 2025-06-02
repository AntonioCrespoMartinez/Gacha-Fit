package com.example.gacha_fit.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gacha_fit.R;
import com.example.gacha_fit.fragmentos.FragmentoDetallesEjercicio;
import com.example.gacha_fit.fragmentos.ListaEjerciciosFragment;

public class EjercicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ejercicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        //Obtener los datos del Intent
        Bundle extras = getIntent().getExtras();
        
        if (savedInstanceState == null && extras != null) {
            //Verificar si debemos mostrar la lista de ejercicios
            if (extras.getBoolean("mostrarListaEjercicios", false)) {
                //Mostrar ListaEjerciciosFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ListaEjerciciosFragment())
                        .commit();
            } else {
                //Comportamiento normal: Cargar el fragmento EjercicioDetalleFragment
                //Crear el fragmento y pasarle los datos
                FragmentoDetallesEjercicio fragmento = new FragmentoDetallesEjercicio();
                fragmento.setArguments(extras);
                
                //Reemplazar el contenido principal con el fragmento
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragmento)
                        .commit();
            }
        }
    }
    
    /**
     * Método para cargar un fragmento en el contenedor principal
     * @param fragment Fragmento a cargar
     */
    public void cambiarFragmento(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}