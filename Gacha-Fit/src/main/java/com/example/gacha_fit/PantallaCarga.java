package com.example.gacha_fit;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PantallaCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_carga);
        // Inset handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🟡 Lanzar la animación (solo si usas ImageView en lugar de ProgressBar)
        ImageView hamster = findViewById(R.id.hamsterAnim);
        if (hamster.getDrawable() instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable anim = (AnimatedVectorDrawable) hamster.getDrawable();
            anim.start();
        }
        //Llamar al método que inicia la lógica del splash screen.
        SplashscreenStart();
    }
    /**
     * Método que controla el tiempo que la pantalla de inicio se muestra.
     * Después de un retraso de 2 segundos, redirige al usuario a la actividad principal.
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