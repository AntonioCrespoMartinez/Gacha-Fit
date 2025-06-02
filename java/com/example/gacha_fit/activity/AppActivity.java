package com.example.gacha_fit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.gacha_fit.utiles.Musica;
import com.example.gacha_fit.fragmentos.Clasificacion;
import com.example.gacha_fit.fragmentos.FitDex;
import com.example.gacha_fit.fragmentos.FragmentoGacha;
import com.example.gacha_fit.R;
import com.example.gacha_fit.fragmentos.Perfil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppActivity extends AppCompatActivity {
    private MaterialToolbar menuArriba;
    private BottomNavigationView menu;
    private String mensaje = "Se ha cerrado sesión correctamente";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app);
        Musica.loadVolumeSettings(this);
        Musica.startMusic(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Inicialización del menu de arriba
        menuArriba = findViewById(R.id.menuArriba);
        //Configura el  MaterialToolBar como la ActionBar
        setSupportActionBar(menuArriba);
        //Inicialización del menú de abajo
        menu = findViewById(R.id.menu);
        //Listener del menú que comprueba que Item esta seleccionado
        menu.setOnItemSelectedListener(item ->{
            //Llamada al metódo para cambiar de Fragment
            onOptionsItemSelected(item);
            return true;
        });
        //Carga el fragmento por defecto si no hay estado guardado
        if (savedInstanceState == null) {
            cambiarFragmento(new FragmentoGacha());
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Musica.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Musica.resumeMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Musica.stopMusic();
    }

    /**
     * Infla el menú superior con los ítems definidos en el archivo XML.
     *
     * @param menu Objeto del menú en el que se insertan los ítems.
     * @return true si el menú se muestra correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflador del menu
        getMenuInflater().inflate(R.menu.menu_arriba,menu);
        return true;
    }
    /**
     * Maneja los eventos cuando se selecciona un ítem del menú.
     * Cambia de fragmento o realiza otras acciones como cerrar sesión.
     *
     * @param item Ítem seleccionado por el usuario.
     * @return true si el evento fue manejado correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Variable utilizada para comprobafr el id del item seleccionado
        int id = item.getItemId();
        if (id == R.id.gacha){
            cambiarFragmento(new FragmentoGacha());
            return true;
        }else if (id == R.id.fitDex){
            cambiarFragmento(new FitDex());
            return true;
        } else if (id == R.id.configuracion) {
            cambiarFragmento(new Clasificacion());
            return true;
        } else if (id == R.id.perfil) {
            cambiarFragmento(new Perfil());
        } else if (id == R.id.log_out) {
            //Limpia el estado de "mantener sesión"
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("mantenerSesion", false);
            editor.apply();
            Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Método auxiliar para cambiar entre fragmentos dentro del contenedor especificado.
     *
     * @param fragment Fragmento que se desea mostrar.
     */
    public void cambiarFragmento(Fragment fragment) {
        //Comprobar si hay una animación en curso
        if (FragmentoGacha.animacionEnCurso) {
            return;
        }
        
        //Solo cambiar el fragmento si no hay animación en curso
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }
}