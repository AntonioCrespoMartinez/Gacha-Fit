package com.example.gacha_fit.fragmentos;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gacha_fit.utiles.Musica;
import com.example.gacha_fit.R;
import com.example.gacha_fit.activity.AppActivity;
import com.example.gacha_fit.base_datos.FireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Exito extends Fragment {

    private TextView timerTextView;
    private Button actionButton;
    private CountDownTimer countDownTimer;
    private long startTime;
    private Handler handler;
    private Runnable chronometerRunnable;
    private MediaPlayer mediaPlayer;

    private String ejercicio;
    private String dificultad;
    private String medida;
    private int unidad;
    private int puntuacion;
    private String nombreUsuario;
    private String correo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exito, container, false);

        timerTextView = view.findViewById(R.id.timerTextView);
        actionButton = view.findViewById(R.id.actionButton);

        actionButton.setVisibility(View.GONE); //Botón oculto por defecto
        //Obtener parámetros recibidos en el bundle
        if (getArguments() != null) {
            ejercicio = getArguments().getString("ejercicio");
            medida = getArguments().getString("medida");
            unidad = getArguments().getInt("unidad");
            puntuacion = getArguments().getInt("puntuacion");
            dificultad = getArguments().getString("dificultad");
        }
        //Obtener correo y nombre de usuario desde Firebase Auth y Firestore
        correo = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        if (correo != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Usuarios")
                    .whereEqualTo("Correo", correo)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot usuarioDoc = queryDocumentSnapshots.getDocuments().get(0);
                            nombreUsuario = usuarioDoc.getString("Usuario");
                        }
                    });

        }
        //Iniciar el temporizador según la medida
        if ("seg".equals(medida)) {
            startCountdown();
        } else if ("reps".equals(medida)) {
            startChronometer();
        }

        return view;
    }
    /**
     * Inicia un temporizador regresivo basado en la cantidad de segundos indicada en unidad.
     * Al finalizar, muestra un botón para salir y registra la actividad en Firebase.
     */
    private void startCountdown() {
        countDownTimer = new CountDownTimer(unidad * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Tiempo restante: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timerTextView.setText("¡Tiempo finalizado!");

                //Reproducir alarma
                mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarma);
                //Aplicar el volumen de efectos de sonido configurado
                Musica.applySoundVolumeToMediaPlayer(mediaPlayer);
                mediaPlayer.start();
                //Aplicar el volumen de efectos de sonido configurado
                float soundVolume = Musica.getSoundVolume();
                mediaPlayer.setVolume(soundVolume, soundVolume);
                mediaPlayer.start();

                actionButton.setText("Salir");
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setOnClickListener(v -> {
                    //Detener la alarma cuando se pulsa el botón
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    
                    String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    FireBase fireBase = new FireBase();
                    fireBase.registroActividad(nombreUsuario, ejercicio + "-" + dificultad, fechaActual);
                    fireBase.registrarFitDex(dificultad, nombreUsuario, ejercicio);
                    fireBase.actualizarPuntos(correo, puntuacion);

                    //Volver a AppActivity
                    Activity activity = getActivity();
                    if (activity != null) {
                        Intent intent = new Intent(activity, AppActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        activity.finish();
                    }
                });
            }

        }.start();
    }
    /**
     * Inicia un cronómetro que muestra el tiempo transcurrido desde que se inició.
     * Muestra un botón para finalizar la sesión, registrar la actividad y actualizar puntos.
     */
    private void startChronometer() {
        handler = new Handler();
        startTime = System.currentTimeMillis();

        chronometerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                timerTextView.setText("Tiempo: " + (elapsed / 1000) + "s");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(chronometerRunnable);

        actionButton.setText("Finalizar");
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setOnClickListener(v -> {
            long totalElapsed = System.currentTimeMillis() - startTime;
            long tiempo = (totalElapsed / 1000);
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            FireBase fireBase = new FireBase();
            fireBase.registroActividad(nombreUsuario,ejercicio + "-" + dificultad,fechaActual + " en " + tiempo + " segundos");
            fireBase.registrarFitDex(dificultad,nombreUsuario,ejercicio);
            fireBase.actualizarPuntos(correo,puntuacion);
            Toast.makeText(getContext(), "Tiempo registrado: " + (totalElapsed / 1000) + "s", Toast.LENGTH_LONG).show();
            
            //Volver a AppActivity
            Activity activity = getActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, AppActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                activity.finish();
            }
        });
    }
    /**
     * Cancela los temporizadores y callbacks pendientes cuando la vista es destruida para evitar fugas.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (handler != null && chronometerRunnable != null) {
            handler.removeCallbacks(chronometerRunnable);
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}