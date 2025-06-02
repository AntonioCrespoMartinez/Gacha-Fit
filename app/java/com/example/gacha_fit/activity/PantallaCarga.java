package com.example.gacha_fit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gacha_fit.base_datos.FireBase;
import com.example.gacha_fit.R;

public class PantallaCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_carga);
        //Ajusta los insets para adaptar la UI a las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Carga inicial de ejercicios desde Firebase
        FireBase fireBase = new FireBase();
        fireBase.subirEjercicios();
        //Llamar al método que inicia la lógica del splash screen.
        SplashscreenStart();
    }
    /**
     * Método que controla la duración de la pantalla de carga.
     * Después de 2 segundos (2000 ms), inicia la actividad MainActivity y cierra esta pantalla.
     */
    public void SplashscreenStart(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PantallaCarga.this, MainActivity.class));
                finish();
            }
            //Dilay que podemos aumentar y disminuir a nuestro gusto
        },2000);

    }
}